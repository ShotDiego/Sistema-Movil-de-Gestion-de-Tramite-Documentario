package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "documentos_ingresados", indices = {@Index(value = "uuid", unique = true), @Index(value = "expedienteId")})
public class DocumentoIngresadoEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    public long expedienteId;
    public long tipoDocumentoId;
    @NonNull
    public String numeroDocumento = "";
    @NonNull
    public String descripcion = "";
    @NonNull
    public String evidenciaUri = "";
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
