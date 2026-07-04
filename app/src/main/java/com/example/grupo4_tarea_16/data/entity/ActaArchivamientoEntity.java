package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "actas_archivamiento", indices = {@Index(value = "uuid", unique = true), @Index(value = "expedienteId")})
public class ActaArchivamientoEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    public long expedienteId;
    @NonNull
    public String numeroActa = "";
    @NonNull
    public String motivo = "";
    public long fechaActa;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
