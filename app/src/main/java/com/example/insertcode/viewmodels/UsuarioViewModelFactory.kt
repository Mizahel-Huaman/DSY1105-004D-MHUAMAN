package com.example.insertcode.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.insertcode.data.PreferencesManager
import com.example.insertcode.data.UsuarioRepository

// Necesario porque UsuarioViewModel recibe dependencias por constructor
class UsuarioViewModelFactory(
    private val repository: UsuarioRepository,
    private val preferences: PreferencesManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
