package com.example.insertcode.data

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room solo sabe guardar tipos primitivos. Como UsuarioEntity usa java.util.Date,
 * necesitamos estos convertidores:
 *  - Date  -> Long : se guarda el timestamp en milisegundos (dateToTimestamp)
 *  - Long  -> Date : se reconstruye la fecha al leer la fila (fromTimestamp)
 * Se registran en AppDatabase con @TypeConverters(Converters::class).
 */
class Converters {

    // Long (BD) -> Date (Entity)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    // Date (Entity) -> Long (BD)
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
