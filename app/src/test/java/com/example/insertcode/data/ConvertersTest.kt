package com.example.insertcode.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.Date

// Prueba pura de JVM: no necesita Android ni mocks
class ConvertersTest : StringSpec({

    val converters = Converters()

    "dateToTimestamp convierte Date a Long (milisegundos)" {
        converters.dateToTimestamp(Date(1_700_000_000_000L)) shouldBe 1_700_000_000_000L
    }

    "fromTimestamp convierte Long a Date" {
        converters.fromTimestamp(1_700_000_000_000L) shouldBe Date(1_700_000_000_000L)
    }

    "la conversión ida y vuelta conserva la fecha" {
        val original = Date()
        converters.fromTimestamp(converters.dateToTimestamp(original)) shouldBe original
    }

    "los valores nulos se mantienen nulos" {
        converters.dateToTimestamp(null).shouldBeNull()
        converters.fromTimestamp(null).shouldBeNull()
    }
})
