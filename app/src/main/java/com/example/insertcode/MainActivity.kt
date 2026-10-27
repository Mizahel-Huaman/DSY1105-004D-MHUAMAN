package com.example.insertcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.insertcode.data.AppDatabase
import com.example.insertcode.data.PreferencesManager
import com.example.insertcode.data.UsuarioRepository
import com.example.insertcode.navigation.AppNavigation
import com.example.insertcode.ui.theme.InsertcodeTheme
import com.example.insertcode.viewmodels.UsuarioViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialización manual de dependencias (Base de datos -> Repository -> Factory)
        val database = AppDatabase.getDatabase(applicationContext)
        val preferences = PreferencesManager(applicationContext)
        val repository = UsuarioRepository(database.usuarioDao(), applicationContext)
        val factory = UsuarioViewModelFactory(repository, preferences)

        setContent {
            InsertcodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(factory = factory)
                }
            }
        }
    }
}
