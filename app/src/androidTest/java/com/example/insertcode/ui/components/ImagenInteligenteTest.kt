package com.example.insertcode.ui.components

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class ImagenInteligenteTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sin_uri_debe_mostrar_el_icono_de_persona() {
        composeRule.setContent {
            MaterialTheme { ImagenInteligente(imagenUri = null) }
        }

        composeRule.onNodeWithContentDescription("Icono de perfil por defecto").assertIsDisplayed()
    }

    @Test
    fun con_uri_debe_mostrar_la_foto_de_perfil() {
        composeRule.setContent {
            MaterialTheme { ImagenInteligente(imagenUri = Uri.parse("file:///android_asset/foto.jpg")) }
        }

        composeRule.onNodeWithContentDescription("Foto de perfil").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Icono de perfil por defecto").assertDoesNotExist()
    }
}
