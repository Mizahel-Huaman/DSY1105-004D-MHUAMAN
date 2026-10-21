package com.example.insertcode.repository

/**
 * Repositorio que almacena y valida los 3 usuarios sintéticos obligatorios del caso.
 */
data class UsuarioPrueba(
    val email: String,
    val pass: String,
    val rol: String,
    val nombre: String
)

class AuthRepository {
    // Lista oficial de credenciales de prueba exigidas en las instrucciones
    private val usuariosAutorizados = listOf(
        UsuarioPrueba("admin@guardian.test", "123456", "Admin", "Administrador General"),
        UsuarioPrueba("supervisor@guardian.test", "123456", "Supervisor", "Supervisor de Terreno"),
        UsuarioPrueba("operador@guardian.test", "123456", "Operador", "Operador de Distribución")
    )

    // Busca si las credenciales coinciden con alguno de los 3 usuarios de prueba
    fun autenticar(email: String, pass: String): UsuarioPrueba? {
        return usuariosAutorizados.find {
            it.email.equals(email.trim(), ignoreCase = true) && it.pass == pass
        }
    }
}