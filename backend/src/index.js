require("dotenv").config();

const cors = require("cors");
const express = require("express");
const helmet = require("helmet");
const jwt = require("jsonwebtoken");
const { Pool } = require("pg");

const app = express();
const port = process.env.PORT || 3000;
const jwtSecret = process.env.JWT_SECRET || "dev_secret_change_me";

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === "production" ? { rejectUnauthorized: false } : false,
});

const tables = {
  usuarios: "usuarios",
  oficinas: "oficinas",
  tipos_documento: "tipos_documento",
  administrados: "administrados",
  personal: "personal",
  direcciones: "direcciones",
  expedientes: "expedientes",
  documentos_ingresados: "documentos_ingresados",
  hojas_ruta: "hojas_ruta",
  archivo_fisico: "archivo_fisico",
  actas_archivamiento: "actas_archivamiento",
};

app.use(helmet());
app.use(cors());
app.use(express.json({ limit: "2mb" }));

app.get("/", (_req, res) => {
  res.json({
    app: "Smart-Gov Sync API",
    grupo: "Grupo 4",
    endpoints: ["POST /login", "GET /sincronizacion", "POST /sync-data", "CRUD /:entidad"],
  });
});

app.get("/health", async (_req, res) => {
  const result = await pool.query("SELECT NOW() AS now");
  res.json({ ok: true, databaseTime: result.rows[0].now });
});

app.post("/login", async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ message: "Email y password son obligatorios" });
  }

  const result = await pool.query(
    "SELECT uuid, nombre, email, rol, password_hash FROM usuarios WHERE email = $1 AND deleted_at IS NULL LIMIT 1",
    [email.toLowerCase()]
  );
  const user = result.rows[0];
  if (!user || user.password_hash !== password) {
    return res.status(401).json({ message: "Credenciales invalidas" });
  }

  const token = jwt.sign(
    { sub: user.uuid, email: user.email, rol: user.rol },
    jwtSecret,
    { expiresIn: process.env.JWT_EXPIRES_IN || "8h" }
  );

  res.json({ token, user: { uuid: user.uuid, nombre: user.nombre, email: user.email, rol: user.rol } });
});

app.get("/sincronizacion", authenticate, async (req, res) => {
  const since = Number(req.query.since || 0);
  const payload = {};

  for (const [entity, table] of Object.entries(tables)) {
    const result = await pool.query(
      `SELECT * FROM ${table} WHERE updated_at > $1 ORDER BY updated_at ASC`,
      [new Date(since)]
    );
    payload[entity] = result.rows;
  }

  res.json({ serverTime: Date.now(), strategy: "last-write-wins", data: payload });
});

app.post("/sync-data", authenticate, async (req, res) => {
  const data = req.body.data || {};
  const summary = {};

  for (const [entity, rows] of Object.entries(data)) {
    if (!tables[entity] || !Array.isArray(rows)) continue;
    summary[entity] = 0;
    for (const row of rows) {
      await upsertRow(tables[entity], row);
      summary[entity] += 1;
    }
  }

  res.json({ ok: true, conflictStrategy: "last-write-wins", summary });
});

app.get("/:entidad", authenticate, async (req, res) => {
  const table = resolveTable(req.params.entidad, res);
  if (!table) return;
  const result = await pool.query(`SELECT * FROM ${table} WHERE deleted_at IS NULL ORDER BY updated_at DESC`);
  res.json({ data: result.rows });
});

app.post("/:entidad", authenticate, async (req, res) => {
  const table = resolveTable(req.params.entidad, res);
  if (!table) return;
  const row = { ...req.body, updated_at: new Date() };
  const saved = await upsertRow(table, row);
  res.status(201).json({ data: saved });
});

app.put("/:entidad/:uuid", authenticate, async (req, res) => {
  const table = resolveTable(req.params.entidad, res);
  if (!table) return;
  const row = { ...req.body, uuid: req.params.uuid, updated_at: new Date() };
  const saved = await upsertRow(table, row);
  res.json({ data: saved });
});

app.delete("/:entidad/:uuid", authenticate, async (req, res) => {
  const table = resolveTable(req.params.entidad, res);
  if (!table) return;
  await pool.query(`UPDATE ${table} SET deleted_at = NOW(), updated_at = NOW() WHERE uuid = $1`, [req.params.uuid]);
  res.json({ ok: true });
});

app.use((error, _req, res, _next) => {
  console.error(error);
  res.status(500).json({ message: "Error interno", detail: error.message });
});

function authenticate(req, res, next) {
  const header = req.headers.authorization || "";
  const token = header.startsWith("Bearer ") ? header.slice(7) : null;
  if (!token) {
    return res.status(401).json({ message: "Token JWT requerido" });
  }
  try {
    req.user = jwt.verify(token, jwtSecret);
    next();
  } catch (_error) {
    res.status(401).json({ message: "Token invalido" });
  }
}

function resolveTable(entity, res) {
  const table = tables[entity];
  if (!table) {
    res.status(404).json({ message: "Entidad no permitida" });
    return null;
  }
  return table;
}

async function upsertRow(table, row) {
  const clean = removeUndefined(row);
  if (!clean.uuid) {
    clean.uuid = cryptoRandomUuid();
  }
  if (!clean.updated_at) {
    clean.updated_at = new Date();
  }
  if (clean.sync_status === undefined) {
    clean.sync_status = "SYNCED";
  }

  const columns = Object.keys(clean).filter((key) => key !== "id");
  const values = columns.map((key) => clean[key]);
  const placeholders = columns.map((_, index) => `$${index + 1}`);
  const updates = columns
    .filter((key) => key !== "uuid")
    .map((key) => `${key} = EXCLUDED.${key}`)
    .join(", ");

  const query = `
    INSERT INTO ${table} (${columns.join(", ")})
    VALUES (${placeholders.join(", ")})
    ON CONFLICT (uuid) DO UPDATE SET ${updates}
    WHERE ${table}.updated_at <= EXCLUDED.updated_at
    RETURNING *
  `;
  const result = await pool.query(query, values);
  return result.rows[0] || null;
}

function removeUndefined(row) {
  return Object.fromEntries(Object.entries(row).filter(([, value]) => value !== undefined));
}

function cryptoRandomUuid() {
  return require("crypto").randomUUID();
}

app.listen(port, () => {
  console.log(`Smart-Gov Sync API running on port ${port}`);
});
