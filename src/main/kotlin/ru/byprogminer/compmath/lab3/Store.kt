package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.equation.Equation
import ru.byprogminer.compmath.lab3.method.EquationMethod
import ru.byprogminer.compmath.lab3.method.EquationSystemMethod
import java.awt.Color

data class Store(
        val mode: Mode,

        val precision: Double?,
        val iterations: Int?,

        val equation: Equation,
        val equationColor: Color,
        val method: EquationMethod,

        val equations: List<Pair<Equation, Color>>,
        val systemMethod: EquationSystemMethod
) {

    enum class Mode {

        EQUATION, EQUATION_SYSTEM
    }
}
