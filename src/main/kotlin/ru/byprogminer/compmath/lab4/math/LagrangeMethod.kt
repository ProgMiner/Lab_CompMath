package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab4.expression.Expression
import ru.byprogminer.compmath.lab4.expression.InvalidExpression
import ru.byprogminer.compmath.lab4.util.toPlainString

object LagrangeMethod: InterpolationMethod {

    override fun interpolate(expression: Expression, points: Set<Double>): Expression {
        if (expression.variables.size != 1 || points.size < 2) {
            return InvalidExpression("")
        }

        val pointList = points.toList()
        val variable = expression.variables.first()
        val values = pointList.map { x -> expression.evaluate(mapOf(variable to x)) }

        return LagrangePolynomial(pointList, values, variable)
    }

    private class LagrangePolynomial(
            private val points: List<Double>,
            private val values: List<Double>,
            variable: String
    ): Expression {

        override val variables = setOf(variable)

        private val view by lazy {
            val f = values.map { it.toPlainString() }
            val x = points.map { it.toPlainString() }

            f.indices.joinToString(" + ") { i -> "${f[i]} * " + x.indices
                    .filter { j -> j != i }.joinToString(" * ") { j ->
                        "(x - ${x[j]}) / (${x[i]} - ${x[j]})"
                    }
            }
        }

        private val denominator: List<List<Double>> = listOf()

        init {
            // TODO
        }

        override fun evaluate(values: Map<String, Double>): Double {
            val x = values.getValue(variables.first())

            return this.values.mapIndexed { i, f ->
                f * points.indices.filter { j -> j != i }
                        .map { j -> (x - points[j]) / (points[i] - points[j]) }
                        .reduce { a, b -> a * b }
            }.sum()
        }

        override fun toString() = view
    }
}
