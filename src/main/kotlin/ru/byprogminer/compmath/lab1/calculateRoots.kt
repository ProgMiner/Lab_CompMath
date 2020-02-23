package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.utils.Fraction

/**
 * Calculates roots of the triangle system of linear equations using Gaussian method
 */
fun LinearSystem.calculateRoots(): Array<Fraction> {
    val roots = Array(A.cols) { Fraction.ZERO }

    for (element in A.rows - 1 downTo 0) {
        var value = b[element]

        for (column in element + 1 until A.cols) {
            value -= A.row(element)[column] * roots[column]
        }

        roots[element] = value / A.row(element)[element]
    }

    return roots
}
