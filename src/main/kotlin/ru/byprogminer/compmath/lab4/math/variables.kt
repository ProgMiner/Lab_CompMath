package ru.byprogminer.compmath.lab4.math

import ru.byprogminer.compmath.lab4.equation.Expression

val Collection<Expression>.variables: Set<String> get() {

    // Dummy Kotlin
    @Suppress("RemoveExplicitTypeArguments")
    val vars = map { try {
        it.variables
    } catch (e: UnsupportedOperationException) {
        emptySet<String>()
    } }

    return if (vars.isNotEmpty()) {
        vars.reduce { acc, set -> acc + set }
    } else {
        emptySet()
    }
}
