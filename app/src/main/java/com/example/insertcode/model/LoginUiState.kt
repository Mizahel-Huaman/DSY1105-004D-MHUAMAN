package com.example.insertcode.model

/**
 * Modelo de estado reactivo para el formulario de Login y sesión activa.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val rolActivo: String = "",
    val nombreUsuario: String = "",
    val errorConexion: Boolean = false,
    val errores: LoginErrores = LoginErrores()
) {
    // Propiedad derivada que habilita el botón solo cuando hay datos básicos ingresados y no está cargando
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}