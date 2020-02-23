package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.utils.Fraction

/**
 * Calculates residuals of the square system and the found roots
 *
 * @param roots Vector of roots of the specified system
 *
 * @return vector of residuals of the specified roots for the specified linear system
 *
 * @throws IllegalArgumentException when linear system is not square
 */
fun LinearSystem.calculateResiduals(roots: Array<Fraction>): Array<Fraction> {
    if (A.rows != A.cols) {
        throw IllegalArgumentException("residuals for not square linear systems is not defined")
    }

    val residuals = Array(A.rows) { Fraction.ZERO }
    for (row in 0 until A.rows) {
        residuals[row] = roots.mapIndexed { column, x -> x * A.row(row)[column] }
                .reduce { a, b -> a + b } - b[row]
    }

    return residuals
}
