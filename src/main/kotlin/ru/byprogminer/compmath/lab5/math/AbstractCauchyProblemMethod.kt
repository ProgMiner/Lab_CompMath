package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression

abstract class AbstractCauchyProblemMethod: CauchyProblemMethod {

    override fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            endX: Double,
            precision: Double,
            variableX: String,
            variableY: String
    ): Map<Double, Double> {
        val length = endX - startX

        return solve(function, startX, startY, length, calcStepsCount(length, precision), variableX, variableY)
    }

    abstract fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            length: Double,
            stepsCount: Int,
            variableX: String,
            variableY: String
    ): Map<Double, Double>

    abstract fun calcStepsCount(length: Double, precision: Double): Int
}
