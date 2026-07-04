package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios", indices = {@Index(value = "uuid", unique = true), @Index(value = "email", unique = true)})
public class UsuarioEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    @NonNull
    public String nombre = "";
    @NonNull
    public String email = "";
    @NonNull
    public String passwordHash = "";
    @NonNull
    public String rol = "";
    public long personalId;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
