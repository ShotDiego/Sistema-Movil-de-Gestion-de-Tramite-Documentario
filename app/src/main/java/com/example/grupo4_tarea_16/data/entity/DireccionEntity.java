package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "direcciones", indices = {@Index(value = "uuid", unique = true), @Index(value = "administradoId")})
public class DireccionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    public long administradoId;
    @NonNull
    public String direccion = "";
    @NonNull
    public String distrito = "";
    public double latitud;
    public double longitud;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
