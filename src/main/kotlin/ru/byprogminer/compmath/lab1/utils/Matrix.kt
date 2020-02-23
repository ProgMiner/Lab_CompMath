package ru.byprogminer.compmath.lab1.utils

class Matrix
private constructor(
        val rows: Int,
        val cols: Int,
        private val matrix: Array<Fraction>
) {

    private val rowAccessors = mutableMapOf<Int, RowAccessor>()
    private val columnAccessors = mutableMapOf<Int, ColumnAccessor>()

    /**
     * Default constructor
     */
    constructor(rows: Int, cols: Int): this(rows, cols, Array(rows * cols) { Fraction.ZERO })

    /**
     * Copy constructor
     */
    constructor(matrix: Matrix): this(matrix.rows, matrix.cols, matrix.matrix.clone())

    /**
     * Swaps two rows in the matrix
     */
    fun swapRows(a: Int, b: Int) {
        if (a >= rows || b >= rows) {
            throw ArrayIndexOutOfBoundsException("row numbers must be less than rows count")
        }

        if (a == b) {
            return
        }

        var tmp: Fraction
        for (col in 0 until cols) {
            tmp = matrix[a * cols + col]
            matrix[a * cols + col] = matrix[b * cols + col]
            matrix[b * cols + col] = tmp
        }
    }

    fun row(row: Int) = rowAccessors.computeIfAbsent(row, this::RowAccessor)
    fun column(column: Int) = columnAccessors.computeIfAbsent(column, this::ColumnAccessor)

    override fun toString() = (0 until rows).map(this::row)
            .joinToString("\n") { (0 until cols).map(it::get).joinToString("\t") }

    operator fun get(i: Int): Fraction = matrix[i]
    operator fun set(i: Int, value: Fraction) { matrix[i] = value }
    operator fun iterator() = matrix.iterator()

    inner class RowAccessor(private val row: Int) {

        operator fun get(column: Int): Fraction = matrix[row * cols + column]
        operator fun set(column: Int, value: Fraction) { matrix[row * cols + column] = value }

        operator fun iterator() = object : Iterator<Fraction> {

            var column = 0

            override fun hasNext() = column < cols
            override fun next() = this@RowAccessor[column++]
        }
    }

    inner class ColumnAccessor(private val column: Int) {

        operator fun get(row: Int): Fraction = matrix[row * cols + column]
        operator fun set(row: Int, value: Fraction) { matrix[row * cols + column] = value }

        operator fun iterator() = object : Iterator<Fraction> {

            var row = 0

            override fun hasNext() = row < rows
            override fun next() = this@ColumnAccessor[row++]
        }
    }
}
