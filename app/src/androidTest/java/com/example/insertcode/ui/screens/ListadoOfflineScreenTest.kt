package com.example.insertcode.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.data.UsuarioEntity
import com.example.insertcode.ui.crearViewModelFalso
import com.example.insertcode.viewmodels.UsuarioViewModel
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalTestApi::class)
class ListadoOfflineScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val ana = UsuarioEntity(
        id = 1, nombre = "Ana Pérez", correo = "ana@mail.cl", direccion = "Calle 1",
        modoEspecial = true, fotoPath = null, fechaRegistro = Date()
    )
    private val luis = UsuarioEntity(
        id = 2, nombre = "Luis Soto", correo = "luis@mail.cl", direccion = "Calle 2",
        modoEspecial = false, fotoPath = null, fechaRegistro = Date()
    )

    private fun mostrar(viewModel: UsuarioViewModel) {
        composeRule.setContent {
            MaterialTheme {
                ListadoOfflineScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun debe_mostrar_los_usuarios_guardados_en_room() {
        mostrar(crearViewModelFalso(usuarios = listOf(ana, luis)))

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Ana Pérez").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeRule.onNodeWithText("ana@mail.cl").assertIsDisplayed()
        composeRule.onNodeWithText("Luis Soto").assertIsDisplayed()
        composeRule.onNodeWithText("No hay usuarios registrados.").assertDoesNotExist()
    }

    @Test
    fun sin_registros_debe_mostrar_el_mensaje_de_lista_vacia() {
        mostrar(crearViewModelFalso(usuarios = emptyList()))

        composeRule.onNodeWithText("No hay usuarios registrados.").assertIsDisplayed()
    }
}
