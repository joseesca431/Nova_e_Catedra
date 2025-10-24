package com.example.aplicacionjetpack.model

// Archivo: com.example.aplicacionjetpack.model.Product.kt (o dentro del archivo HomeScreen.kt temporalmente)
data class Product(
    val name: String,
    val price: String,
    // En una app real, esto sería una URL o un Resource ID para fines de mockup
    val imageResId: Int
)
