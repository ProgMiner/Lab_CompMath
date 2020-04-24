package ru.byprogminer.compmath.lab4

import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab4.equation.Expression
import java.awt.Color

data class Store(
        val function: Expression,
        val functionColor: Color,

        val interpolation: Expression,
        val interpolationColor: Color,
        val interpolationPoints: List<Fraction>,

        val points: List<Fraction>,
        val pointValues: Map<Fraction, Pair<Fraction, Fraction>>?,

        val plotAbscissaVariable: String?,
        val plotAbscissaBegin: Fraction,
        val plotAbscissaEnd: Fraction,
        val plotOrdinateBegin: Fraction,
        val plotOrdinateEnd: Fraction
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
