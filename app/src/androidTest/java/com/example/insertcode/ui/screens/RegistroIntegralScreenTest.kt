package com.example.insertcode.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.ui.crearViewModelFalso
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class RegistroIntegralScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun mostrarPantalla() {
        val viewModel = crearViewModelFalso()
        composeRule.setContent {
            MaterialTheme {
                RegistroIntegralScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }
    }

    // El ViewModel simula una carga de 1500 ms: esperamos a que aparezca el formulario
    private fun esperarFormulario() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Registro Integral").fetchSemanticsNodes().isNotEmpty()
        }
    }

    // Campo de texto identificado por su etiqueta
    private fun campo(etiqueta: String): SemanticsMatcher = hasText(etiqueta) and hasSetTextAction()

    @Test
    fun al_iniciar_debe_mostrar_el_indicador_de_carga() {
        mostrarPantalla()

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeRule.onNodeWithText("Registro Integral").assertDoesNotExist()
    }

    @Test
    fun despues_de_la_carga_debe_mostrar_el_formulario() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNodeWithText("Registro Integral").assertIsDisplayed()
        composeRule.onNodeWithText("Cámara").assertIsDisplayed()
        composeRule.onNodeWithText("Galería").assertIsDisplayed()
    }

    @Test
    fun registrar_con_formulario_vacio_debe_mostrar_los_errores_de_validacion() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNodeWithText("Registrar y ver Resumen").performScrollTo().performClick()

        composeRule.onNodeWithText("Debes seleccionar o tomar una foto").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("El nombre debe tener al menos 3 caracteres").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("El correo debe contener @").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("La clave debe tener al menos 6 caracteres").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Debes confirmar la clave").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("La dirección es obligatoria").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Debes aceptar los términos y condiciones").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun escribir_en_el_campo_nombre_debe_limpiar_su_error() {
        mostrarPantalla()
        esperarFormulario()
        composeRule.onNodeWithText("Registrar y ver Resumen").performScrollTo().performClick()
        composeRule.onNodeWithText("El nombre debe tener al menos 3 caracteres").assertExists()

        composeRule.onNode(campo("Nombre")).performScrollTo().performTextInput("Ana")

        composeRule.onNodeWithText("El nombre debe tener al menos 3 caracteres").assertDoesNotExist()
    }

    @Test
    fun el_boton_premium_debe_cambiar_de_texto_y_mostrar_la_tarjeta_premium() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNodeWithText("Modo Premium: DESACTIVADO").performScrollTo().performClick()

        composeRule.onNodeWithText("Modo Premium: ACTIVADO").assertExists()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("¡Beneficios Premium activados!").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("✔ Modo Premium guardado en DataStore").assertExists()
    }

    @Test
    fun el_mensaje_de_exito_debe_desaparecer_despues_de_unos_segundos() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNodeWithText("Modo Premium: DESACTIVADO").performScrollTo().performClick()
        composeRule.onNodeWithText("✔ Modo Premium guardado en DataStore").assertExists()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("✔ Modo Premium guardado en DataStore").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun claves_distintas_deben_mostrar_el_error_de_coincidencia() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNode(campo("Clave")).performScrollTo().performTextInput("secreto123")
        composeRule.onNode(campo("Confirmar clave")).performScrollTo().performTextInput("otra456")

        composeRule.onNodeWithText("Las claves no coinciden").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun debe_mostrar_los_botones_de_registro_con_google_y_apple() {
        mostrarPantalla()
        esperarFormulario()

        composeRule.onNodeWithText("Registrarme con Google").assertIsDisplayed()
        composeRule.onNodeWithText("Registrarme con Apple").assertIsDisplayed()
    }
}
