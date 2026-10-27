package com.example.insertcode.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insertcode.data.PreferencesManager
import com.example.insertcode.data.UsuarioEntity
import com.example.insertcode.data.UsuarioRepository
import com.example.insertcode.model.UsuarioErrores
import com.example.insertcode.model.UsuarioUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class UsuarioViewModel(
    private val repository: UsuarioRepository,
    private val preferences: PreferencesManager
) : ViewModel() {

    // ---------- Estados (MutableStateFlow privado, StateFlow público) ----------
    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val _errores = MutableStateFlow(UsuarioErrores())
    val errores: StateFlow<UsuarioErrores> = _errores.asStateFlow()

    // Inicia en true: se muestra el CircularProgressIndicator mientras "carga"
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // true durante 2 segundos después de cambiar el modo especial
    private val _mensajeExito = MutableStateFlow(false)
    val mensajeExito: StateFlow<Boolean> = _mensajeExito.asStateFlow()

    private var mensajeJob: Job? = null

    // ---------- Listado offline (ROOM) con búsqueda por correo ----------
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarios: StateFlow<List<UsuarioEntity>> = _busqueda
        .flatMapLatest { query ->
            if (query.isBlank()) repository.usuarios else repository.buscarPorCorreo(query.trim())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        cargarInicial()
    }

    // Simula una carga inicial (1.5 s) y recupera el modo especial desde DataStore
    private fun cargarInicial() {
        viewModelScope.launch {
            delay(1500)
            val modoGuardado = preferences.modoEspecialFlow.first()
            _uiState.update { it.copy(modoEspecial = modoGuardado) }
            _cargando.value = false
        }
    }

    // ---------- Eventos del formulario (limpian el error del campo editado) ----------
    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor) }
        _errores.update { it.copy(nombre = null) }
    }

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor) }
        _errores.update { it.copy(correo = null) }
    }

    fun onClaveChange(valor: String) {
        _uiState.update { it.copy(clave = valor) }
        _errores.update { it.copy(clave = null) }
    }

    fun onDireccionChange(valor: String) {
        _uiState.update { it.copy(direccion = valor) }
        _errores.update { it.copy(direccion = null) }
    }

    fun onTerminosChange(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor) }
        _errores.update { it.copy(aceptaTerminos = null) }
    }

    // Guía 13: foto desde cámara o galería
    fun actualizarFoto(uri: Uri?) {
        _uiState.update { it.copy(fotoUri = uri) }
        if (uri != null) _errores.update { it.copy(foto = null) }
    }

    // ---------- Modo especial: DataStore + mensaje temporal ----------
    fun toggleModoEspecial() {
        val nuevoValor = !_uiState.value.modoEspecial
        _uiState.update { it.copy(modoEspecial = nuevoValor) }

        viewModelScope.launch { preferences.guardarModoEspecial(nuevoValor) }

        // Si se pulsa varias veces seguidas, se reinicia el temporizador de 2 s
        mensajeJob?.cancel()
        mensajeJob = viewModelScope.launch {
            _mensajeExito.value = true
            delay(2000)
            _mensajeExito.value = false
        }
    }

    // ---------- Validación ----------
    fun validarFormulario(): Boolean {
        val s = _uiState.value
        val nuevosErrores = UsuarioErrores(
            nombre = if (s.nombre.trim().length < 3) "El nombre debe tener al menos 3 caracteres" else null,
            correo = if (!s.correo.contains("@")) "El correo debe contener @" else null,
            clave = if (s.clave.length < 6) "La clave debe tener al menos 6 caracteres" else null,
            direccion = if (s.direccion.isBlank()) "La dirección es obligatoria" else null,
            aceptaTerminos = if (!s.aceptaTerminos) "Debes aceptar los términos y condiciones" else null,
            foto = if (s.fotoUri == null) "Debes seleccionar o tomar una foto" else null
        )
        _errores.value = nuevosErrores
        return !nuevosErrores.hayErrores
    }

    // ---------- Persistencia en ROOM ----------
    // Convierte el UiState a UsuarioEntity e inserta en la base de datos.
    // onGuardado se ejecuta (en el hilo principal) cuando el insert terminó.
    fun guardarEnRoom(onGuardado: () -> Unit = {}) {
        viewModelScope.launch {
            val s = _uiState.value
            // Copia la foto al almacenamiento interno; si falla, guarda la Uri original
            val fotoPath = s.fotoUri?.let { repository.guardarFotoLocal(it) ?: it.toString() }

            val entidad = UsuarioEntity(
                nombre = s.nombre.trim(),
                correo = s.correo.trim(),
                direccion = s.direccion.trim(),
                modoEspecial = s.modoEspecial,
                fotoPath = fotoPath,
                fechaRegistro = Date()
            )
            repository.insertar(entidad)
            onGuardado()
        }
    }

    // ---------- Listado ----------
    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
    }

    fun eliminarUsuario(usuario: UsuarioEntity) {
        viewModelScope.launch { repository.eliminar(usuario) }
    }

    // Deja el formulario vacío para un nuevo registro (conserva el modo especial)
    fun limpiarFormulario() {
        _uiState.update { UsuarioUiState(modoEspecial = it.modoEspecial) }
        _errores.value = UsuarioErrores()
    }
}
