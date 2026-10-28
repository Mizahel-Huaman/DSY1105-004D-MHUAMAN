package com.example.insertcode.viewmodels

import android.net.Uri
import com.example.insertcode.data.PreferencesManager
import com.example.insertcode.data.UsuarioEntity
import com.example.insertcode.data.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.util.Date

// Dependencias simuladas + ViewModel bajo prueba
private class Escenario(
    val repo: UsuarioRepository,
    val prefs: PreferencesManager,
    val vm: UsuarioViewModel
)

private fun crearEscenario(
    modoGuardado: Boolean = false,
    usuarios: List<UsuarioEntity> = emptyList()
): Escenario {
    val repo = mockk<UsuarioRepository>()
    val prefs = mockk<PreferencesManager>()
    every { repo.usuarios } returns flowOf(usuarios)
    every { prefs.modoEspecialFlow } returns flowOf(modoGuardado)
    coEvery { prefs.guardarModoEspecial(any()) } just Runs
    return Escenario(repo, prefs, UsuarioViewModel(repo, prefs))
}

private fun usuarioFalso(id: Long, correo: String) = UsuarioEntity(
    id = id, nombre = "Usuario $id", correo = correo, direccion = "Calle $id",
    modoEspecial = false, fotoPath = null, fechaRegistro = Date(0)
)

