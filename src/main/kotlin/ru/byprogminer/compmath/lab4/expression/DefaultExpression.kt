package ru.byprogminer.compmath.lab4.expression

abstract class DefaultExpression(
        expression: String,
        override val variables: Set<String>
): AbstractExpression(expression) {

    override val isValid = true
}
