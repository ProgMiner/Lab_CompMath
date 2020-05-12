package ru.byprogminer.compmath.lab5.math

import ru.byprogminer.compmath.lab4.expression.Expression
import kotlin.math.min

object AdamsMethod: OrderedCauchyProblemMethod(4) {

    override fun solve(
            function: Expression,
            startX: Double,
            startY: Double,
            step: Double,
            stepsCount: Int,
            variableX: String,
            variableY: String
    ): Map<Double, Double> {
        val firstStepsCount = min(3, stepsCount)
        val firstValues = RungeKuttaMethod.solve(
                function,
                startX,
                startY,
                step,
                firstStepsCount,
                variableX,
                variableY
        ).toSortedMap(
                (if (step >= 0) {
                    Comparator.naturalOrder<Double>()
                } else {
                    Comparator.reverseOrder()
                }) as Comparator<Double>
        ).toList()

        val points = Array(stepsCount + 1) { i -> firstValues.getOrElse(i) { (startX + i * step) to .0 } }

        if (stepsCount > firstStepsCount) {
            val f = { x: Double, y: Double -> -function.evaluate(mapOf(variableX to x, variableY to y)) }

            val values = Array(stepsCount + 1) { i ->
                when (i < 4) {
                    true -> f(points[i].first, points[i].second)
                    else -> .0
                }
            }

            for (i in 4..stepsCount) {
                val fi = values[i - 1]
                val fi1 = values[i - 2]
                val fi2 = values[i - 3]
                val fi3 = values[i - 4]

                val d1 = fi - fi1
                val d2 = fi - 2 * fi1 + fi2
                val d3 = fi - 3 * fi1 + 3 * fi2 - fi3
                val point = points[i].copy(second = points[i - 1].second +
                        step * (fi + step * (d1 / 2 + step * (5 * d2 / 12 + step * 3 * d3 / 8))))

                points[i] = point
                values[i] = f(point.first, point.second)
            }
        }

        return points.toMap()
    }
}
