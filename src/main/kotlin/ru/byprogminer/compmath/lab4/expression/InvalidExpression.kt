package ru.byprogminer.compmath.lab4.expression

class InvalidExpression(expression: String): AbstractExpression(expression) {

    override val variables
        get() = throw UnsupportedOperationException("expression is invalid")

    override fun evaluate(values: Map<String, Double>): Double {
        throw UnsupportedOperationException("expression is invalid")
    }
}