// Llena el formulario con datos válidos
private fun Escenario.llenarFormularioValido(foto: Uri) {
    vm.onNombreChange("Ana Pérez")
    vm.onCorreoChange("ana@mail.cl")
    vm.onClaveChange("secreto123")
    vm.onConfirmarClaveChange("secreto123")
    vm.onDireccionChange("Av. Siempre Viva 742")
    vm.onTerminosChange(true)
    vm.actualizarFoto(foto)
}

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest : StringSpec({

    // viewModelScope usa Dispatchers.Main, que no existe en un test JVM: lo reemplazamos
    beforeTest { Dispatchers.setMain(StandardTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    // ---------------- Carga inicial ----------------

    "al iniciar, cargando es true y el modo especial es false" {
        runTest {
            val e = crearEscenario(modoGuardado = true)
            e.vm.cargando.value shouldBe true
            e.vm.uiState.value.modoEspecial shouldBe false
        }
    }

    "tras 1500 ms termina la carga y recupera el modo especial desde DataStore" {
        runTest {
            val e = crearEscenario(modoGuardado = true)
            advanceTimeBy(1499)
            runCurrent()
            e.vm.cargando.value shouldBe true

            advanceTimeBy(2)
            runCurrent()
            e.vm.cargando.value shouldBe false
            e.vm.uiState.value.modoEspecial shouldBe true
        }
    }

    // ---------------- Eventos del formulario ----------------

    "los eventos onXChange actualizan el uiState" {
        runTest {
            val e = crearEscenario()
            e.vm.onNombreChange("Ana")
            e.vm.onCorreoChange("ana@mail.cl")
            e.vm.onClaveChange("123456")
            e.vm.onConfirmarClaveChange("123456")
            e.vm.onDireccionChange("Calle 1")
            e.vm.onTerminosChange(true)

            val s = e.vm.uiState.value
            s.nombre shouldBe "Ana"
            s.correo shouldBe "ana@mail.cl"
            s.clave shouldBe "123456"
            s.direccion shouldBe "Calle 1"
            s.aceptaTerminos shouldBe true
        }
    }

    "actualizarFoto guarda la Uri en el uiState" {
        runTest {
            val e = crearEscenario()
            val uri = mockk<Uri>()
            e.vm.actualizarFoto(uri)
            e.vm.uiState.value.fotoUri shouldBe uri
        }
    }

    // ---------------- Validación ----------------

    "validarFormulario con todo vacío devuelve false y marca los 6 errores" {
        runTest {
            val e = crearEscenario()
            e.vm.validarFormulario() shouldBe false

            val err = e.vm.errores.value
            err.nombre.shouldNotBeNull()
            err.correo.shouldNotBeNull()
            err.clave.shouldNotBeNull()
            err.direccion.shouldNotBeNull()
            err.aceptaTerminos.shouldNotBeNull()
            err.foto.shouldNotBeNull()
        }
    }

    "validarFormulario con datos válidos devuelve true y no deja errores" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())

            e.vm.validarFormulario() shouldBe true
            e.vm.errores.value.hayErrores shouldBe false
        }
    }

    "nombre de 2 caracteres es inválido y de 3 es válido" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())

            e.vm.onNombreChange("Al")
            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.nombre.shouldNotBeNull()

            e.vm.onNombreChange("Ali")
            e.vm.validarFormulario() shouldBe true
        }
    }

    "correo sin @ es inválido" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onCorreoChange("correo-sin-arroba")

            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.correo.shouldNotBeNull()
        }
    }

    "clave de 5 caracteres es inválida y de 6 es válida" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())

            e.vm.onClaveChange("12345")
            e.vm.onConfirmarClaveChange("12345")
            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.clave.shouldNotBeNull()

            e.vm.onClaveChange("123456")
            e.vm.onConfirmarClaveChange("123456")
            e.vm.validarFormulario() shouldBe true
        }
    }

    "confirmar clave vacía es inválida" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onConfirmarClaveChange("")

            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.confirmarClave shouldBe "Debes confirmar la clave"
        }
    }

    "si la confirmación no coincide con la clave, el formulario es inválido" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onConfirmarClaveChange("otraClave456")

            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.confirmarClave shouldBe "Las claves no coinciden"

            e.vm.onConfirmarClaveChange("secreto123")
            e.vm.validarFormulario() shouldBe true
        }
    }

    "con Google o Apple no se exige clave ni confirmación" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onClaveChange("")
            e.vm.onConfirmarClaveChange("")
            e.vm.validarFormulario() shouldBe false

            e.vm.onSocialSignInExitoso("Google", "Ana Pérez", "ana@gmail.com")

            e.vm.uiState.value.registradoConSocial shouldBe true
            e.vm.uiState.value.correo shouldBe "ana@gmail.com"
            e.vm.validarFormulario() shouldBe true
        }
    }

    "el registro con Apple guarda el proveedor y no exige clave" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onClaveChange("")
            e.vm.onConfirmarClaveChange("")

            e.vm.onSocialSignInExitoso("Apple", "", "ana@privaterelay.appleid.com")

            e.vm.uiState.value.proveedorSocial shouldBe "Apple"
            e.vm.uiState.value.nombre shouldBe "Ana Pérez" // Apple no siempre entrega nombre: se conserva el escrito
            e.vm.validarFormulario() shouldBe true
        }
    }

    "desvincularSocial vuelve a exigir la clave" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.onSocialSignInExitoso("Google", "Ana Pérez", "ana@gmail.com")

            e.vm.desvincularSocial()

            e.vm.uiState.value.registradoConSocial shouldBe false
            e.vm.uiState.value.correo shouldBe ""
            e.vm.validarFormulario() shouldBe false
        }
    }

    "dirección en blanco, sin términos o sin foto invalidan el formulario" {
        runTest {
            val e = crearEscenario()
            e.llenarFormularioValido(mockk<Uri>())

            e.vm.onDireccionChange("   ")
            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.direccion.shouldNotBeNull()
            e.vm.onDireccionChange("Calle 1")

            e.vm.onTerminosChange(false)
            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.aceptaTerminos.shouldNotBeNull()
            e.vm.onTerminosChange(true)

            e.vm.actualizarFoto(null)
            e.vm.validarFormulario() shouldBe false
            e.vm.errores.value.foto.shouldNotBeNull()
        }
    }

    "editar un campo limpia solo el error de ese campo" {
        runTest {
            val e = crearEscenario()
            e.vm.validarFormulario()

            e.vm.onNombreChange("Ana")

            e.vm.errores.value.nombre.shouldBeNull()
            e.vm.errores.value.correo.shouldNotBeNull()
        }
    }

    // ---------------- Modo especial (DataStore + mensaje) ----------------

    "toggleModoEspecial invierte el modo y lo guarda en DataStore" {
        runTest {
            val e = crearEscenario(modoGuardado = false)
            advanceUntilIdle()

            e.vm.toggleModoEspecial()
            runCurrent()

            e.vm.uiState.value.modoEspecial shouldBe true
            coVerify(exactly = 1) { e.prefs.guardarModoEspecial(true) }

            e.vm.toggleModoEspecial()
            runCurrent()

            e.vm.uiState.value.modoEspecial shouldBe false
            coVerify(exactly = 1) { e.prefs.guardarModoEspecial(false) }
        }
    }

    "mensajeExito se muestra durante 2 segundos y luego se oculta" {
        runTest {
            val e = crearEscenario()
            advanceUntilIdle()
            e.vm.mensajeExito.value shouldBe false

            e.vm.toggleModoEspecial()
            runCurrent()
            e.vm.mensajeExito.value shouldBe true

            advanceTimeBy(1999)
            e.vm.mensajeExito.value shouldBe true

            advanceTimeBy(2)
            runCurrent()
            e.vm.mensajeExito.value shouldBe false
        }
    }

    // ---------------- Guardar en ROOM ----------------

    "guardarEnRoom convierte el uiState en UsuarioEntity y lo inserta" {
        runTest {
            val e = crearEscenario(modoGuardado = true)
            advanceUntilIdle()
            val uri = mockk<Uri>()
            e.llenarFormularioValido(uri)

            val capturada = slot<UsuarioEntity>()
            coEvery { e.repo.guardarFotoLocal(uri) } returns "/data/fotos/foto.jpg"
            coEvery { e.repo.insertar(capture(capturada)) } returns 1L

            var avisado = false
            e.vm.guardarEnRoom { avisado = true }
            advanceUntilIdle()

            capturada.captured.nombre shouldBe "Ana Pérez"
            capturada.captured.correo shouldBe "ana@mail.cl"
            capturada.captured.direccion shouldBe "Av. Siempre Viva 742"
            capturada.captured.modoEspecial shouldBe true
            capturada.captured.fotoPath shouldBe "/data/fotos/foto.jpg"
            avisado shouldBe true
        }
    }

    "guardarEnRoom usa la Uri original si falla la copia de la foto" {
        runTest {
            val e = crearEscenario()
            advanceUntilIdle()
            val uri = mockk<Uri>()
            every { uri.toString() } returns "content://galeria/42"
            e.llenarFormularioValido(uri)

            val capturada = slot<UsuarioEntity>()
            coEvery { e.repo.guardarFotoLocal(uri) } returns null
            coEvery { e.repo.insertar(capture(capturada)) } returns 1L

            e.vm.guardarEnRoom()
            advanceUntilIdle()

            capturada.captured.fotoPath shouldBe "content://galeria/42"
        }
    }

    // ---------------- Limpiar ----------------

    "limpiarFormulario vacía los campos pero conserva el modo especial" {
        runTest {
            val e = crearEscenario(modoGuardado = true)
            advanceUntilIdle()
            e.llenarFormularioValido(mockk<Uri>())
            e.vm.validarFormulario()

            e.vm.limpiarFormulario()

            val s = e.vm.uiState.value
            s.nombre shouldBe ""
            s.correo shouldBe ""
            s.fotoUri.shouldBeNull()
            s.aceptaTerminos shouldBe false
            s.modoEspecial shouldBe true
            e.vm.errores.value.hayErrores shouldBe false
        }
    }

    // ---------------- Listado y búsqueda ----------------

    "usuarios emite la lista de ROOM y cambia al buscar por correo" {
        runTest {
            val todos = listOf(usuarioFalso(1, "ana@mail.cl"), usuarioFalso(2, "luis@mail.cl"))
            val filtrados = listOf(todos[0])
            val e = crearEscenario(usuarios = todos)
            every { e.repo.buscarPorCorreo("ana") } returns flowOf(filtrados)

            // Mientras haya un colector activo, stateIn(WhileSubscribed) mantiene el flujo
            backgroundScope.launch { e.vm.usuarios.collect { } }
            advanceUntilIdle()
            e.vm.usuarios.value shouldContainExactly todos

            e.vm.onBusquedaChange("ana")
            advanceUntilIdle()
            e.vm.usuarios.value shouldContainExactly filtrados
        }
    }

    "eliminarUsuario delega en el repositorio" {
        runTest {
            val e = crearEscenario()
            val usuario = usuarioFalso(5, "x@x.cl")
            coEvery { e.repo.eliminar(usuario) } just Runs

            e.vm.eliminarUsuario(usuario)
            advanceUntilIdle()

            coVerify(exactly = 1) { e.repo.eliminar(usuario) }
        }
    }
})
