package ru.byprogminer.compmath.lab4.expression

interface Expression {

    val variables: Set<String>

    fun evaluate(values: Map<String, Double>): Double
}
