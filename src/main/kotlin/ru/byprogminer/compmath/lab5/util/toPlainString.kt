package ru.byprogminer.compmath.lab5.util

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.toPlainString(scale: Int = 12): String {
    if (!isFinite()) {
        return toString()
    }

    if (toLong().toDouble() == this) {
        return toLong().toString()
    }

    return BigDecimal.valueOf(this)
            .setScale(scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
}

fun Double?.toPlainString(scale: Int): String? = this?.toPlainString(scale)
fun Double?.toPlainString(): String? = this?.toPlainString()
