package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.utils.Fraction

fun main() {
    val system = LinearSystem(
            matrix(3, 3, arrayOf(
                    2, 2, 4,
                    6, 9, 3,
                    3, 8, 2
            )),
            arrayOf(
                    Fraction.valueOf(7),
                    Fraction.valueOf(5),
                    Fraction.valueOf(1)
            )
    )

    calculate(system)
}

private fun calculate(system: LinearSystem) {
    println("Source matrix:")
    println(system)
    println()

    system.makeTriangle()
    println("Determinant: ${system.A.calculateDeterminant()}")
    println()

    println("Triangle matrix:")
    println(system)
    println()

    println("Roots:")
    val roots = system.calculateRoots()
    println(roots.mapIndexed { i, x -> "x_$i = $x" }.joinToString("\n"))
    println()

    println("Residuals:")
    val residuals = system.calculateResiduals(roots)
    println(residuals.mapIndexed { i, r -> "r_$i = $r" }.joinToString("\n"))
}

private fun matrix(rows: Int, cols: Int, elements: Array<Long>): Matrix {
    val matrix = Matrix(rows, cols)

    for (i in elements.indices) {
        matrix[i] = Fraction.valueOf(elements[i])
    }

    return matrix
}
