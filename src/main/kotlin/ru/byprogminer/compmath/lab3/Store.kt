package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.equation.Equation
import ru.byprogminer.compmath.lab3.math.EquationMethod
import ru.byprogminer.compmath.lab3.math.EquationSystemMethod
import ru.byprogminer.compmath.lab3.math.variables
import java.awt.Color

data class Store(
        val mode: Mode,

        val begin: Double?,
        val end: Double?,
        val cuts: Int?,

        val precision: Double?,
        val iterations: Int?,

        val equation: Equation,
        val equationColor: Color,
        val method: EquationMethod,

        val startValues: Map<String, Double>,
        val equations: List<Pair<Equation, Color>>,
        val systemMethod: EquationSystemMethod,

        val roots: Set<Pair<Map<String, Double>, Int>>?,

        val plotAbscissaVariable: String?,
        val plotAbscissaBegin: Double,
        val plotAbscissaEnd: Double,
        val plotOrdinateBegin: Double,
        val plotOrdinateEnd: Double,
        val plotMode: PlotMode,
        val plotSlice: Map<String, Double>
) {

    val variables: Set<String>
        get() = when (mode) {
            Mode.EQUATION -> try {
                equation.variables
            } catch (e: UnsupportedOperationException) {
                // Dummy Kotlin
                @Suppress("RemoveExplicitTypeArguments")
                emptySet<String>()
            }

            Mode.EQUATION_SYSTEM -> equations.map { (eq, _) -> eq }.variables
        }

    @Suppress("unused")
    enum class Mode {

        EQUATION, EQUATION_SYSTEM
    }

    enum class PlotMode {

        EQUATIONS, FUNCTIONS
    }
}
