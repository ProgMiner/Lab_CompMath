package ru.byprogminer.compmath.lab4.equation

abstract class AbstractExpression(private val expression: String): Expression {

    override fun toString() = expression
}
