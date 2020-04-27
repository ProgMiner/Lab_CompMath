package ru.byprogminer.compmath.lab4

import ru.byprogminer.compmath.lab4.expression.Expression
import java.awt.Color

data class Store(
        val function: Expression,
        val functionColor: Color,

        val interpolation: Expression,
        val interpolationColor: Color,
        val interpolationPoints: List<Double>,

        val valuePoints: List<Double>,
        val values: Map<Double, Pair<Double, Double>>?,

        val plotAbscissaVariable: String?,
        val plotAbscissaBegin: Double?,
        val plotAbscissaEnd: Double?,
        val plotOrdinateBegin: Double?,
        val plotOrdinateEnd: Double?
) {

    val variables: Set<String>
        get() = try {
            function.variables
        } catch (e: UnsupportedOperationException) {
            // Dummy Kotlin
            @Suppress("RemoveExplicitTypeArguments")
            emptySet<String>()
        }
}
