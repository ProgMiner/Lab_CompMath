package ru.byprogminer.compmath.lab3.method

import ru.byprogminer.compmath.lab3.equation.Equation

interface EquationMethod {

    fun solve(equation: Equation): Map<String, Double>
}
