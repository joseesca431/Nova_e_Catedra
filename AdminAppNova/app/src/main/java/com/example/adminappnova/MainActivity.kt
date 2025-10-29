// com/example/adminappnova/MainActivity.kt
package com.example.adminappnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.adminappnova.ui.navigation.AppNavigation
import com.example.adminappnova.ui.theme.AdminAppNovaTheme
import dagger.hilt.android.AndroidEntryPoint // 👈 --- AÑADE ESTA IMPORTACIÓN

@AndroidEntryPoint // 👈 --- AÑADE ESTA ANOTACIÓN
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // ... (tu código de installSplashScreen, etc.)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AdminAppNovaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}