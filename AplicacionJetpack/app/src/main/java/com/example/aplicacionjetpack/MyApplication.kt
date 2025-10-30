package com.example.aplicacionjetpack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
// import com.jakewharton.threetenabp.AndroidThreeTen // Descomenta si usas ThreeTenABP para fechas

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // AndroidThreeTen.init(this) // Descomenta si usas ThreeTenABP (API < 26)
    }
}