package ru.byprogminer.compmath.lab3.equation

import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab3.parser.EquationParser
import ru.byprogminer.compmath.lab3.parser.InterpreterListener

class InterpretingEquation(
        equation: String,
        variables: Set<String>,
        private val cst: EquationParser.EquationContext
): DefaultEquation(equation, variables) {

    override fun evaluate(values: Map<String, Double>): Pair<Double, Double> {
        val interpreter = InterpreterListener(values)
        ParseTreeWalker().walk(interpreter, cst)

        return interpreter.left to interpreter.right
    }

    override fun evaluateAsFunction(values: Map<String, Double>): Double {
        val (left, right) = evaluate(values)

        return left - right
    }
}
