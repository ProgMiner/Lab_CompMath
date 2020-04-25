package ru.byprogminer.compmath.lab4.equation

import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab4.parser.ExpressionParser
import ru.byprogminer.compmath.lab4.parser.InterpreterListener

class InterpretingExpression(
        equation: String,
        variables: Set<String>,
        private val cst: ExpressionParser.ExpressionContext
): DefaultExpression(equation, variables) {

    override fun evaluate(values: Map<String, Double>): Double {
        val interpreter = InterpreterListener(values)
        ParseTreeWalker().walk(interpreter, cst)

        return interpreter.result
    }
}
