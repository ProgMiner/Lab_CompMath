package ru.byprogminer.compmath.lab4.equation

import ru.byprogminer.compmath.lab1.utils.Fraction

class InvalidExpression(equation: String): AbstractExpression(equation) {

    override val variables
        get() = throw UnsupportedOperationException("expression is invalid")

    override fun evaluate(values: Map<String, Fraction>): Fraction {
        throw UnsupportedOperationException("expression is invalid")
    }
}
