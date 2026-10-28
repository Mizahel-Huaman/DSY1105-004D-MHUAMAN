package com.example.insertcode.auth

// Resultado de iniciar sesión con un proveedor social (Google / Apple)
sealed interface ResultadoSocial {
    data class Exito(val nombre: String, val correo: String) : ResultadoSocial
    data object Cancelado : ResultadoSocial
    data class Error(val mensaje: String) : ResultadoSocial
}
