package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation

val Collection<Equation>.variables: Set<String> get() {
    val vars = map { it.variables }

    return if (vars.isNotEmpty()) {
        vars.reduce { acc, set -> acc + set }
    } else {
        emptySet()
    }
}
