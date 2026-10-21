package com.example.insertcode.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.insertcode.viewmodels.LoginViewModel

/**
 * Pantallas diferenciadas por rol que observan el mismo LoginViewModel compartido
 * mediante collectAsState() sin pasar argumentos por ruta (Requisito Guía 11).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRolScreen(
    tituloPantalla: String,
    descripcionRol: String,
    tareasPermitidas: List<String>,
    loginViewModel: LoginViewModel,
    onCerrarSesion: () -> Unit
) {
    // Observamos el LoginViewModel compartido para obtener el correo y rol activo
    val estado by loginViewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloPantalla) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta con datos del usuario obtenidos desde el ViewModel compartido
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sesión Activa - Rol: ${estado.rolActivo.ifEmpty { "Autorizado" }}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Usuario: ${estado.nombreUsuario}")
                    Text("Correo: ${estado.email}")
                }
            }

            Text(
                text = descripcionRol,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Funciones habilitadas en terreno:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Listado de capacidades específicas según el rol
            tareasPermitidas.forEach { tarea ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• $tarea", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    loginViewModel.cerrarSesion()
                    onCerrarSesion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cerrar Sesión Segura")
            }
        }
    }
}

@Composable
fun HomeAdminScreen(loginViewModel: LoginViewModel, onLogout: () -> Unit) {
    HomeRolScreen(
        tituloPantalla = "Panel Administrador",
        descripcionRol = "Control total de usuarios de prueba, roles, permisos y configuración del sistema.",
        tareasPermitidas = listOf(
            "Gestionar usuarios sintéticos (Admin, Supervisor, Operador)",
            "Configurar permisos de acceso móvil y sucursales",
            "Auditar registros de trazabilidad y estado de servidores"
        ),
        loginViewModel = loginViewModel,
        onCerrarSesion = onLogout
    )
}

@Composable
fun HomeSupervisorScreen(loginViewModel: LoginViewModel, onLogout: () -> Unit) {
    HomeRolScreen(
        tituloPantalla = "Dashboard Supervisor",
        descripcionRol = "Monitoreo de indicadores comerciales, logísticos y cumplimiento de entregas.",
        tareasPermitidas = listOf(
            "KPI Ventas del Día: $1.345.000 CLP (Meta: 92%)",
            "Pedidos en Ruta: 8 activos | Entregados: 14",
            "Supervisar alertas de retraso en distribución"
        ),
        loginViewModel = loginViewModel,
        onCerrarSesion = onLogout
    )
}

@Composable
fun HomeOperadorScreen(loginViewModel: LoginViewModel, onLogout: () -> Unit) {
    val estadosPedidos = remember {
        mutableStateListOf(
            "PED-101 • Comercial Andes SpA -> En Ruta",
            "PED-102 • Minimarket El Sol -> Pendiente",
            "PED-103 • Ferretería Central -> Entregado"
        )
    }

    HomeRolScreen(
        tituloPantalla = "Ruta de Operador / Repartidor",
        descripcionRol = "Consulta de hoja de ruta asignada y actualización de estados de entrega en terreno.",
        tareasPermitidas = estadosPedidos,
        loginViewModel = loginViewModel,
        onCerrarSesion = onLogout
    )
}