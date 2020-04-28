package ru.byprogminer.compmath.lab4.expression

interface Expression {

    val isValid: Boolean
    val variables: Set<String>

    fun evaluate(values: Map<String, Double>): Double
}
