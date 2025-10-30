package com.example.aplicacionjetpack.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.min

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }.take(4)
        val out = buildString {
            if (trimmed.length > 2) {
                append(trimmed.substring(0, 2))
                append('/')
                append(trimmed.substring(2))
            } else {
                append(trimmed)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                return min(offset + 1, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                return min(offset - 1, trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
