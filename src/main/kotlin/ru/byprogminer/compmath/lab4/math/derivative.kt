package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab4.equation.Expression

private const val EPSILON = 1e-5

fun derivative(expression: Expression, variable: String, x: Double, vars: Map<String, Double> = mapOf()) =
        (expression.evaluate(vars + mapOf(variable to x + EPSILON)) -
                expression.evaluate(vars + mapOf(variable to x))) / EPSILON

