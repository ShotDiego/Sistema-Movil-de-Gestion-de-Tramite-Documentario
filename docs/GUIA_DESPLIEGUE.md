# Guia de despliegue - Smart-Gov Sync

## Datos del proyecto

- Grupo: Grupo 4
- Repositorio GitHub: https://github.com/ShotDiego/Sistema-Movil-de-Gestion-de-Tramite-Documentario
- API publica Render: https://smart-gov-sync-api.onrender.com
- Health check: https://smart-gov-sync-api.onrender.com/health
- Base remota: Neon PostgreSQL
- App movil: Android Java/XML con Room, Retrofit, Google Maps y captura multimedia

## Integrantes

- Asencios Obregon David Imanol
- Delgado Santos Diego Antonio
- Morales Pena Angel Gabriel
- Veramendi Borja Luis Fernando

## Arquitectura

La aplicacion trabaja con enfoque offline-first. Los datos se guardan primero en Room, dentro del dispositivo Android. Cuando existe conexion a internet, la app puede sincronizar con la API REST desplegada en Render. El backend Node/Express se conecta a PostgreSQL en Neon usando la variable `DATABASE_URL`.

Componentes:

- Android: Java, XML, Room, Retrofit, Google Maps, Location Services.
- Backend: Node.js, Express, JWT, PostgreSQL.
- Servidor publico: Render Web Service.
- Base de datos remota: Neon PostgreSQL.
- Seguridad: login con JWT.
- Conflictos: estrategia `last-write-wins` usando `updated_at`.

## Endpoints principales

- `GET /health`: verifica conexion del backend y la base Neon.
- `POST /login`: valida usuario y devuelve token JWT.
- `GET /sincronizacion`: descarga cambios del servidor.
- `POST /sync-data`: sube cambios creados offline.
- `GET/POST/PUT/DELETE /:entidad`: CRUD generico para las entidades permitidas.

Entidades:

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

## Configuracion de Neon

1. Crear proyecto `smart-gov-sync`.
2. Entrar al SQL Editor.
3. Ejecutar el archivo `backend/sql/schema.sql`.
4. Copiar la connection string PostgreSQL.
5. Usarla en Render como variable `DATABASE_URL`.

## Configuracion de Render

Crear un Web Service conectado al repo GitHub con:

```text
Name: smart-gov-sync-api
Language: Node
Branch: main
Root Directory: backend
Build Command: npm install
Start Command: npm start
Plan: Free
```

Variables de entorno:

```text
DATABASE_URL = connection string de Neon
JWT_SECRET = grupo4_smart_gov_sync_2026
JWT_EXPIRES_IN = 8h
NODE_ENV = production
```

## Configuracion Android

En `local.properties` colocar:

```properties
MAPS_API_KEY=TU_API_KEY_DE_GOOGLE_MAPS
API_BASE_URL=https://smart-gov-sync-api.onrender.com/
```

`local.properties` no se sube a GitHub porque contiene claves privadas y rutas locales.

## Usuarios demo

```text
diego@smartgov.pe / 123456
david@smartgov.pe / 123456
angel@smartgov.pe / 123456
luis@smartgov.pe / 123456
```

## Pruebas rapidas

Health check:

```powershell
Invoke-RestMethod -Uri 'https://smart-gov-sync-api.onrender.com/health'
```

Login:

```powershell
Invoke-RestMethod -Uri 'https://smart-gov-sync-api.onrender.com/login' `
  -Method Post `
  -ContentType 'application/json' `
  -Body '{"email":"diego@smartgov.pe","password":"123456"}'
```

Compilar Android:

```powershell
.\gradlew.bat :app:assembleDebug
```

APK generado:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Guion breve para el video

1. Mostrar GitHub con el codigo del proyecto.
2. Mostrar Neon con las tablas creadas.
3. Mostrar Render con el servicio `smart-gov-sync-api` en estado live.
4. Abrir `https://smart-gov-sync-api.onrender.com/health` y mostrar `ok: true`.
5. Probar login y mostrar que devuelve JWT.
6. Ejecutar la app Android.
7. Iniciar sesion con usuario demo.
8. Mostrar bandeja de hojas de ruta.
9. Cambiar una derivacion a Recibido/Rechazado.
10. Capturar ubicacion en el mapa.
11. Tomar foto de evidencia multimedia.
12. Explicar que los registros se guardan offline en Room y luego se sincronizan con Render/Neon.
