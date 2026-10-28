# Configurar "Registrarme con Google" y "Registrarme con Apple"

Todo el código ya está integrado. Falta crear credenciales fuera del proyecto.
Los botones funcionan por separado: puedes configurar solo Google y dejar Apple para después.

## Google (gratis, ~10 min)

1. https://console.cloud.google.com > crea un proyecto.
2. **APIs y servicios > Pantalla de consentimiento de OAuth** (tipo *Externo*). En modo "Prueba",
   agrega tu Gmail como *usuario de prueba*.
3. **Credenciales > Crear credenciales > ID de cliente de OAuth**:
   - Tipo **Android**: package `com.example.insertcode` + SHA-1 de tu keystore debug
     (`./gradlew signingReport`, variante `debug`).
   - Tipo **Aplicación web**: copia su **ID de cliente**.
4. Pégalo en `app/src/main/res/values/strings.xml`:
   `<string name="google_web_client_id" translatable="false">TU_ID.apps.googleusercontent.com</string>`
5. Prueba en un emulador con imagen **Google Play** (o un teléfono) con una cuenta de Google iniciada.

## Apple (requiere cuenta de pago)

Apple en Android no tiene SDK nativo: se usa el flujo web de "Sign in with Apple" a través de
Firebase Auth. Necesitas **Apple Developer Program (US$99/año)**; sin él no se puede probar.

1. En https://developer.apple.com > Certificates, Identifiers & Profiles:
   - Crea un **App ID** con la capacidad *Sign in with Apple*.
   - Crea un **Services ID** (ej. `com.example.insertcode.service`), actívale *Sign in with Apple*
     y deja pendiente la "Return URL" (paso 4).
   - Crea una **Key** con *Sign in with Apple* y descarga el `.p8` (anota el Key ID y tu Team ID).
2. En https://console.firebase.google.com crea un proyecto y agrega una **app Android** con el package
   `com.example.insertcode`. Descarga `google-services.json` y colócalo en `app/google-services.json`.
3. Firebase > **Authentication > Sign-in method > Apple**: actívalo e ingresa el Services ID,
   Team ID, Key ID y el contenido del `.p8`.
4. Copia el "callback URL" que muestra Firebase (termina en `/__/auth/handler`) y pégalo como
   *Return URL* en el Services ID de Apple (con el dominio en "Domains").
5. Sync de Gradle: el plugin de Google Services se aplica solo cuando existe `google-services.json`.

Sin `google-services.json`, el botón de Apple muestra "Falta configurar Firebase" en vez de fallar.

## Notas
- Apple solo entrega el nombre la primera vez que el usuario autoriza la app, y puede entregar un
  correo privado (`@privaterelay.appleid.com`). Si no llega nombre, se conserva el que escribas.
- La app solo usa nombre y correo. Si más adelante validas contra un backend, envía el ID token
  al servidor y verifícalo allí.
