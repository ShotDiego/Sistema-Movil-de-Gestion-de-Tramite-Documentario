package com.example.grupo4_tarea_16.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

import java.util.List;

@Dao
public interface SmartGovDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUsuario(UsuarioEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOficina(OficinaEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTipoDocumento(TipoDocumentoEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAdministrado(AdministradoEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPersonal(PersonalEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDireccion(DireccionEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertExpediente(ExpedienteEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDocumentoIngresado(DocumentoIngresadoEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHojaRuta(HojaRutaEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertArchivoFisico(ArchivoFisicoEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActaArchivamiento(ActaArchivamientoEntity item);

    @Query("SELECT * FROM usuarios WHERE email = :email AND passwordHash = :password LIMIT 1")
    UsuarioEntity login(String email, String password);

    @Query("SELECT COUNT(*) FROM usuarios")
    int countUsuarios();

    @Query("SELECT COUNT(*) FROM oficinas")
    int countOficinas();

    @Query("SELECT COUNT(*) FROM tipos_documento")
    int countTiposDocumento();

    @Query("SELECT COUNT(*) FROM administrados")
    int countAdministrados();

    @Query("SELECT COUNT(*) FROM personal")
    int countPersonal();

    @Query("SELECT COUNT(*) FROM direcciones")
    int countDirecciones();

    @Query("SELECT COUNT(*) FROM expedientes")
    int countExpedientes();

    @Query("SELECT COUNT(*) FROM documentos_ingresados")
    int countDocumentosIngresados();

    @Query("SELECT COUNT(*) FROM hojas_ruta")
    int countHojasRuta();

    @Query("SELECT COUNT(*) FROM archivo_fisico")
    int countArchivoFisico();

    @Query("SELECT COUNT(*) FROM actas_archivamiento")
    int countActasArchivamiento();

    @Query("SELECT COUNT(*) FROM hojas_ruta WHERE syncStatus != 'SYNCED'")
    int countPendingSync();

    @Query("SELECT * FROM hojas_ruta WHERE estado IN ('PENDIENTE', 'DERIVADO') AND deletedAt = 0 ORDER BY updatedAt DESC")
    List<HojaRutaEntity> getPendingRoutes();

    @Query("UPDATE hojas_ruta SET estado = :estado, syncStatus = 'PENDING', updatedAt = :updatedAt WHERE id = :id")
    int updateRouteStatus(long id, String estado, long updatedAt);

    @Query("UPDATE hojas_ruta SET latitud = :latitud, longitud = :longitud, syncStatus = 'PENDING', updatedAt = :updatedAt WHERE id = :id")
    int updateRouteLocation(long id, double latitud, double longitud, long updatedAt);

    @Query("UPDATE hojas_ruta SET syncStatus = 'SYNCED', updatedAt = :updatedAt WHERE syncStatus != 'SYNCED'")
    int markRoutesSynced(long updatedAt);

    @Query("UPDATE documentos_ingresados SET evidenciaUri = :uri, syncStatus = 'PENDING', updatedAt = :updatedAt WHERE id = (SELECT id FROM documentos_ingresados ORDER BY id LIMIT 1)")
    int updateFirstDocumentEvidence(String uri, long updatedAt);
}
