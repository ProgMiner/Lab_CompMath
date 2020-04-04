package ru.byprogminer.compmath.lab3.method

import ru.byprogminer.compmath.lab3.equation.Equation

interface EquationSystemMethod {

    fun solve(equations: List<Equation>): Map<String, Double>
}
