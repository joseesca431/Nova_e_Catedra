package com.example.aplicacionjetpack.utils

import java.math.BigDecimal

class toBigDecimal {

    private fun toBigDecimal(value: Any?): BigDecimal {
        return when (value) {
            null -> BigDecimal.ZERO
            is BigDecimal -> value
            is Number -> BigDecimal.valueOf(value.toDouble())
            is String -> try { BigDecimal(value.trim()) } catch (_: Exception) { BigDecimal.ZERO }
            else -> BigDecimal.ZERO
        }
    }

}