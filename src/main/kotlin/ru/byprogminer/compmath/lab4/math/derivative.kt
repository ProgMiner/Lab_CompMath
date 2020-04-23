package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab4.equation.Expression

private val EPSILON = Fraction(1e-5)

fun derivative(expression: Expression, variable: String, x: Fraction, vars: Map<String, Fraction> = mapOf()) =
        (expression.evaluate(vars + mapOf(variable to x + EPSILON)) -
                expression.evaluate(vars + mapOf(variable to x))) / EPSILON

