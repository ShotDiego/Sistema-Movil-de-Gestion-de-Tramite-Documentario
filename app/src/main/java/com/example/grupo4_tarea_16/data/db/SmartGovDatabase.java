package com.example.grupo4_tarea_16.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.grupo4_tarea_16.data.dao.SmartGovDao;
import com.example.grupo4_tarea_16.data.entity.ActaArchivamientoEntity;
import com.example.grupo4_tarea_16.data.entity.AdministradoEntity;
import com.example.grupo4_tarea_16.data.entity.ArchivoFisicoEntity;
import com.example.grupo4_tarea_16.data.entity.DireccionEntity;
import com.example.grupo4_tarea_16.data.entity.DocumentoIngresadoEntity;
import com.example.grupo4_tarea_16.data.entity.ExpedienteEntity;
import com.example.grupo4_tarea_16.data.entity.HojaRutaEntity;
import com.example.grupo4_tarea_16.data.entity.OficinaEntity;
import com.example.grupo4_tarea_16.data.entity.PersonalEntity;
import com.example.grupo4_tarea_16.data.entity.TipoDocumentoEntity;
import com.example.grupo4_tarea_16.data.entity.UsuarioEntity;

@Database(
        entities = {
                UsuarioEntity.class,
                OficinaEntity.class,
                TipoDocumentoEntity.class,
                AdministradoEntity.class,
                PersonalEntity.class,
                DireccionEntity.class,
                ExpedienteEntity.class,
                DocumentoIngresadoEntity.class,
                HojaRutaEntity.class,
                ArchivoFisicoEntity.class,
                ActaArchivamientoEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class SmartGovDatabase extends RoomDatabase {
    private static volatile SmartGovDatabase instance;

    public abstract SmartGovDao smartGovDao();

    public static SmartGovDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (SmartGovDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SmartGovDatabase.class,
                                    "smart_gov_sync.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
