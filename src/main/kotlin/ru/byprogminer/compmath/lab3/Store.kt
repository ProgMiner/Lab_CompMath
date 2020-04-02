package ru.byprogminer.compmath.lab3

import java.awt.Color

data class Store(
        val mode: Mode,

        val precision: Double?,
        val iterations: Int?,

        val equation: Equation?,
        val equationColor: Color,
        val method: EquationMethod,

        val equations: List<Pair<Equation, Color>>,
        val systemMethod: EquationSystemMethod
) {

    enum class Mode {

        EQUATION, EQUATION_SYSTEM
    }
}
