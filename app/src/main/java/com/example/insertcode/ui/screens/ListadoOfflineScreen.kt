package com.example.insertcode.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.insertcode.data.UsuarioEntity
import com.example.insertcode.ui.components.ImagenInteligente
import com.example.insertcode.viewmodels.UsuarioViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

// Lista los usuarios guardados en ROOM (funciona sin internet)
@Composable
fun ListadoOfflineScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Usuarios registrados (offline)",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChange,
                label = { Text("Buscar por correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (usuarios.isEmpty()) {
                Text(
                    text = "No hay usuarios registrados.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = usuarios, key = { it.id }) { usuario ->
                    UsuarioCard(
                        usuario = usuario,
                        onEliminar = { viewModel.eliminarUsuario(usuario) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UsuarioCard(
    usuario: UsuarioEntity,
    onEliminar: () -> Unit
) {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImagenInteligente(imagenUri = rutaAUri(usuario.fotoPath), tamano = 64.dp)

            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                Text(usuario.correo, style = MaterialTheme.typography.bodyMedium)
                Text(usuario.direccion, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "${if (usuario.modoEspecial) "Premium" else "Básico"} • ${formato.format(usuario.fechaRegistro)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar usuario")
            }
        }
    }
}

// fotoPath puede ser una ruta de archivo (/data/...) o una Uri (content://...)
private fun rutaAUri(path: String?): Uri? = path?.let {
    if (it.startsWith("/")) Uri.fromFile(File(it)) else Uri.parse(it)
}
