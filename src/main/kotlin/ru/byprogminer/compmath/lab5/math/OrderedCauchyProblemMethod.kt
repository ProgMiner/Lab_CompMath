package ru.byprogminer.compmath.lab5.math

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow

abstract class OrderedCauchyProblemMethod(private val order: Int): AbstractCauchyProblemMethod() {

    init {
        if (order < 0) {
            throw IllegalArgumentException("negative and zero orders is not supported")
        }
    }

    override fun calcStepsCount(length: Double, precision: Double) =
            ceil(abs(length) / precision.pow(1.0 / order)).toInt()
}
