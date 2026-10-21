package com.example.insertcode.model

/**
 * Modelo que almacena los mensajes de error individuales por cada campo del formulario.
 */
data class LoginErrores(
    val email: String? = null,
    val password: String? = null,
    val autenticacion: String? = null
)