package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation
import java.util.stream.Collectors
import kotlin.math.abs
import kotlin.math.sign

object BisectionMethod: EquationMethod {

    override fun solve(equation: Equation, interval: Interval, precision: Precision): Set<Pair<Map<String, Double>, Int>> {
        if (equation.variables.isEmpty()) {
            return emptySet()
        }

        if (equation.variables.size > 1) {
            TODO("equations with many variables solving")
        }

        val variable = equation.variables.first()
        return equation.splitRoots(interval).map { (begin, end) -> bisect(equation, precision, variable, begin, end) }
                .map { (x, i) -> mapOf(variable to x) to i }.collect(Collectors.toSet())
    }

    private tailrec fun bisect(
            equation: Equation,
            precision: Precision,
            variable: String,
            begin: Double,
            end: Double,
            i: Int = 0
    ): Pair<Double, Int> {
        val middle = begin + (end - begin) / 2

        val xValue = equation.evaluateAsFunction(mapOf(variable to middle))
        if (abs(end - begin) <= precision.precision || abs(xValue) < precision.precision || i >= precision.iterations) {
            return middle to i
        }

        val aSign = sign(equation.evaluateAsFunction(mapOf(variable to begin)))
        return if (aSign * sign(xValue) > 0) {
            bisect(equation, precision, variable, middle, end, i + 1)
        } else {
            bisect(equation, precision, variable, begin, middle, i + 1)
        }
    }

    override fun toString() = "Bisection method"
}