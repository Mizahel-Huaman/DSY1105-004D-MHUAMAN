package com.example.insertcode.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Muestra el selector de cuentas de Google (Credential Manager) y devuelve nombre y correo.
 *
 * @param context debe ser el contexto de la Activity (LocalContext.current en Compose).
 * @param webClientId "Web client ID" de Google Cloud Console.
 */
class GoogleSignInHelper(private val context: Context) {

    suspend fun iniciarSesion(webClientId: String): ResultadoSocial {
        val opcion = GetSignInWithGoogleOption.Builder(webClientId).build()
        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(opcion)
            .build()

        return try {
            val respuesta = CredentialManager.create(context).getCredential(context, solicitud)
            val credencial = respuesta.credential

            if (credencial is CustomCredential &&
                credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(credencial.data)
                // google.id es el correo de la cuenta
                ResultadoSocial.Exito(
                    nombre = google.displayName ?: google.id.substringBefore("@"),
                    correo = google.id
                )
            } else {
                ResultadoSocial.Error("Tipo de credencial no soportado")
            }
        } catch (e: GetCredentialCancellationException) {
            ResultadoSocial.Cancelado
        } catch (e: NoCredentialException) {
            ResultadoSocial.Error("No hay cuentas de Google disponibles en este dispositivo")
        } catch (e: GetCredentialException) {
            ResultadoSocial.Error(e.message ?: "No se pudo iniciar sesión con Google")
        } catch (e: GoogleIdTokenParsingException) {
            ResultadoSocial.Error("Respuesta de Google inválida")
        }
    }
}
