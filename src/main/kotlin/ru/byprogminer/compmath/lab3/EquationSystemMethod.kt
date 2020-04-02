package ru.byprogminer.compmath.lab3

interface EquationSystemMethod {

    fun solve(equations: List<Equation>): Map<String, Double>
}
