package ru.byprogminer.compmath.lab1.linearsystem

import ru.byprogminer.compmath.lab1.utils.Matrix
import ru.byprogminer.compmath.lab1.utils.Fraction

class LinearSystem(val A: Matrix, val b: Array<Fraction>) {

    init {
        if (b.size != A.rows) {
            throw IllegalArgumentException("b size must be equals to count of A rows")
        }
    }

    /**
     * Copy constructor
     */
    constructor(system: LinearSystem): this(Matrix(system.A), system.b.clone())

    /**
     * Swaps two equations in the linear system
     */
    fun swapEquations(a: Int, b: Int) {
        if (a == b) {
            return
        }

        A.swapRows(a, b)
        val tmp = this.b[a]
        this.b[a] = this.b[b]
        this.b[b] = tmp
    }

    fun toString(vararg vars: String) = (0 until A.rows).map { A.row(it) to b[it] }
            .joinToString("\n") { (0 until A.cols).map(it.first::get).mapIndexed { i, c -> if (i > 0) {
                "${if (c.toDouble() >= 0) "+" else "-"} ${c.toString().removePrefix("-")} ${vars[i]}"
            } else {
                "$c ${vars[i]}"
            } }.joinToString(" ") + " = ${it.second}" }

    override fun toString() = toString(*Array(A.cols) { "x_${it + 1}" })
}
