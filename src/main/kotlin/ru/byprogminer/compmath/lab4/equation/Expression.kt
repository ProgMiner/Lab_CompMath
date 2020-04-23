package ru.byprogminer.compmath.lab4.equation

import ru.byprogminer.compmath.lab1.utils.Fraction

interface Expression {

    val variables: Set<String>

    fun evaluate(values: Map<String, Fraction>): Fraction
}
