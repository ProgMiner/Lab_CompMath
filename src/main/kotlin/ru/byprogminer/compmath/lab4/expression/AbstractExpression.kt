package ru.byprogminer.compmath.lab4.expression

abstract class AbstractExpression(private val expression: String): Expression {

    override fun toString() = expression
}
