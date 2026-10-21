package com.example.insertcode.model

import android.net.Uri

// Estado completo del formulario de usuario (Guía 13 + Guía 14)
data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val fotoUri: Uri? = null,          // Guía 13: foto desde cámara o galería
    val modoEspecial: Boolean = false  // Guía 14: modo premium (persistido en DataStore)
)
