package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation

interface EquationSystemMethod {

    fun solve(equations: List<Equation>, interval: Interval, precision: Precision): Set<Pair<Map<String, Double>, Int>>
}
