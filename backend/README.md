# Smart-Gov Sync API

Backend publico para la Tarea 16 del Grupo 4.

## URLs

- API publica: https://smart-gov-sync-api.onrender.com
- Health check: https://smart-gov-sync-api.onrender.com/health
- Repositorio: https://github.com/ShotDiego/Sistema-Movil-de-Gestion-de-Tramite-Documentario

## Integrantes

- Asencios Obregon David Imanol
- Delgado Santos Diego Antonio
- Morales Pena Angel Gabriel
- Veramendi Borja Luis Fernando

## Endpoints principales

- `GET /health`
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

## Variables de entorno Render

```text
DATABASE_URL=connection string de Neon
JWT_SECRET=grupo4_smart_gov_sync_2026
JWT_EXPIRES_IN=8h
NODE_ENV=production
```

## Desarrollo local

```bash
cd backend
npm install
copy .env.example .env
npm run dev
```

## Deploy Render

Usar Neon para PostgreSQL y Render para el Web Service.

1. Crear proyecto en Neon.
2. Ejecutar `backend/sql/schema.sql` en el SQL Editor de Neon.
3. Crear un Web Service en Render conectado al repositorio GitHub.
4. Configurar:
   - Root Directory: `backend`
   - Build Command: `npm install`
   - Start Command: `npm start`
5. Agregar variables de entorno.
6. Desplegar el servicio.

## Prueba de login

```powershell
Invoke-RestMethod -Uri 'https://smart-gov-sync-api.onrender.com/login' `
  -Method Post `
  -ContentType 'application/json' `
  -Body '{"email":"diego@smartgov.pe","password":"123456"}'
```
