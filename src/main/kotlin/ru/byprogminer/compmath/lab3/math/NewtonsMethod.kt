package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation

object NewtonsMethod: EquationSystemMethod {

    override fun solve(equations: List<Equation>, interval: Interval, precision: Precision): Set<Pair<Map<String, Double>, Int>> {
        TODO("Not yet implemented")
    }

    override fun toString() = "Newton's method"
}
