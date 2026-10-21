package com.example.insertcode.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.insertcode.data.EstadoDataStore
import com.example.insertcode.model.Pedido
import com.example.insertcode.model.UsuarioErrores
import com.example.insertcode.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = EstadoDataStore(application)

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    // Pedidos simulados para el MVP
    private val _pedidos = MutableStateFlow(
        listOf(
            Pedido("PED-001", "Ferretería Sur", "Av. Central 123", 150000, "Alta", "Pendiente"),
            Pedido("PED-002", "Minimarket Sol", "Calle Prat 45", 45000, "Baja", "En Ruta")
        )
    )
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRolChange(rol: String) {
        _estado.update { it.copy(rol = rol) }
    }

    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = UsuarioErrores(
            correo = if (!actual.correo.contains("@")) "Correo inválido" else null,
            clave = if (actual.clave.length < 6) "Mínimo 6 caracteres" else null
        )

        val hayErrores = listOfNotNull(errores.correo, errores.clave).isNotEmpty()
        _estado.update { it.copy(errores = errores) }

        if (!hayErrores) {
            viewModelScope.launch {
                dataStore.guardarPerfil(actual.correo, actual.rol)
            }
        }
        return !hayErrores
    }

    fun avanzarEstadoPedido(id: String) {
        _pedidos.update { lista ->
            lista.map { pedido ->
                if (pedido.id == id) {
                    val nuevoEstado = when (pedido.estado) {
                        "Pendiente" -> "En Ruta"
                        "En Ruta" -> "Entregado"
                        else -> "Pendiente"
                    }
                    pedido.copy(estado = nuevoEstado)
                } else pedido
            }
        }
    }
}