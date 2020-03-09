package ru.byprogminer.compmath.lab2

import kotlin.math.abs

class SimpsonsMethod(val func: (Double) -> Double) {

    var cuts = 0
        private set

    var error = .0
        private set

    private lateinit var values: Array<Double>

    fun calculate(start: Double, end: Double, precision: Double): Double {
        val realStart = if (start < end) {
            start
        } else {
            end
        }

        val length = if (end > start) {
            end - start
        } else {
            start - end
        }

        values = arrayOf(
                func(realStart),
                func(realStart + length / 2),
                func(realStart + length)
        )

        var result = doCalculate(realStart, length, 2)
        var prevResult: Double
        cuts = 4

        do {
            prevResult = result
            result = doCalculate(realStart, length, cuts)

            if (!result.isFinite()) {
                throw ArithmeticException("integral doesn't convergence")
            }

            cuts *= 2

            error = abs(result - prevResult) / 15
        } while (error > precision)

        return if (end < start) {
            -result
        } else {
            result
        }
    }

    private fun doCalculate(start: Double, length: Double, cuts: Int): Double {
        val cutLength = length / cuts

        if (cuts > 2) {
            updateValues(start, cutLength)
        }

        return (0 until cuts / 2)
                .map { i -> values[2 * i] + 4 * values[2 * i + 1] + values[2 * i + 2] }
                .reduce { a, b -> a + b } * cutLength / 3
    }

    private fun updateValues(start: Double, cutLength: Double) {
        val values = arrayOfNulls<Double>(2 * this.values.size - 1)

        for (i in values.indices step 2) {
            values[i] = this.values[i / 2]
        }

        for (i in 1 until values.size - 1 step 2) {
            values[i] = func(start + cutLength * i)
        }

        this.values = values as Array<Double>
    }
}
