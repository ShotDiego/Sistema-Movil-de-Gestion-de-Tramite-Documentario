package com.example.grupo4_tarea_16.data.db;

import com.example.grupo4_tarea_16.data.SyncStatus;
import com.example.grupo4_tarea_16.data.dao.SmartGovDao;
import com.example.grupo4_tarea_16.data.entity.ActaArchivamientoEntity;
import com.example.grupo4_tarea_16.data.entity.AdministradoEntity;
import com.example.grupo4_tarea_16.data.entity.ArchivoFisicoEntity;
import com.example.grupo4_tarea_16.data.entity.DireccionEntity;
import com.example.grupo4_tarea_16.data.entity.DocumentoIngresadoEntity;
import com.example.grupo4_tarea_16.data.entity.ExpedienteEntity;
import com.example.grupo4_tarea_16.data.entity.HojaRutaEntity;
import com.example.grupo4_tarea_16.data.entity.OficinaEntity;
import com.example.grupo4_tarea_16.data.entity.PersonalEntity;
import com.example.grupo4_tarea_16.data.entity.TipoDocumentoEntity;
import com.example.grupo4_tarea_16.data.entity.UsuarioEntity;

import java.util.UUID;

public final class DemoSeeder {
    private DemoSeeder() {
    }

    public static void seedIfNeeded(SmartGovDao dao) {
        if (dao.countUsuarios() > 0) {
            return;
        }

        long now = System.currentTimeMillis();

        OficinaEntity mesaPartes = oficina("MP", "Mesa de Partes", now, SyncStatus.SYNCED);
        long mesaPartesId = dao.insertOficina(mesaPartes);
        OficinaEntity archivo = oficina("ARCH", "Archivo Central", now, SyncStatus.SYNCED);
        long archivoId = dao.insertOficina(archivo);
        OficinaEntity sistemas = oficina("SIS", "Oficina de Sistemas", now, SyncStatus.SYNCED);
        long sistemasId = dao.insertOficina(sistemas);

        long solicitudId = dao.insertTipoDocumento(tipoDocumento("SOL", "Solicitud", now));
        long informeId = dao.insertTipoDocumento(tipoDocumento("INF", "Informe", now));

        PersonalEntity diego = personal("Delgado Santos Diego Antonio", "Analista de tramite", mesaPartesId, now);
        long diegoId = dao.insertPersonal(diego);
        long davidId = dao.insertPersonal(personal("Asencios Obregon David Imanol", "Coordinador", sistemasId, now));
        long angelId = dao.insertPersonal(personal("Morales Pena Angel Gabriel", "Responsable de archivo", archivoId, now));
        long luisId = dao.insertPersonal(personal("Veramendi Borja Luis Fernando", "Operador documentario", mesaPartesId, now));

        dao.insertUsuario(usuario("Delgado Santos Diego Antonio", "diego@smartgov.pe", "123456", "ADMIN", diegoId, now));
        dao.insertUsuario(usuario("Asencios Obregon David Imanol", "david@smartgov.pe", "123456", "PERSONAL", davidId, now));
        dao.insertUsuario(usuario("Morales Pena Angel Gabriel", "angel@smartgov.pe", "123456", "ARCHIVO", angelId, now));
        dao.insertUsuario(usuario("Veramendi Borja Luis Fernando", "luis@smartgov.pe", "123456", "PERSONAL", luisId, now));

        AdministradoEntity administrado = administrado("Municipalidad Distrital de Huaura", "20123456789", "987654321", "mesa@huaura.gob.pe", now);
        long administradoId = dao.insertAdministrado(administrado);
        long direccionId = dao.insertDireccion(direccion(administradoId, "Av. San Martin 150", "Huacho", -11.1067, -77.6050, now));

        ExpedienteEntity expediente = expediente("EXP-2026-0001", administradoId, "Solicitud de actualizacion de licencia municipal", "EN_TRAMITE", now);
        long expedienteId = dao.insertExpediente(expediente);
        dao.insertDocumentoIngresado(documento(expedienteId, solicitudId, "SOL-145-2026", "Documento ingresado con evidencia fotografica pendiente", "", now));
        dao.insertDocumentoIngresado(documento(expedienteId, informeId, "INF-088-2026", "Informe tecnico para derivacion", "", now));

        dao.insertHojaRuta(hojaRuta(expedienteId, mesaPartesId, sistemasId, davidId, "PENDIENTE", "Verificar datos y adjuntar evidencia en campo", -11.1067, -77.6050, now, SyncStatus.PENDING));
        dao.insertHojaRuta(hojaRuta(expedienteId, sistemasId, archivoId, angelId, "DERIVADO", "Preparar archivamiento fisico del expediente", -11.1049, -77.6062, now, SyncStatus.SYNCED));

        dao.insertArchivoFisico(archivoFisico(expedienteId, "CAJA-2026-A01", "Estante 3 / Nivel 2", now));
        dao.insertActaArchivamiento(acta(expedienteId, "ACT-2026-001", "Cierre provisional por tramite completado", now));
    }

