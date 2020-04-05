package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.equation.Equation
import ru.byprogminer.compmath.lab3.math.EquationMethod
import ru.byprogminer.compmath.lab3.math.EquationSystemMethod
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
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

        val equations: List<Pair<Equation, Color>>,
        val systemMethod: EquationSystemMethod,

        val roots: ReactiveHolder<Set<Map<String, Double>>>
) {

    @Suppress("unused")
    enum class Mode {

        EQUATION, EQUATION_SYSTEM
    }
}
