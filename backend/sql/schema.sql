CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS oficinas (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS tipos_documento (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS administrados (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  nombres TEXT NOT NULL,
  documento_identidad TEXT NOT NULL UNIQUE,
  telefono TEXT,
  email TEXT,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS personal (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  nombres TEXT NOT NULL,
  cargo TEXT NOT NULL,
  oficina_id BIGINT NOT NULL REFERENCES oficinas(id),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  nombre TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  rol TEXT NOT NULL,
  personal_id BIGINT REFERENCES personal(id),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS direcciones (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  administrado_id BIGINT NOT NULL REFERENCES administrados(id),
  direccion TEXT NOT NULL,
  distrito TEXT NOT NULL,
  latitud DOUBLE PRECISION,
  longitud DOUBLE PRECISION,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS expedientes (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  numero TEXT NOT NULL UNIQUE,
  administrado_id BIGINT NOT NULL REFERENCES administrados(id),
  asunto TEXT NOT NULL,
  estado TEXT NOT NULL,
  fecha_registro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS documentos_ingresados (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  expediente_id BIGINT NOT NULL REFERENCES expedientes(id),
  tipo_documento_id BIGINT NOT NULL REFERENCES tipos_documento(id),
  numero_documento TEXT NOT NULL,
  descripcion TEXT,
  evidencia_uri TEXT,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS hojas_ruta (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  expediente_id BIGINT NOT NULL REFERENCES expedientes(id),
  oficina_origen_id BIGINT NOT NULL REFERENCES oficinas(id),
  oficina_destino_id BIGINT NOT NULL REFERENCES oficinas(id),
  personal_destino_id BIGINT NOT NULL REFERENCES personal(id),
  estado TEXT NOT NULL CHECK (estado IN ('PENDIENTE', 'DERIVADO', 'RECIBIDO', 'RECHAZADO', 'ARCHIVADO')),
  observacion TEXT,
  latitud DOUBLE PRECISION,
  longitud DOUBLE PRECISION,
  fecha_derivacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS archivo_fisico (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  expediente_id BIGINT NOT NULL REFERENCES expedientes(id),
  codigo_caja TEXT NOT NULL,
  ubicacion TEXT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS actas_archivamiento (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  expediente_id BIGINT NOT NULL REFERENCES expedientes(id),
  numero_acta TEXT NOT NULL,
  motivo TEXT NOT NULL,
  fecha_acta TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  sync_status TEXT NOT NULL DEFAULT 'SYNCED',
  deleted_at TIMESTAMPTZ
);

INSERT INTO oficinas (codigo, nombre) VALUES
  ('MP', 'Mesa de Partes'),
  ('SIS', 'Oficina de Sistemas'),
  ('ARCH', 'Archivo Central')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO tipos_documento (codigo, nombre) VALUES
  ('SOL', 'Solicitud'),
  ('INF', 'Informe')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO personal (nombres, cargo, oficina_id)
SELECT 'Delgado Santos Diego Antonio', 'Analista de tramite', id FROM oficinas WHERE codigo = 'MP'
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (nombre, email, password_hash, rol, personal_id)
SELECT 'Delgado Santos Diego Antonio', 'diego@smartgov.pe', '123456', 'ADMIN', p.id
FROM personal p WHERE p.nombres = 'Delgado Santos Diego Antonio'
ON CONFLICT (email) DO NOTHING;
