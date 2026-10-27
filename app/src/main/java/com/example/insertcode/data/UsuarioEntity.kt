package com.example.insertcode.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// Tabla "usuarios" de ROOM.
// La clave NO se guarda en la base de datos (buena práctica de seguridad).
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val modoEspecial: Boolean,
    val fotoPath: String?,      // ruta local de la foto (copiada al almacenamiento interno)
    val fechaRegistro: Date     // se convierte a Long con Converters
)
