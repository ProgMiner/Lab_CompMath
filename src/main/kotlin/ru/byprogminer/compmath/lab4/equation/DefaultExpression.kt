package ru.byprogminer.compmath.lab4.equation

abstract class DefaultExpression(
        expression: String,
        override val variables: Set<String>
): AbstractExpression(expression)
