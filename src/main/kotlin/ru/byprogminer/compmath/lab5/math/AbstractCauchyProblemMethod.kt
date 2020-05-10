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
        val stepsCount = calcStepsCount(length, precision)

        return solve(function, startX, startY, length / stepsCount, stepsCount, variableX, variableY)
    }

    abstract fun calcStepsCount(length: Double, precision: Double): Int
}
