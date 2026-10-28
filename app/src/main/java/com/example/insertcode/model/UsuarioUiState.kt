package com.example.insertcode.model

import android.net.Uri

// Estado completo del formulario de usuario (Guía 13 + Guía 14)
data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",   // repetir la clave para verificar que coincidan
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val fotoUri: Uri? = null,          // Guía 13: foto desde cámara o galería
    val modoEspecial: Boolean = false, // Guía 14: modo premium (persistido en DataStore)
    val proveedorSocial: String? = null // "Google" o "Apple" si nombre/correo vienen de ahí (no requiere clave)
) {
    // true si el registro se hace con una cuenta social (Google/Apple)
    val registradoConSocial: Boolean get() = proveedorSocial != null
}
