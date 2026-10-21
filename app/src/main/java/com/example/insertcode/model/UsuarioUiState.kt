package com.example.insertcode.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val rol: String = "Vendedor", // <-- Aquí está la propiedad que te faltaba
    val aceptaTerminos: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores()
)

data class Pedido(
    val id: String,
    val cliente: String,
    val direccion: String,
    val monto: Int,
    val prioridad: String,
    val estado: String
)