package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "personal", indices = {@Index(value = "uuid", unique = true), @Index(value = "oficinaId")})
public class PersonalEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    @NonNull
    public String nombres = "";
    @NonNull
    public String cargo = "";
    public long oficinaId;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
