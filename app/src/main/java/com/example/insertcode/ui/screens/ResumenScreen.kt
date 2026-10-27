package com.example.insertcode.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.insertcode.ui.components.ImagenInteligente
import com.example.insertcode.ui.components.PremiumCard
import com.example.insertcode.viewmodels.UsuarioViewModel

// Observa el MISMO UsuarioViewModel que el registro (compartido en AppNavigation)
@Composable
fun ResumenScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen del Registro",
                style = MaterialTheme.typography.headlineMedium
            )

            ImagenInteligente(imagenUri = uiState.fotoUri)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Nombre: ${uiState.nombre}")
                    Text("Correo: ${uiState.correo}")
                    Text("Dirección: ${uiState.direccion}")
                    Text("Modo: ${if (uiState.modoEspecial) "Premium" else "Básico"}")
                }
            }

            PremiumCard(modoEspecial = uiState.modoEspecial)

            Button(
                onClick = { navController.navigate("listado") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ver listado offline") }

            OutlinedButton(
                onClick = {
                    viewModel.limpiarFormulario()
                    navController.popBackStack(route = "registro", inclusive = false)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Nuevo registro") }
        }
    }
}
