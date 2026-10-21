package com.example.insertcode.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

// Capa intermedia entre el ViewModel y ROOM
class UsuarioRepository(
    private val dao: UsuarioDao,
    private val context: Context
) {
    // Listado offline: viene directo de ROOM
    val usuarios: Flow<List<UsuarioEntity>> = dao.getUsuariosFlow()

    fun buscarPorCorreo(query: String): Flow<List<UsuarioEntity>> = dao.buscarPorCorreo(query)

    suspend fun insertar(usuario: UsuarioEntity): Long = dao.insertar(usuario)

    suspend fun eliminar(usuario: UsuarioEntity) = dao.eliminar(usuario)

    /**
     * Copia la imagen (cámara o galería) al almacenamiento interno de la app.
     * Así la foto sigue disponible offline aunque se pierda el permiso de la Uri original.
     * Devuelve la ruta absoluta, o null si falló la copia.
     */
    suspend fun guardarFotoLocal(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.filesDir, "fotos").apply { mkdirs() }
            val destino = File(dir, "foto_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            } ?: return@runCatching null
            destino.absolutePath
        }.getOrNull()
    }
}
