plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// El plugin de Google Services exige app/google-services.json (Firebase). Solo se aplica si el
// archivo existe, para que el proyecto compile aunque aún no hayas configurado Firebase.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "com.example.insertcode"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.insertcode"
        minSdk = 26 // O 33 según requieras; 24 permite probarlo en casi cualquier teléfono Android actual
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // Guía 15: evita el error "6 files found with path META-INF/LICENSE.md"
    // (varias librerías de JUnit 5 / MockK traen el mismo archivo de licencia)
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/NOTICE.md",
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/{AL2.0,LGPL2.1}"
            )
        }
    }

    // Guía 15: las pruebas unitarias (src/test) corren con la plataforma JUnit 5
    testOptions {
        unitTests {
            isReturnDefaultValues = true   // las clases android.* devuelven valores por defecto en vez de lanzar error
            all { it.useJUnitPlatform() }  // Obligatorio para usar JUnit 5 / Kotest
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Dependencias extra y Guía 12
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.datastore:datastore-preferences:1.1.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Guía 14: ROOM (runtime + extensiones coroutines/Flow + compilador KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Registro con Google (Credential Manager + "Iniciar sesión con Google")
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Registro con Apple (flujo web OAuth a través de Firebase Auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ---------- Guía 15: Testing ----------
    // Kotest (runner sobre JUnit 5 + aserciones)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    // Permite que los tests JUnit 4 existentes (ExampleUnitTest) sigan corriendo con JUnit 5
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.0")

    // MockK (simular dependencias)
    testImplementation("io.mockk:mockk:1.13.10")

    // Corrutinas: runTest, StandardTestDispatcher, Dispatchers.setMain
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Pruebas de UI (instrumentadas). Compose UI Test ya viene de la BOM (androidTestImplementation / debugImplementation arriba)
    androidTestImplementation("androidx.navigation:navigation-compose:2.7.7")
}
