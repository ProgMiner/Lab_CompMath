package ru.byprogminer.compmath.lab3.math

import ru.byprogminer.compmath.lab3.equation.Equation
import java.util.stream.Stream

fun Equation.splitRoots(interval: Interval): Stream<Pair<Double, Double>> {
    return splitRoots(interval, variables, emptyMap())
}

private fun Equation.splitRoots(interval: Interval, variables: Set<String>, values: Map<String, Double>):
        Stream<Pair<Double, Double>>
{
    val step = (interval.end - interval.begin) / interval.cuts
    val variable = variables.first()

    var current = interval.begin
    val result = Stream.builder<Pair<Double, Double>>()
    if (variables.size == 1) {
        var previousValue = evaluateAsFunction(values + mapOf(variable to current))
        var previous = current

        repeat(interval.cuts) {
            current += step

            val currentValue = evaluateAsFunction(values + mapOf(variable to current))
            if (!previousValue.isNaN() && !currentValue.isNaN() && currentValue * previousValue <= 0) {
                result.add(previous to current)
            }

            previous = current
            previousValue = currentValue
        }
    } else {
        TODO("split values on equations with many variables")
    }

    return result.build()
}
