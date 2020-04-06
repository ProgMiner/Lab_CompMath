package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation
import java.util.stream.Collectors
import kotlin.math.abs
import kotlin.math.max

object SimpleIterationsMethod: EquationMethod {

    private const val EPSILON = 1e-5

    override fun solve(equation: Equation, interval: Interval, precision: Precision): Set<Pair<Map<String, Double>, Int>> {
        if (equation.variables.isEmpty()) {
            return emptySet()
        }

        if (equation.variables.size > 1) {
            throw UnsupportedOperationException("equations of several variables is not supported")
        }

        val variable = equation.variables.first()
        return equation.splitRoots(interval).map { (begin, end) ->
            val l = -1 / max(derivative(equation, variable, begin), derivative(equation, variable, end))

            simpleIteration(equation, precision, variable, l, begin + (end - begin) / 2)
        }.map { (x, i) -> mapOf(variable to x) to i }.collect(Collectors.toSet())
    }

    private tailrec fun simpleIteration(
            equation: Equation,
            precision: Precision,
            variable: String,
            l: Double,
            c: Double,
            i: Int = 0
    ): Pair<Double, Int> {
        val x = l * equation.evaluateAsFunction(mapOf(variable to c)) + c

        if (abs(x - c) < precision.precision || i >= precision.iterations) {
            return x to i
        }

        return simpleIteration(equation, precision, variable, l, x, i + 1)
    }

    private fun derivative(equation: Equation, variable: String, x: Double) =
            (equation.evaluateAsFunction(mapOf(variable to x + EPSILON)) -
                    equation.evaluateAsFunction(mapOf(variable to x))) / EPSILON

    override fun toString() = "Simple iterations method"
}
