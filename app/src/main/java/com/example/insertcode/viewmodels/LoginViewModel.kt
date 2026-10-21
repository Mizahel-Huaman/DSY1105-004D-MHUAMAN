package com.example.insertcode.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.insertcode.data.EstadoDataStore
import com.example.insertcode.model.LoginErrores
import com.example.insertcode.model.LoginUiState
import com.example.insertcode.navigation.Screen
import com.example.insertcode.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado del formulario de Login, validaciones reactivas,
 * simulación de conectividad y persistencia local básica.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val dataStore = EstadoDataStore(application)

    // Estado interno mutable y estado público inmutable
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Actualiza el correo y limpia errores previos del campo
    fun onEmailChange(valor: String) {
        _estado.update {
            it.copy(
                email = valor,
                errores = it.errores.copy(email = null, autenticacion = null)
            )
        }
    }

    // Actualiza la contraseña y limpia errores previos del campo
    fun onPasswordChange(valor: String) {
        _estado.update {
            it.copy(
                password = valor,
                errores = it.errores.copy(password = null, autenticacion = null)
            )
        }
    }

    // Simula un interruptor de falla de red para demostrar manejo de error de conectividad
    fun alternarSimulacionRed() {
        _estado.update { it.copy(errorConexion = !it.errorConexion) }
    }

    // Valida la presencia y formato de los campos para desplegar errores específicos en la UI
    fun validarFormulario(): Boolean {
        val actual = _estado.value

        val errorEmail = when {
            actual.email.isBlank() -> "Debes ingresar un correo electrónico"
            !actual.email.contains("@") || !actual.email.contains(".") -> "Formato de correo inválido"
            else -> null
        }

        val errorPass = when {
            actual.password.isBlank() -> "Debes ingresar una contraseña"
            actual.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }

        val nuevosErrores = LoginErrores(email = errorEmail, password = errorPass)
        val esValido = listOfNotNull(errorEmail, errorPass).isEmpty()

        _estado.update { it.copy(errores = nuevosErrores) }
        return esValido
    }

    // Ejecuta el login con simulación de carga, control de conectividad y redirección según rol
    fun iniciarSesion(onNavegarPorRol: (Screen) -> Unit) {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _estado.update { it.copy(isLoading = true, errores = LoginErrores()) }
            delay(1000) // Simula tiempo de espera de red (Loader)

            val actual = _estado.value

            // Verifica si se activó la simulación de error de conectividad
            if (actual.errorConexion) {
                _estado.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(autenticacion = "Error de conectividad: Verifique su conexión WiFi o datos móviles.")
                    )
                }
                return@launch
            }

            // Autentica contra el repositorio
            val usuario = repository.autenticar(actual.email, actual.password)
            if (usuario != null) {
                // Guarda persistencia local en DataStore
                dataStore.guardarPerfil(usuario.nombre, usuario.rol)

                // Limpia los campos de correo y contraseña para que no queden guardados en la vista
                _estado.update {
                    it.copy(
                        isLoading = false,
                        rolActivo = usuario.rol,
                        nombreUsuario = usuario.nombre,
                        email = "",
                        password = "",
                        errores = LoginErrores()
                    )
                }

                // Determina la pantalla de destino según el rol autenticado
                val destino = when (usuario.rol) {
                    "Admin" -> Screen.HomeAdmin
                    "Supervisor" -> Screen.HomeSupervisor
                    else -> Screen.HomeOperador
                }
                onNavegarPorRol(destino)
            } else {
                // Credenciales erróneas
                _estado.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(
                            autenticacion = "Credenciales incorrectas. Verifique su correo y contraseña."
                        )
                    )
                }
            }
        }
    }

    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }
}