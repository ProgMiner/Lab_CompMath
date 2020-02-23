package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.linearsystem.*
import ru.byprogminer.compmath.lab1.utils.Matrix
import ru.byprogminer.compmath.lab1.utils.Fraction

fun main() {
    val system = LinearSystem(
            matrix(3, 3, arrayOf(
                    2, 2, 3,
                    4, 5, 6,
                    7, 8, 9
            )),
            arrayOf(
                    Fraction(7),
                    Fraction(5),
                    Fraction(1)
            )
    )

    calculate(LinearSystem(system))
}

private fun calculate(system: LinearSystem) {
    println("Source matrix:")
    println(system)
    println()

    if (system.A.rows != system.A.cols) {
        println("Count of equations is not equals to count of variables. System does not have a single solution.")
        return
    }

    system.makeTriangle()
    println("Triangle matrix:")
    println(system)
    println()

    val det = system.A.calculateDeterminant()
    println("Determinant: $det.")
    println()

    if (det == Fraction.ZERO) {
        println("Matrix determinant is 0. System does not have a single solution.")
        return
    }

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
        matrix[i] = Fraction(elements[i])
    }

    return matrix
}
