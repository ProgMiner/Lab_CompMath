package ru.byprogminer.compmath.lab4

import ru.byprogminer.compmath.lab4.expression.Expression
import java.awt.Color

data class Store(
        val function: Expression,
        val functionColor: Color,
        val functionValues: Map<Double, Double>?,

        val interpolation: Expression,
        val interpolationColor: Color,
        val interpolationPoints: List<Double>,
        val interpolationValues: Map<Double, Double>?,

        val valuePoints: List<Double>,

        val plotAbscissaVariable: String?,
        val plotAbscissaBegin: Double?,
        val plotAbscissaEnd: Double?,
        val plotOrdinateBegin: Double?,
        val plotOrdinateEnd: Double?
) {

    val variables: Set<String>
        get() = if (function.isValid) {
            function.variables
        } else {
            emptySet()
        }
}
