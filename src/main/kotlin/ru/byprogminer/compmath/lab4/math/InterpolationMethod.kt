package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab4.expression.Expression

interface InterpolationMethod {

    fun interpolate(expression: Expression, points: Set<Double>): Expression
}
