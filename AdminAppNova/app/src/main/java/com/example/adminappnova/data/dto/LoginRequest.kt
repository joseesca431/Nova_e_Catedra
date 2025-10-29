package com.example.adminappnova.data.dto

data class LoginRequest(
    val username: String, // ¡Asegúrate que los nombres coincidan con tu DTO de Spring!
    val password: String
)