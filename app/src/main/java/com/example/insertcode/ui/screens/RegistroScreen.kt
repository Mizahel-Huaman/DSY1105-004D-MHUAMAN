package com.example.insertcode.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.insertcode.viewmodels.UsuarioViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Campo Nombre
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text(text = "Nombre") },
            isError = estado.errores.nombre != null,
            supportingText = {
                estado.errores.nombre?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Correo
        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text(text = "Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Clave
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.clave != null,
            supportingText = {
                estado.errores.clave?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Dirección
        OutlinedTextField(
            value = estado.direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text(text = "Dirección") },
            isError = estado.errores.direccion != null,
            supportingText = {
                estado.errores.direccion?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Checkbox: aceptar términos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.aceptaTerminos,
                onCheckedChange = viewModel::onAceptaTerminosChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Acepto los términos y condiciones")
        }

        // Botón Enviar
        Button(
            onClick = {
                if (viewModel.validarFormulario()) {
                    navController.navigate(route = "resumen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrar")
        }
    }
}
