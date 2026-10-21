package com.example.insertcode.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class EstadoDataStore(private val context: Context) {

    private val ESTADO_ACTIVADO = booleanPreferencesKey(name = "modo_activado")
    private val USUARIO_NOMBRE = stringPreferencesKey(name = "usuario_nombre")
    private val USUARIO_ROL = stringPreferencesKey(name = "usuario_rol")

    // Función original de la Guía 12
    suspend fun guardarEstado(valor: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ESTADO_ACTIVADO] = valor
        }
    }

    fun obtenerEstado(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[ESTADO_ACTIVADO]
        }
    }

    // NUEVA FUNCIÓN: Guarda los datos del perfil comercial (Caso Insertcode)
    suspend fun guardarPerfil(nombre: String, rol: String) {
        context.dataStore.edit { preferencias ->
            preferencias[USUARIO_NOMBRE] = nombre
            preferencias[USUARIO_ROL] = rol
        }
    }

    fun obtenerNombre(): Flow<String> = context.dataStore.data.map { it[USUARIO_NOMBRE] ?: "" }
    fun obtenerRol(): Flow<String> = context.dataStore.data.map { it[USUARIO_ROL] ?: "Vendedor" }
}