package ru.byprogminer.compmath.lab2

import kotlin.math.*

private inline fun<T> input(prompt: String = "", map: (String) -> T?): T? {
    var input: T? = null

    do {
        print(prompt)

        try {
            input = map(readLine() ?: return null)
        } catch (e: Exception) {
            println(e.message)
        }
    } while (input == null)

    return input
}

private fun input(prompt: String = "") = input(prompt) { it }

private fun mapDouble(s: String) = s.toDoubleOrNull() ?: throw IllegalArgumentException("Please, enter integer.")

fun main() {
    loop@while (true) {
        println("Welcome to determined integral solver! Select and enter one of equations to calculate:")

        val function = menu(listOf(
                MenuItem("y = sin(x)"),
                MenuItem("y = cos(x)"),
                MenuItem("y = ln(x)"),
                MenuItem("y = log10(x)"),
                MenuItem("y = 1 / x"),
                MenuItem("y = x / |x|"),
                MenuItem("y = sqrt(x)"),
                MenuItem.SEPARATOR,
                MenuItem("Quit")
        ))

        val func: (Double) -> Double = when (function) {
            0 -> ::sin
            1 -> ::cos
            2 -> ::ln
            3 -> ::log10
            4 -> { { x: Double -> 1 / x } }
            5 -> { { x: Double -> x / abs(x) } }
            6 -> ::sqrt

            else -> break@loop
        }

        val start = input("Enter start x value: ", ::mapDouble) ?: break@loop
        val end = input("Enter end x value: ", ::mapDouble) ?: break@loop
        val precision = input("Enter precision: ", ::mapDouble) ?: break@loop

        calculate(start, end, precision, func)
        println()
    }
}

private fun calculate(start: Double, end: Double, precision: Double, func: (Double) -> Double) {
    val method = SimpsonsMethod(func)

    try {
        println("Result: " + method.calculate(start, end, precision))
        println("Result error: ${method.error}")
        println("Count of cuts: ${method.cuts}")
    } catch (e: ArithmeticException) {
        println("Integral doesn't convergence or function has gaps on interval")
    }
}
