package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "archivo_fisico", indices = {@Index(value = "uuid", unique = true), @Index(value = "expedienteId")})
public class ArchivoFisicoEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    public long expedienteId;
    @NonNull
    public String codigoCaja = "";
    @NonNull
    public String ubicacion = "";
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
