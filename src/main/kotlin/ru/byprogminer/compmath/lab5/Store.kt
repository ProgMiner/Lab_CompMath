package ru.byprogminer.compmath.lab5

import ru.byprogminer.compmath.lab4.expression.Expression
import java.awt.Color

data class Store(
        val expression: Expression,
        val startX: Double?,
        val startY: Double?,
        val endX: Double?,

        val precision: Double?,

        val rungeKuttaSolutionPoints: Map<Double, Double>?,
        val rungeKuttaSolutionInterpolation: Expression,
        val rungeKuttaSolutionInterpolationColor: Color,

        val adamsSolutionPoints: Map<Double, Double>?,
        val adamsSolutionInterpolation: Expression,
        val adamsSolutionInterpolationColor: Color,

        val plotAbscissaBegin: Double?,
        val plotAbscissaEnd: Double?,
        val plotOrdinateBegin: Double?,
        val plotOrdinateEnd: Double?
) {

    companion object {

        const val ABSCISSA_VARIABLE = "x"
        const val ORDINATE_VARIABLE = "y"
    }

    val expressionValid: Boolean
        get() = expression.isValid && expression.variables.all {
            it == ABSCISSA_VARIABLE || it == ORDINATE_VARIABLE
        }
}
