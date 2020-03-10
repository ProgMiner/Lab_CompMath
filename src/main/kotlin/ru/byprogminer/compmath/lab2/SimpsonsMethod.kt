package ru.byprogminer.compmath.lab2

import kotlin.math.abs

class SimpsonsMethod(val func: (Double) -> Double) {

    companion object {

        private const val EPSILON = 1e-5
    }

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
                getFuncRightValue(realStart),
                getFuncValue(realStart + length / 2),
                getFuncLeftValue(realStart + length)
        )

        cuts = 2
        var result = doCalculate(realStart, length)
        var prevResult: Double

        do {
            prevResult = result

            cuts *= 2
            result = doCalculate(realStart, length)

            if (!result.isFinite()) {
                throw ArithmeticException("integral doesn't convergence")
            }

            error = abs(result - prevResult) / 15
        } while (error > precision)

        return if (end < start) {
            -result
        } else {
            result
        }
    }

    private fun doCalculate(start: Double, length: Double): Double {
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
            values[i] = getFuncValue(start + cutLength * i)
        }

        this.values = values as Array<Double>
    }

    private fun getFuncLeftValue(x: Double): Double {
        val ret = func(x)

        if (!ret.isFinite()) {
            return func(x - EPSILON)
        }

        return ret
    }

    private fun getFuncRightValue(x: Double): Double {
        val ret = func(x)

        if (!ret.isFinite()) {
            return func(x + EPSILON)
        }

        return ret
    }

    private fun getFuncValue(x: Double): Double {
        val ret = func(x)

        if (!ret.isFinite()) {
            return (func(x - EPSILON) + func(x + EPSILON)) / 2
        }

        return ret
    }
}
