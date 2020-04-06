package ru.byprogminer.compmath.lab3.equation

interface Equation {

    val variables: Set<String>

    fun evaluate(values: Map<String, Double>): Pair<Double, Double>
    fun evaluateAsFunction(values: Map<String, Double>): Double
}
