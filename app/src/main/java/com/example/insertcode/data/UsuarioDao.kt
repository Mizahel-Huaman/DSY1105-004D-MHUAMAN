package com.example.insertcode.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // Flow: la UI se actualiza sola cada vez que cambia la tabla
    @Query("SELECT * FROM usuarios ORDER BY fechaRegistro DESC")
    fun getUsuariosFlow(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getPorId(id: Long): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity)

    // Búsqueda parcial por correo
    @Query("SELECT * FROM usuarios WHERE correo LIKE '%' || :query || '%' ORDER BY fechaRegistro DESC")
    fun buscarPorCorreo(query: String): Flow<List<UsuarioEntity>>
}
