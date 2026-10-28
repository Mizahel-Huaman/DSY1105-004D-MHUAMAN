package com.example.insertcode.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.insertcode.navigation.Screen
import com.example.insertcode.ui.components.ImagenInteligente
import com.example.insertcode.viewmodels.MainViewModel
import com.example.insertcode.viewmodels.PerfilViewModel
import java.io.File

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val items = listOf(Screen.Home, Screen.Profile)
    var selectedItem by remember { mutableIntStateOf(1) }

    val context = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()

    // Variable temporal para guardar la Uri de la captura de cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { perfilViewModel.actualizarImagenUri(it) }
    }

    // Launcher para Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            perfilViewModel.actualizarImagenUri(tempCameraUri)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            viewModel.navigateTo(screen)
                        },
                        label = { Text(text = screen.route) },
                        icon = {
                            Icon(
                                imageVector = if (screen == Screen.Home) Icons.Default.Home else Icons.Default.Person,
                                contentDescription = screen.route
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Perfil de Usuario",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Componente circular reutilizable
            ImagenInteligente(imagenUri = imagenUri)

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acceso a Galería y Cámara
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Text("Galería")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val uri = crearUriTemporal(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                ) {
                    Text("Cámara")
                }
            }
        }
    }
}

// Genera una Uri de archivo válida asegurando la existencia del directorio en caché
private fun crearUriTemporal(context: Context): Uri {
    val directory = File(context.cacheDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val tempFile = File.createTempFile(
        "picture_${System.currentTimeMillis()}",
        ".jpg",
        directory
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}