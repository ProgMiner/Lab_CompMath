package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation

interface EquationMethod {

    fun solve(equation: Equation, interval: Interval, precision: Precision): Set<Map<String, Double>>
}
