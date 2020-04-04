package ru.byprogminer.compmath.lab3.equation

interface Equation {

    val variables: Set<String>

    fun calculateAsFunction(values: Map<String, Double>): Double
}
