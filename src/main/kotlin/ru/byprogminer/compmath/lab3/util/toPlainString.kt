package ru.byprogminer.compmath.lab3.util

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.toPlainString(scale: Int = 12): String {
    if (toLong().toDouble() == this) {
        return toLong().toString()
    }

    return BigDecimal.valueOf(this)
            .setScale(scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
}
