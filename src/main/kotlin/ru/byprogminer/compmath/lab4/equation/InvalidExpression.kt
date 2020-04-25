package ru.byprogminer.compmath.lab4.equation

class InvalidExpression(equation: String): AbstractExpression(equation) {

    override val variables
        get() = throw UnsupportedOperationException("expression is invalid")

    override fun evaluate(values: Map<String, Double>): Double {
        throw UnsupportedOperationException("expression is invalid")
    }
}
