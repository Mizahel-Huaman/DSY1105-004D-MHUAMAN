package com.example.insertcode.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
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
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Date
import kotlin.io.path.createTempDirectory

private fun usuarioFalso(id: Long, correo: String) = UsuarioEntity(
    id = id,
    nombre = "Usuario $id",
    correo = correo,
    direccion = "Calle $id",
    modoEspecial = false,
    fotoPath = null,
    fechaRegistro = Date(0)
)

class UsuarioRepositoryTest : StringSpec({

    "usuarios entrega la lista que emite el DAO" {
        val lista = listOf(usuarioFalso(1, "a@a.cl"), usuarioFalso(2, "b@b.cl"))
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(lista)
        val repo = UsuarioRepository(dao, mockk<Context>())

        runTest {
            repo.usuarios.first() shouldContainExactly lista
        }
    }

    "buscarPorCorreo delega la consulta al DAO" {
        val resultado = listOf(usuarioFalso(1, "ana@mail.cl"))
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        every { dao.buscarPorCorreo("ana") } returns flowOf(resultado)
        val repo = UsuarioRepository(dao, mockk<Context>())

        runTest {
            repo.buscarPorCorreo("ana").first() shouldContainExactly resultado
        }
        verify(exactly = 1) { dao.buscarPorCorreo("ana") }
    }

    "insertar delega en el DAO y devuelve el id generado" {
        val usuario = usuarioFalso(0, "nuevo@mail.cl")
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        coEvery { dao.insertar(usuario) } returns 7L
        val repo = UsuarioRepository(dao, mockk<Context>())

        runTest {
            repo.insertar(usuario) shouldBe 7L
        }
        coVerify(exactly = 1) { dao.insertar(usuario) }
    }

    "eliminar delega en el DAO" {
        val usuario = usuarioFalso(3, "x@x.cl")
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        coEvery { dao.eliminar(usuario) } just Runs
        val repo = UsuarioRepository(dao, mockk<Context>())

        runTest { repo.eliminar(usuario) }
        coVerify(exactly = 1) { dao.eliminar(usuario) }
    }

    "guardarFotoLocal copia la imagen a filesDir/fotos y devuelve su ruta" {
        val carpetaApp = createTempDirectory("filesDir").toFile()
        val uri = mockk<Uri>()
        val resolver = mockk<ContentResolver>()
        val context = mockk<Context>()
        every { context.filesDir } returns carpetaApp
        every { context.contentResolver } returns resolver
        every { resolver.openInputStream(uri) } returns ByteArrayInputStream("contenido-foto".toByteArray())
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        val repo = UsuarioRepository(dao, context)

        runTest {
            val ruta = repo.guardarFotoLocal(uri).shouldNotBeNull()
            val archivo = File(ruta)
            archivo.exists() shouldBe true
            archivo.parentFile?.name shouldBe "fotos"
            archivo.readText() shouldBe "contenido-foto"
        }
        carpetaApp.deleteRecursively()
    }

    "guardarFotoLocal devuelve null si no se puede abrir la imagen" {
        val carpetaApp = createTempDirectory("filesDir").toFile()
        val uri = mockk<Uri>()
        val resolver = mockk<ContentResolver>()
        val context = mockk<Context>()
        every { context.filesDir } returns carpetaApp
        every { context.contentResolver } returns resolver
        every { resolver.openInputStream(uri) } returns null
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        val repo = UsuarioRepository(dao, context)

        runTest { repo.guardarFotoLocal(uri).shouldBeNull() }
        carpetaApp.deleteRecursively()
    }

    "guardarFotoLocal devuelve null (sin crashear) si falla la lectura" {
        val carpetaApp = createTempDirectory("filesDir").toFile()
        val uri = mockk<Uri>()
        val resolver = mockk<ContentResolver>()
        val context = mockk<Context>()
        every { context.filesDir } returns carpetaApp
        every { context.contentResolver } returns resolver
        every { resolver.openInputStream(uri) } throws SecurityException("sin permiso")
        val dao = mockk<UsuarioDao>()
        every { dao.getUsuariosFlow() } returns flowOf(emptyList())
        val repo = UsuarioRepository(dao, context)

        runTest { repo.guardarFotoLocal(uri).shouldBeNull() }
        carpetaApp.deleteRecursively()
    }
})
