package ru.byprogminer.compmath.lab1

/**
 * Calculates determinant of triangle matrix
 *
 * @return result of multiplying elements on the main diagonal of the matrix
 *
 * @throws IllegalArgumentException when matrix is not square
 */
fun Matrix.calculateDeterminant() = if (rows != cols) {
    throw IllegalArgumentException("determinant of not square matrix is not defined")
} else {
    (0 until rows).map { i -> row(i)[i] }.reduce { a, b -> a * b }
}
