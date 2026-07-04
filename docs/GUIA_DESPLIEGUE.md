# Guia de despliegue - Smart-Gov Sync

## Decision tecnica

Para la exposicion conviene usar servidor publico en vez de `localhost` porque el telefono o emulador puede consumir una URL real, el ingeniero puede ver el endpoint `/health`, y el video demuestra publicacion de servicios web.

La combinacion recomendada es:

- Android: Java + XML + Room + Retrofit + Google Maps.
- Servidor publico: Render Web Service.
- Base remota: Neon PostgreSQL.
- Seguridad: JWT en cada endpoint privado.
- Conflictos: estrategia `last-write-wins` usando `updated_at`.

## Google Maps

Para que el mapa se vea debes crear una API key de Google Maps Platform:

1. Entrar a Google Cloud Console.
2. Crear o seleccionar un proyecto.
3. Habilitar `Maps SDK for Android`.
4. Crear una API key.
5. Restringirla al paquete Android `com.example.grupo4_tarea_16` y al SHA-1 de debug.
6. Agregar en `local.properties`:

```properties
MAPS_API_KEY=TU_API_KEY
API_BASE_URL=https://TU-SERVICIO.onrender.com/
```

Si no colocas `MAPS_API_KEY`, la app compila, pero Google Maps mostrara error o mapa en blanco.

## Comandos utiles

Compilar Android:

```bash
.\gradlew.bat :app:assembleDebug
```

Probar backend local:

```bash
cd backend
npm install
npm run dev
```

Probar servidor desplegado:

```bash
curl https://TU-SERVICIO.onrender.com/health
```

## Guion breve para el video

1. Mostrar login con usuario demo `diego@smartgov.pe / 123456`.
2. Explicar que Room mantiene las 10 tablas y `usuarios` en local.
3. Mostrar la bandeja de hojas de ruta pendiente.
4. Marcar una derivacion como recibida o rechazada; queda `PENDING` para sincronizar.
5. Capturar ubicacion y mostrar el marcador en Google Maps.
6. Pulsar sincronizar y explicar que el backend recibe los cambios con `POST /sync-data`.
7. Mostrar la URL publica de Render y el SQL en Neon.
