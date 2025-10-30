// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // 1. AÑADE LA DEFINICIÓN DEL PLUGIN DE HILT
    // La versión "2.48" debe coincidir con la que usas en las dependencias de tu app
    id("com.google.dagger.hilt.android") version "2.48" apply false

    // 2. AÑADE LA DEFINICIÓN DEL PLUGIN DE KAPT
    // La versión de Kapt DEBE COINCIDIR con tu versión de Kotlin.
    id("org.jetbrains.kotlin.kapt") version "1.9.23" apply false // <-- ⚠️ REVISA LA NOTA ABAJO
}