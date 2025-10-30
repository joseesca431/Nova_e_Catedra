package com.example.aplicacionjetpack.utils

import java.time.YearMonth
import java.util.regex.Pattern

object ValidationUtils {
    // Regex para Visa, Mastercard, AMEX
    private val CARD_PATTERN = Pattern.compile(
        "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13})$"
    )
    private val EXPIRY_DATE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$")
    private val CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$")

    fun isValidCardNumber(cardNumber: String): Boolean {
        val normalized = cardNumber.replace(" ", "")
        return CARD_PATTERN.matcher(normalized).matches()
    }

    fun isValidCvv(cvv: String): Boolean {
        return CVV_PATTERN.matcher(cvv).matches()
    }

    fun isValidCardHolder(holder: String): Boolean {
        return holder.trim().length > 2
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        val matcher = EXPIRY_DATE_PATTERN.matcher(expiryDate)
        if (!matcher.matches()) return false
        try {
            val mes = matcher.group(1)!!.toInt()
            val anio = matcher.group(2)!!.toInt() + 2000
            return !YearMonth.of(anio, mes).isBefore(YearMonth.now())
        } catch (e: Exception) {
            return false
        }
    }
}