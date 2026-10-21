package com.example.insertcode.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel : ViewModel() {

    // Estado reactivo de la URI de la imagen
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    // Función para actualizar la URI en el estado
    fun actualizarImagenUri(uri: Uri?) {
        _imagenUri.value = uri
    }
}