    private static OficinaEntity oficina(String codigo, String nombre, long now, String syncStatus) {
        OficinaEntity item = new OficinaEntity();
        item.uuid = uuid();
        item.codigo = codigo;
        item.nombre = nombre;
        item.updatedAt = now;
        item.syncStatus = syncStatus;
        return item;
    }

    private static TipoDocumentoEntity tipoDocumento(String codigo, String nombre, long now) {
        TipoDocumentoEntity item = new TipoDocumentoEntity();
        item.uuid = uuid();
        item.codigo = codigo;
        item.nombre = nombre;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static PersonalEntity personal(String nombres, String cargo, long oficinaId, long now) {
        PersonalEntity item = new PersonalEntity();
        item.uuid = uuid();
        item.nombres = nombres;
        item.cargo = cargo;
        item.oficinaId = oficinaId;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static UsuarioEntity usuario(String nombre, String email, String password, String rol, long personalId, long now) {
        UsuarioEntity item = new UsuarioEntity();
        item.uuid = uuid();
        item.nombre = nombre;
        item.email = email;
        item.passwordHash = password;
        item.rol = rol;
        item.personalId = personalId;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static AdministradoEntity administrado(String nombres, String documento, String telefono, String email, long now) {
        AdministradoEntity item = new AdministradoEntity();
        item.uuid = uuid();
        item.nombres = nombres;
        item.documentoIdentidad = documento;
        item.telefono = telefono;
        item.email = email;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static DireccionEntity direccion(long administradoId, String direccion, String distrito, double latitud, double longitud, long now) {
        DireccionEntity item = new DireccionEntity();
        item.uuid = uuid();
        item.administradoId = administradoId;
        item.direccion = direccion;
        item.distrito = distrito;
        item.latitud = latitud;
        item.longitud = longitud;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static ExpedienteEntity expediente(String numero, long administradoId, String asunto, String estado, long now) {
        ExpedienteEntity item = new ExpedienteEntity();
        item.uuid = uuid();
        item.numero = numero;
        item.administradoId = administradoId;
        item.asunto = asunto;
        item.estado = estado;
        item.fechaRegistro = now;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.PENDING;
        return item;
    }

    private static DocumentoIngresadoEntity documento(long expedienteId, long tipoDocumentoId, String numero, String descripcion, String evidenciaUri, long now) {
        DocumentoIngresadoEntity item = new DocumentoIngresadoEntity();
        item.uuid = uuid();
        item.expedienteId = expedienteId;
        item.tipoDocumentoId = tipoDocumentoId;
        item.numeroDocumento = numero;
        item.descripcion = descripcion;
        item.evidenciaUri = evidenciaUri;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.PENDING;
        return item;
    }

    private static HojaRutaEntity hojaRuta(long expedienteId, long origenId, long destinoId, long personalDestinoId, String estado, String observacion, double latitud, double longitud, long now, String syncStatus) {
        HojaRutaEntity item = new HojaRutaEntity();
        item.uuid = uuid();
        item.expedienteId = expedienteId;
        item.oficinaOrigenId = origenId;
        item.oficinaDestinoId = destinoId;
        item.personalDestinoId = personalDestinoId;
        item.estado = estado;
        item.observacion = observacion;
        item.latitud = latitud;
        item.longitud = longitud;
        item.fechaDerivacion = now;
        item.updatedAt = now;
        item.syncStatus = syncStatus;
        return item;
    }

    private static ArchivoFisicoEntity archivoFisico(long expedienteId, String codigoCaja, String ubicacion, long now) {
        ArchivoFisicoEntity item = new ArchivoFisicoEntity();
        item.uuid = uuid();
        item.expedienteId = expedienteId;
        item.codigoCaja = codigoCaja;
        item.ubicacion = ubicacion;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static ActaArchivamientoEntity acta(long expedienteId, String numeroActa, String motivo, long now) {
        ActaArchivamientoEntity item = new ActaArchivamientoEntity();
        item.uuid = uuid();
        item.expedienteId = expedienteId;
        item.numeroActa = numeroActa;
        item.motivo = motivo;
        item.fechaActa = now;
        item.updatedAt = now;
        item.syncStatus = SyncStatus.SYNCED;
        return item;
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }
}
