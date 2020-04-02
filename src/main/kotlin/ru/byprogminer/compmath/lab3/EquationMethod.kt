package ru.byprogminer.compmath.lab3

interface EquationMethod {

    fun solve(equation: Equation): Map<String, Double>
}
