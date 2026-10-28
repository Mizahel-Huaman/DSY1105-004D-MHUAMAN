package com.example.insertcode.model

// Mensajes de error (nullable) para cada campo del formulario
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val direccion: String? = null,
    val aceptaTerminos: String? = null,
    val foto: String? = null
) {
    // true si existe al menos un error
    val hayErrores: Boolean
        get() = listOfNotNull(nombre, correo, clave, confirmarClave, direccion, aceptaTerminos, foto).isNotEmpty()
}
