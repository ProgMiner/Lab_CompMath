package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression

interface CauchyProblemMethod {

    fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            endX: Double,
            precision: Double,
            variableX: String,
            variableY: String
    ): Map<Double, Double>

    fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            step: Double,
            stepsCount: Int,
            variableX: String,
            variableY: String
    ): Map<Double, Double>
}
