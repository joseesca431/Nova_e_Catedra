package com.example.aplicacionjetpack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
// --- 👇👇👇 ¡AÑADIMOS LA IMPORTACIÓN DE COLOR! 👇👇👇 ---
import androidx.compose.ui.graphics.Color
// --- ------------------------------------------- ---
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    // --- Opcional: Para mejorar el modo oscuro ---
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5)
)

// --- 👇👇👇 ¡¡¡LA CORRECCIÓN DE LA VICTORIA ESTÁ AQUÍ!!! 👇👇👇 ---
private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,       // Tu color primario principal (morado oscuro)
    secondary = OrangeAccent,   // Tu color secundario (naranja)
    tertiary = Pink40,          // Mantenemos este por si se usa

    // --- Colores de fondo y superficie ---
    background = Color(0xFFFDFBFF), // Un blanco ligeramente roto, muy común
    surface = Color(0xFFFDFBFF),    // Superficies como Cards usarán este blanco

    // --- Colores del TEXTO (on = sobre) ---
    onPrimary = Color.White,        // Texto sobre un botón primario (morado) será blanco
    onSecondary = Color.White,      // Texto sobre un botón secundario (naranja) será blanco
    onTertiary = Color.White,       // Texto sobre un botón terciario será blanco
    onBackground = Color.Black,     // ¡¡¡EL TEXTO SOBRE EL FONDO SERÁ NEGRO!!!
    onSurface = Color.Black         // ¡¡¡EL TEXTO SOBRE CARDS SERÁ NEGRO!!!
)
// --- ----------------------------------------------------------------- ---

@Composable
fun AplicacionJetpackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color es mejor deshabilitarlo para tener un branding consistente
    dynamicColor: Boolean = false, // <-- CAMBIADO A 'false'
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
