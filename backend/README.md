# Smart-Gov Sync API

Backend publico para la Tarea 16 del Grupo 4.

## Integrantes

- Asencios Obregon David Imanol
- Delgado Santos Diego Antonio
- Morales Pena Angel Gabriel
- Veramendi Borja Luis Fernando

## Endpoints principales

- `POST /login`
- `GET /sincronizacion`
- `POST /sync-data`
- `GET /:entidad`
- `POST /:entidad`
- `PUT /:entidad/:uuid`
- `DELETE /:entidad/:uuid`

Entidades permitidas:

- `usuarios`
- `oficinas`
- `tipos_documento`
- `administrados`
- `personal`
- `direcciones`
- `expedientes`
- `documentos_ingresados`
- `hojas_ruta`
- `archivo_fisico`
- `actas_archivamiento`

## Desarrollo local

```bash
cd backend
npm install
copy .env.example .env
npm run dev
```

## Deploy recomendado

Usar Neon para PostgreSQL y Render para el Web Service.

1. Crear proyecto en Neon.
2. Copiar la cadena `DATABASE_URL`.
3. Ejecutar `backend/sql/schema.sql` en el SQL editor de Neon.
4. Subir el repositorio a GitHub.
5. Crear un Web Service en Render conectado al repositorio.
6. Configurar:
   - Root Directory: `backend`
   - Build Command: `npm install`
   - Start Command: `npm start`
   - Environment variable `DATABASE_URL`
   - Environment variable `JWT_SECRET`
7. Copiar la URL publica de Render y ponerla en Android `local.properties`:

```properties
API_BASE_URL=https://smart-gov-sync.onrender.com/
```
