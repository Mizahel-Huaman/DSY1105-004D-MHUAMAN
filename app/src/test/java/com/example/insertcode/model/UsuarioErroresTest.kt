package com.example.insertcode.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UsuarioErroresTest : StringSpec({

    "sin mensajes, hayErrores es false" {
        UsuarioErrores().hayErrores shouldBe false
    }

    "con un solo mensaje, hayErrores es true" {
        UsuarioErrores(clave = "muy corta").hayErrores shouldBe true
        UsuarioErrores(foto = "falta foto").hayErrores shouldBe true
    }
})
