package com.example.aplicacionjetpack.utils

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

object ValidationUtils {
    // Acepta tarjetas comunes de 16 dígitos
    private val CARD_PATTERN = Pattern.compile("^[0-9]{16}$")
    // Acepta formato MM/YY y comprueba que la fecha no haya pasado
    private val EXPIRY_DATE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])([0-9]{2})$")
    private val CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$")
    private val EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


    fun isValidCardNumber(cardNumber: String): Boolean {
        return CARD_PATTERN.matcher(cardNumber).matches()
    }

    fun isValidCvv(cvv: String): Boolean {
        return CVV_PATTERN.matcher(cvv).matches()
    }

    fun isValidCardHolder(holder: String): Boolean {
        return holder.trim().length > 2
    }

    fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        if (!EXPIRY_DATE_PATTERN.matcher(expiryDate).matches()) return false
        return try {
            val formatter = DateTimeFormatter.ofPattern("MMyy")
            val expiry = YearMonth.parse(expiryDate, formatter)
            // La tarjeta es válida hasta el final del mes que indica
            !expiry.isBefore(YearMonth.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
