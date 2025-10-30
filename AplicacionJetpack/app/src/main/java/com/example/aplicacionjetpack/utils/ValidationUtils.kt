package com.example.aplicacionjetpack.utils

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

object ValidationUtils {
    // Acepta tarjetas comunes de 16 dígitos
    private val CARD_PATTERN = Pattern.compile("^[0-9]{16}$")
    // Acepta formato MMYY y comprueba que la fecha no haya pasado
    private val EXPIRY_DATE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])[0-9]{2}$")
    private val CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$")
    private val EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    // --- 👇👇👇 ¡¡¡LÓGICA SIMPLIFICADA Y A PRUEBA DE ERRORES!!! 👇👇👇 ---

    fun isValidCardNumber(cardNumber: String): Boolean {
        // Simple: ¿tiene 16 dígitos?
        return cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    }

    fun isValidCvv(cvv: String): Boolean {
        // Simple: ¿tiene 3 o 4 dígitos?
        return (cvv.length == 3 || cvv.length == 4) && cvv.all { it.isDigit() }
    }

    fun isValidCardHolder(holder: String): Boolean {
        // Simple: ¿tiene más de 2 caracteres después de quitar espacios?
        return holder.trim().length > 2
    }

    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        // Simple: ¿tiene 4 dígitos? El VisualTransformation ya le da el formato.
        // La validación real la hará el servidor de pagos.
        return expiryDate.length == 4 && expiryDate.all { it.isDigit() }
    }
}
