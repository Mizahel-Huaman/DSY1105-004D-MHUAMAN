package com.example.insertcode.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.insertcode.viewmodels.EstadoViewModel

@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    viewModel: EstadoViewModel = viewModel()
) {
    val estado = viewModel.activo.collectAsState()
    val mostrarMensaje = viewModel.mostrarMensaje.collectAsState()

    // Control con rememberSaveable (Parte 1 y 5)
    var contadorCambios by rememberSaveable { mutableIntStateOf(0) }

    if (estado.value == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val estaActivo = estado.value!!

        val colorAnimado by animateColorAsState(
            targetValue = if (estaActivo) Color(0xFF4CAF50) else Color(0xFFB0BEC5),
            animationSpec = tween(durationMillis = 500),
            label = "colorBoton"
        )

        val colorFondo by animateColorAsState(
            targetValue = if (estaActivo) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
            animationSpec = tween(durationMillis = 700),
            label = "colorFondo"
        )

        val escalaBoton by animateFloatAsState(
            targetValue = if (estaActivo) 1.05f else 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "escalaBoton"
        )

        val textoBoton by remember(key1 = estaActivo) {
            derivedStateOf { if (estaActivo) "Desactivar" else "Activar" }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(all = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (estaActivo) "🌟 MODO ESPECIAL ACTIVO" else "💤 MODO INACTIVO",
                style = MaterialTheme.typography.titleMedium,
                color = if (estaActivo) Color(0xFF2E7D32) else Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.alternarEstado()
                    contadorCambios++
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(escalaBoton)
            ) {
                Text(
                    text = textoBoton,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cambios realizados en esta sesión: $contadorCambios",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = mostrarMensaje.value,
                enter = fadeIn(tween(300)) + expandVertically(),
                exit = fadeOut(tween(300)) + shrinkVertically()
            ) {
                Text(
                    text = "¡Estado guardado exitosamente!",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}