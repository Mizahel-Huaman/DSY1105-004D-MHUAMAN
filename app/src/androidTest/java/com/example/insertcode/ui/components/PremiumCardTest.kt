package com.example.insertcode.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class PremiumCardTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun con_modo_especial_activo_debe_mostrar_el_mensaje_premium() {
        composeRule.setContent {
            MaterialTheme { PremiumCard(modoEspecial = true) }
        }

        composeRule.onNodeWithText("¡Beneficios Premium activados!").assertIsDisplayed()
    }

    @Test
    fun con_modo_especial_inactivo_no_debe_mostrar_la_tarjeta() {
        composeRule.setContent {
            MaterialTheme { PremiumCard(modoEspecial = false) }
        }

        composeRule.onNodeWithText("¡Beneficios Premium activados!").assertDoesNotExist()
    }
}
