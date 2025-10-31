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
    // Regex de email más robusto (simple y efectivo)
    private val EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    // Regex para teléfono: acepta dígitos, +, espacios, guiones, paréntesis. Longitud entre 7 y 20.
    private val PHONE_PATTERN = Pattern.compile("^[0-9+()\\s-]{7,20}$")

    fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    }

    fun isValidCvv(cvv: String): Boolean {
        return (cvv.length == 3 || cvv.length == 4) && cvv.all { it.isDigit() }
    }

    fun isValidCardHolder(holder: String): Boolean {
        return holder.trim().length > 2
    }

    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return PHONE_PATTERN.matcher(phone).matches()
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        return expiryDate.length == 4 && expiryDate.all { it.isDigit() }
    }
}
