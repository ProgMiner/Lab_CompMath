package ru.byprogminer.compmath.lab3.equation

class InvalidEquation(equation: String): AbstractEquation(equation) {

    override val variables
        get() = throw UnsupportedOperationException("equation is invalid")

    override fun calculateAsFunction(values: Map<String, Double>): Double {
        throw UnsupportedOperationException("equation is invalid")
    }
}