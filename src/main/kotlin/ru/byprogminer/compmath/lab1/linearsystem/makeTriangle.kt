package ru.byprogminer.compmath.lab1.linearsystem

import ru.byprogminer.compmath.lab1.utils.Fraction

/**
 * Makes square linear system triangle using Gaussian method with main element selecting
 *
 * @throws IllegalArgumentException when linear system is not square
 */
fun LinearSystem.makeTriangle() {
    if (A.rows != A.cols) {
        throw IllegalArgumentException("linear system is not square")
    }

    for (element in 0 until A.cols - 1) {
        selectMainElement(element)

        if (A.column(element)[element] == Fraction.ZERO) {
            continue
        }

        for (row in element + 1 until A.rows) {
            val coefficient = A.column(element)[row] / A.column(element)[element]

            for (column in element until A.cols) {
                A.column(column)[row] -= A.column(column)[element] * coefficient
            }

            b[row] -= b[element] * coefficient
        }
    }
}

private fun LinearSystem.selectMainElement(element: Int) {
    swapEquations(element, (element until A.rows).maxBy { A.column(element)[it] } ?: return)
}
