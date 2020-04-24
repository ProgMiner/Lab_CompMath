package ru.byprogminer.compmath.lab1.utils

fun String.toFractionOrNull() = try {
    Fraction(this)
} catch (e: Exception) {
    null
}
