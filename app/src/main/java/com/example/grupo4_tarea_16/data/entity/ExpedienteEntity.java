package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expedientes", indices = {@Index(value = "uuid", unique = true), @Index(value = "administradoId")})
public class ExpedienteEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    @NonNull
    public String numero = "";
    public long administradoId;
    @NonNull
    public String asunto = "";
    @NonNull
    public String estado = "";
    public long fechaRegistro;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
