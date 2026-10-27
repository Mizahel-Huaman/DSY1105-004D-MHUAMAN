package com.example.insertcode.ui

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.insertcode.data.PreferencesManager
import com.example.insertcode.data.UsuarioDao
import com.example.insertcode.data.UsuarioEntity
import com.example.insertcode.data.UsuarioRepository
import com.example.insertcode.viewmodels.UsuarioViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// DAO falso en memoria: no usa Room ni el disco
class FakeUsuarioDao(inicial: List<UsuarioEntity>) : UsuarioDao {
    private val datos = MutableStateFlow(inicial)

    override fun getUsuariosFlow(): Flow<List<UsuarioEntity>> = datos

    override suspend fun getPorId(id: Long): UsuarioEntity? = datos.value.firstOrNull { it.id == id }

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        datos.value = datos.value.filter { it.id != usuario.id } + usuario
        return usuario.id
    }

    override suspend fun eliminar(usuario: UsuarioEntity) {
        datos.value = datos.value - usuario
    }

    override fun buscarPorCorreo(query: String): Flow<List<UsuarioEntity>> =
        datos.map { lista -> lista.filter { it.correo.contains(query, ignoreCase = true) } }
}

// Preferencias falsas en memoria: no tocan el DataStore real de la app
class FakePreferencesManager(context: Context, inicial: Boolean) : PreferencesManager(context) {
    private val estado = MutableStateFlow(inicial)

    override val modoEspecialFlow: Flow<Boolean> = estado

    override suspend fun guardarModoEspecial(valor: Boolean) {
        estado.value = valor
    }
}

// UsuarioViewModel real, pero con dependencias falsas (sin MockK)
fun crearViewModelFalso(
    usuarios: List<UsuarioEntity> = emptyList(),
    modoGuardado: Boolean = false
): UsuarioViewModel {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val repo = UsuarioRepository(FakeUsuarioDao(usuarios), context)
    val prefs = FakePreferencesManager(context, modoGuardado)
    return UsuarioViewModel(repo, prefs)
}
