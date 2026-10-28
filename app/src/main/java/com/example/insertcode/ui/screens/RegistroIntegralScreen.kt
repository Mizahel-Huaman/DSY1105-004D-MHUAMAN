package com.example.insertcode.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.insertcode.R
import android.app.Activity
import android.content.ContextWrapper
import com.example.insertcode.auth.AppleSignInHelper
import com.example.insertcode.auth.GoogleSignInHelper
import com.example.insertcode.auth.ResultadoSocial
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.insertcode.ui.components.ImagenInteligente
import com.example.insertcode.ui.components.PremiumCard
import com.example.insertcode.viewmodels.UsuarioViewModel
import java.io.File

@Composable
fun RegistroIntegralScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()

    val context = LocalContext.current

    // rememberSaveable: sobrevive a rotación de pantalla / recreación de la Activity
    var tempCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Registro con Google/Apple: mensaje de error / aviso (sobrevive a la rotación)
    var mensajeSocial by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val webClientId = context.getString(R.string.google_web_client_id)

    // Estado local del botón premium (también sobrevive a la rotación)
    var premiumActivo by rememberSaveable { mutableStateOf(false) }
    // Se sincroniza con el valor real cargado desde DataStore
    LaunchedEffect(uiState.modoEspecial) { premiumActivo = uiState.modoEspecial }

    // Texto derivado del estado del botón
    val textoDerivado by remember {
        derivedStateOf { if (premiumActivo) "Modo Premium: ACTIVADO" else "Modo Premium: DESACTIVADO" }
    }

    // Animación de color: gris -> verde #2E7D32
    val colorBoton by animateColorAsState(
        targetValue = if (premiumActivo) Color(0xFF2E7D32) else Color.Gray,
        animationSpec = tween(durationMillis = 600),
        label = "colorBotonPremium"
    )

    // Launcher Galería (si el usuario cancela, uri llega null y no pasa nada)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.actualizarFoto(uri)
    }

    // Launcher Cámara (si el usuario cancela, success = false y no pasa nada)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) tempCameraUri?.let { viewModel.actualizarFoto(it) }
    }

    Scaffold { innerPadding ->
        if (cargando) {
            // Carga inicial simulada (delay 1500 ms en el ViewModel)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                    text = "Registro Integral",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Imagen centrada al inicio
                ImagenInteligente(imagenUri = uiState.fotoUri)
                errores.foto?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                // Botones Cámara / Galería
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val uri = crearUriCamara(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cámara") }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Galería") }
                }

                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre") },
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = { Text("Correo electrónico") },
                    isError = errores.correo != null,
                    supportingText = { errores.correo?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    readOnly = uiState.registradoConSocial, // el correo lo define la cuenta de Google/Apple
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Con Google/Apple no se pide clave (la autenticación la hace el proveedor)
                if (!uiState.registradoConSocial) {
                    OutlinedTextField(
                        value = uiState.clave,
                        onValueChange = viewModel::onClaveChange,
                        label = { Text("Clave") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errores.clave != null,
                        supportingText = { errores.clave?.let { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Verificación: debe ser igual a la clave de arriba
                    val noCoinciden = uiState.confirmarClave.isNotEmpty() &&
                        uiState.confirmarClave != uiState.clave
                    val errorConfirmar = errores.confirmarClave
                        ?: if (noCoinciden) "Las claves no coinciden" else null

                    OutlinedTextField(
                        value = uiState.confirmarClave,
                        onValueChange = viewModel::onConfirmarClaveChange,
                        label = { Text("Confirmar clave") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errorConfirmar != null,
                        supportingText = { errorConfirmar?.let { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = uiState.direccion,
                    onValueChange = viewModel::onDireccionChange,
                    label = { Text("Dirección") },
                    isError = errores.direccion != null,
                    supportingText = { errores.direccion?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Términos y condiciones
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.aceptaTerminos,
                            onCheckedChange = viewModel::onTerminosChange
                        )
                        Text("Acepto los términos y condiciones")
                    }
                    errores.aceptaTerminos?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }

                // Botón modo premium: color animado + texto derivado
                Button(
                    onClick = {
                        premiumActivo = !premiumActivo
                        viewModel.toggleModoEspecial()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(textoDerivado)
                }

                // Mensaje de éxito (2 s) al guardar en DataStore
                AnimatedVisibility(visible = mensajeExito) {
                    Text(
                        text = if (premiumActivo) "✔ Modo Premium guardado en DataStore"
                        else "✔ Modo Básico guardado en DataStore",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Tarjeta premium animada
                PremiumCard(modoEspecial = uiState.modoEspecial)

                // Validar -> guardar en ROOM -> navegar a resumen
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            viewModel.guardarEnRoom {
                                navController.navigate("resumen")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar y ver Resumen")
                }

                // ---------- Registro con Google / Apple ----------
                if (uiState.registradoConSocial) {
                    Text(
                        text = "✔ Cuenta de ${uiState.proveedorSocial} vinculada: ${uiState.correo}",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { viewModel.desvincularSocial() }) {
                        Text("Registrarme con clave en su lugar")
                    }
                } else {
                    Text(
                        text = "o regístrate con",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedButton(
                        onClick = {
                            mensajeSocial = null
                            if (webClientId.startsWith("REEMPLAZAR")) {
                                mensajeSocial = "Falta configurar google_web_client_id en strings.xml"
                            } else {
                                scope.launch {
                                    when (val r = GoogleSignInHelper(context).iniciarSesion(webClientId)) {
                                        is ResultadoSocial.Exito ->
                                            viewModel.onSocialSignInExitoso("Google", r.nombre, r.correo)
                                        is ResultadoSocial.Error -> mensajeSocial = r.mensaje
                                        ResultadoSocial.Cancelado -> Unit
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Registrarme con Google") }

                    OutlinedButton(
                        onClick = {
                            mensajeSocial = null
                            val activity = context.buscarActivity()
                            if (activity == null) {
                                mensajeSocial = "No se pudo abrir el inicio de sesión con Apple"
                            } else {
                                scope.launch {
                                    when (val r = AppleSignInHelper(activity).iniciarSesion()) {
                                        is ResultadoSocial.Exito ->
                                            viewModel.onSocialSignInExitoso("Apple", r.nombre, r.correo)
                                        is ResultadoSocial.Error -> mensajeSocial = r.mensaje
                                        ResultadoSocial.Cancelado -> Unit
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Registrarme con Apple") }

                    mensajeSocial?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }

                TextButton(onClick = { navController.navigate("listado") }) {
                    Text("Ver listado offline")
                }
            }
        }
    }
}

// Crea un archivo temporal en getExternalFilesDir y devuelve su Uri vía FileProvider
private fun crearUriCamara(context: Context): Uri {
    val directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    val archivo = File.createTempFile("foto_${System.currentTimeMillis()}_", ".jpg", directorio)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

// Obtiene la Activity desde un Context (puede venir envuelto en ContextWrapper)
private fun Context.buscarActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
