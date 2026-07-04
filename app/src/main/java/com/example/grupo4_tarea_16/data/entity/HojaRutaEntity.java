package com.example.grupo4_tarea_16.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "hojas_ruta", indices = {@Index(value = "uuid", unique = true), @Index(value = "expedienteId"), @Index(value = "personalDestinoId")})
public class HojaRutaEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String uuid = "";
    public long expedienteId;
    public long oficinaOrigenId;
    public long oficinaDestinoId;
    public long personalDestinoId;
    @NonNull
    public String estado = "";
    @NonNull
    public String observacion = "";
    public double latitud;
    public double longitud;
    public long fechaDerivacion;
    public long updatedAt;
    @NonNull
    public String syncStatus = "PENDING";
    public long deletedAt;
}
