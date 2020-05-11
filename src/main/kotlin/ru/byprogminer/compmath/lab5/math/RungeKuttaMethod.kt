package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression

object RungeKuttaMethod: OrderedCauchyProblemMethod(4) {

    override fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            step: Double,
            stepsCount: Int,
            variableX: String,
            variableY: String
    ): Map<Double, Double> {
        val f = { x: Double, y: Double -> -function.evaluate(mapOf(variableX to x, variableY to y)) }

        return generateSequence(startX to startY) { (prevX, prevY) ->
            val k1 = f(prevX, prevY)
            val k2 = f(prevX + step / 2, prevY + step * k1 / 2)
            val k3 = f(prevX + step / 2, prevY + step * k2 / 2)
            val k4 = f(prevX + step, prevY + step * k3)

            return@generateSequence (prevX + step) to (prevY + step * (k1 + 2 * k2 + 2 * k3 + k4) / 6)
        }.take(stepsCount + 1).toMap()
    }
}
