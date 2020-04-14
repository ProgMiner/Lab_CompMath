package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation

private const val EPSILON = 1e-5

fun derivative(equation: Equation, variable: String, x: Double, vars: Map<String, Double> = mapOf()) =
        (equation.evaluateAsFunction(vars + mapOf(variable to x + EPSILON)) -
                equation.evaluateAsFunction(vars + mapOf(variable to x))) / EPSILON

