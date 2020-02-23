package ru.byprogminer.compmath.lab1.linearsystem

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

        for (row in element + 1 until A.rows) {
            val coefficient = A.column(element)[row] / A.column(element)[element]

            for (column in 0 until A.cols) {
                A.row(row)[column] -= A.row(element)[column] * coefficient
            }

            b[row] -= b[element] * coefficient
        }
    }
}

private fun LinearSystem.selectMainElement(element: Int) {
    swapEquations(element, (element until A.rows).maxBy { A.column(element)[it] } ?: return)
}
