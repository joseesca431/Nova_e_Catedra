package com.example.aplicacionjetpack.data.dto

// --- 👇👇👇 ¡¡¡LA CORRECCIÓN DEFINITIVA Y FINAL!!! 👇👇👇 ---
data class DireccionResponse(
    val idDireccion: Long,
    val alias: String,
    val calle: String,
    val ciudad: String,
    val departamento: String,
    // --- ¡¡¡AÑADIMOS LOS CAMPOS QUE FALTABAN PARA QUE COINCIDA CON TU API!!! ---
    val latitud: Double?,
    val longitud: Double?
)
// --- ------------------------------------------------------------------- ---
