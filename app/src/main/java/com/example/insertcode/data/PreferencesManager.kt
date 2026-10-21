package com.example.insertcode.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Reutiliza la extensión Context.dataStore declarada en EstadoDataStore.kt
// (un solo DataStore por nombre: crear dos con el mismo nombre provoca un crash)
class PreferencesManager(private val context: Context) {

    private val MODO_ESPECIAL = booleanPreferencesKey("modo_especial")

    // Flow reactivo con el modo especial (false por defecto)
    val modoEspecialFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MODO_ESPECIAL] ?: false
    }

    suspend fun guardarModoEspecial(valor: Boolean) {
        context.dataStore.edit { prefs -> prefs[MODO_ESPECIAL] = valor }
    }
}
