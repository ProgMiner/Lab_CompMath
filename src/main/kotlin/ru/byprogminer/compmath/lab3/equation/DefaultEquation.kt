package ru.byprogminer.compmath.lab3.equation

abstract class DefaultEquation(
        equation: String,
        override val variables: Set<String>
): AbstractEquation(equation)
