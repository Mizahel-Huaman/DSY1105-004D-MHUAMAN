package com.example.insertcode.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Inicio de sesión con Apple. En Android no existe SDK nativo: Firebase Auth abre el flujo web
 * de "Sign in with Apple" en una pestaña del navegador y devuelve el usuario.
 * Requiere google-services.json y Apple habilitado en Firebase (ver LEEME_LOGIN_SOCIAL.md).
 */
class AppleSignInHelper(private val activity: Activity) {

    suspend fun iniciarSesion(): ResultadoSocial {
        return try {
            val auth = FirebaseAuth.getInstance()
            val proveedor = OAuthProvider.newBuilder("apple.com")
                .setScopes(listOf("email", "name"))
                .build()

            val resultado = auth.startActivityForSignInWithProvider(activity, proveedor).await()
            val usuario = resultado.user
            val correo = usuario?.email
                ?: resultado.additionalUserInfo?.profile?.get("email") as? String
                ?: return ResultadoSocial.Error("Apple no entregó un correo")

            // Apple solo entrega el nombre la primera vez que el usuario autoriza la app
            ResultadoSocial.Exito(
                nombre = usuario?.displayName ?: "",
                correo = correo
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseAuthWebException) {
            if (e.errorCode == "ERROR_WEB_CONTEXT_CANCELED") ResultadoSocial.Cancelado
            else ResultadoSocial.Error(e.message ?: "No se pudo iniciar sesión con Apple")
        } catch (e: IllegalStateException) {
            // FirebaseApp no inicializado: falta google-services.json
            ResultadoSocial.Error("Falta configurar Firebase (google-services.json)")
        } catch (e: Exception) {
            ResultadoSocial.Error(e.message ?: "No se pudo iniciar sesión con Apple")
        }
    }
}
