package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation
import java.util.stream.Collectors
import kotlin.math.abs

object SimpleIterationsMethod: EquationMethod {

    override fun solve(equation: Equation, interval: Interval, precision: Precision): Set<Pair<Map<String, Double>, Int>> {
        if (equation.variables.isEmpty()) {
            return emptySet()
        }

        if (equation.variables.size > 1) {
            throw UnsupportedOperationException("equations of several variables is not supported")
        }

        val variable = equation.variables.first()
        return equation.splitRoots(interval).map(selectApproximation(equation, variable))
                .map { c -> simpleIteration(equation, precision, variable, c) }
                .map { (x, i) -> mapOf(variable to x) to i }
                .collect(Collectors.toSet())
    }

    private fun selectApproximation(equation: Equation, variable: String) = { (begin, end): Pair<Double, Double> ->
        selectApproximation(equation, variable, begin, end)
    }

    private fun selectApproximation(equation: Equation, variable: String, begin: Double, end: Double): Double {
        val middle = begin + (end - begin) / 2

        return listOf(
                abs(equation.evaluateAsFunction(mapOf(variable to begin))) to begin,
                abs(equation.evaluateAsFunction(mapOf(variable to middle))) to middle,
                abs(equation.evaluateAsFunction(mapOf(variable to end))) to end
        ).minBy { (f, _) -> f }!!.second
    }

    private tailrec fun simpleIteration(
            equation: Equation,
            precision: Precision,
            variable: String,
            c: Double,
            i: Int = 0
    ): Pair<Double, Int> {
        val x = equation.evaluateAsFunction(mapOf(variable to c)) + c

        if (abs(x - c) < precision.precision || i >= precision.iterations) {
            return x to i
        }

        return simpleIteration(equation, precision, variable, x, i + 1)
    }

    override fun toString() = "Simple iterations method"
}
