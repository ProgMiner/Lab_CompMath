package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression

object RungeKuttaMethod: OrderedCauchyProblemMethod(4) {

    override fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            length: Double,
            stepsCount: Int,
            variableX: String,
            variableY: String
    ): Map<Double, Double> {
        val f = { x: Double, y: Double -> -function.evaluate(mapOf(variableX to x, variableY to y)) }
        val step = length / stepsCount

        return generateSequence(startX to startY) { (prevX, prevY) ->
            val k0 = f(prevX, prevY)
            val k1 = f(prevX + step / 2, prevY + k0 / 2)
            val k2 = f(prevX + step / 2, prevY + k1 / 2)
            val k3 = f(prevX + step, prevY + k2)

            return@generateSequence (prevX + step) to (prevY + step * (k0 + 2 * k1 + 2 * k2 + k3) / 6)
        }.take(stepsCount + 1).toMap()
    }
}
