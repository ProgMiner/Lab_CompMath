package ru.byprogminer.compmath.lab4.expression

val Collection<Expression>.variables: Set<String> get() {

    // Dummy Kotlin
    @Suppress("RemoveExplicitTypeArguments")
    val vars = map {
        if (it.isValid) {
            it.variables
        } else {
            emptySet<String>()
        }
    }

    return if (vars.isNotEmpty()) {
        vars.reduce { acc, set -> acc + set }
    } else {
        emptySet()
    }
}
