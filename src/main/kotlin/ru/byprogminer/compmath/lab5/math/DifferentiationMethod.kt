package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression

interface DifferentiationMethod {

    fun differentiate(
            function: Expression,
            startX: Double,
            startY: Double,
            endX: Double,
            precision: Double
    ): Map<Double, Double>
}
