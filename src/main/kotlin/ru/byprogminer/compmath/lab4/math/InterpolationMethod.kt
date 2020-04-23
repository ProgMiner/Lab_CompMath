package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab4.equation.Expression

interface InterpolationMethod {

    fun interpolate(expression: Expression, points: Set<Fraction>): Expression
}
