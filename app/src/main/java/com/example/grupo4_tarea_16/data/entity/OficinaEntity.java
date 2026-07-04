package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "oficinas", indices = {@Index(value = "uuid", unique = true)})
public class OficinaEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    @NonNull
    public String codigo = "";
    @NonNull
    public String nombre = "";
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
