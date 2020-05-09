package ru.byprogminer.compmath.lab5.math

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow

abstract class OrderedCauchyProblemMethod(private val order: Int): AbstractCauchyProblemMethod() {

    override fun calcStepsCount(length: Double, precision: Double) =
            ceil(abs(length) / precision.pow(1.0 / order)).toInt()
}
