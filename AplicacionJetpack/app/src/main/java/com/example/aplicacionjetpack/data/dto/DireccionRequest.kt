package com.example.aplicacionjetpack.data.dto

// --- 👇👇👇 ¡¡¡LA ÚNICA CORRECCIÓN QUE NECESITAS!!! 👇👇👇 ---
data class DireccionRequest(
    val alias: String,
    val calle: String,
    val ciudad: String,
    val departamento: String,
    // --- ¡¡¡AÑADIMOS LOS CAMPOS QUE FALTABAN!!! ---
    val latitud: Double?,
    val longitud: Double?
)
// --- ---------------------------------------------------- ---
