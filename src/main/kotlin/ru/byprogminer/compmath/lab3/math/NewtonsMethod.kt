package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab1.linearsystem.LinearSystem
import ru.byprogminer.compmath.lab1.linearsystem.calculateDeterminant
import ru.byprogminer.compmath.lab1.linearsystem.calculateRoots
import ru.byprogminer.compmath.lab1.linearsystem.makeTriangle
import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab1.utils.Matrix
import ru.byprogminer.compmath.lab3.equation.Equation
import kotlin.math.abs

object NewtonsMethod: EquationSystemMethod {

    override fun solve(
            equations: List<Equation>,
            values: Map<String, Double>,
            precision: Precision
    ): Set<Pair<Map<String, Double>, Int>> {
        try {
            if (equations.variables.all(values::containsKey)) {
                return setOf(solve(equations, values, precision, 0))
            }

            return emptySet()
        } catch (e: Exception) {
            return emptySet()
        }
    }

    private tailrec fun solve(
            equations: List<Equation>,
            values: Map<String, Double>,
            precision: Precision,
            i: Int
    ): Pair<Map<String, Double>, Int> {
        val variables = values.keys.toList()

        val matrix = Matrix(equations.size, values.size)
        for (row in 0 until matrix.rows) {
            for (col in 0 until matrix.cols) {
                val variable = variables[col]

                matrix.row(row)[col] = Fraction(derivative(equations[row], variable, values.getValue(variable), values))
            }
        }

        val vector = equations.map { eq -> Fraction(-eq.evaluateAsFunction(values)) }.toTypedArray()
        val system = LinearSystem(matrix, vector)
        system.makeTriangle()

        if (system.A.calculateDeterminant() == Fraction.ZERO) {
            throw IllegalArgumentException("system doesn't convergence")
        }

        val roots = system.calculateRoots().mapIndexed { v, x -> variables[v] to x.toDouble() }.toMap()
        val end = roots.all { (v, x) -> abs(x - values.getValue(v)) <= precision.precision }

        if (end || i >= precision.iterations) {
            return roots to i
        }

        return solve(equations, roots, precision, i + 1)
    }

    override fun toString() = "Newton's method"
}
