package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.utils.Fraction
import java.math.BigInteger

fun main() {
    val system = LinearSystem(
            matrix(3, 3, arrayOf(
                    2, 2, 4,
                    6, 9, 3,
                    3, 8, 2
            )),
            arrayOf(
                    Fraction(BigInteger.valueOf(7), BigInteger.ONE),
                    Fraction(BigInteger.valueOf(5), BigInteger.ONE),
                    Fraction(BigInteger.valueOf(1), BigInteger.ONE)
            )
    )

    println(system)
    println()

    system.makeTriangle()
    println(system)
    println()

    val roots = system.calculateRoots()
    println(roots.joinToString(" "))
    println()

    val residuals = system.calculateResiduals(roots)
    println(residuals.joinToString(" "))
}

private fun matrix(rows: Int, cols: Int, elements: Array<Long>): Matrix {
    val matrix = Matrix(rows, cols)

    for (i in elements.indices) {
        matrix[i] = Fraction(BigInteger.valueOf(elements[i]), BigInteger.ONE)
    }

    return matrix
}
