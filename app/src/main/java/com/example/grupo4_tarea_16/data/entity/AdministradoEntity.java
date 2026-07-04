package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "administrados", indices = {@Index(value = "uuid", unique = true), @Index(value = "documentoIdentidad")})
public class AdministradoEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    @NonNull
    public String nombres = "";
    @NonNull
    public String documentoIdentidad = "";
    @NonNull
    public String telefono = "";
    @NonNull
    public String email = "";
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
