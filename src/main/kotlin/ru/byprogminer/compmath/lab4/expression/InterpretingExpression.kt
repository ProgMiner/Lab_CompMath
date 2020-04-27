package ru.byprogminer.compmath.lab4.expression

import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab4.parser.ExpressionParser
import ru.byprogminer.compmath.lab4.parser.InterpreterListener

class InterpretingExpression(
        expression: String,
        variables: Set<String>,
        private val cst: ExpressionParser.ExpressionContext
): DefaultExpression(expression, variables) {

    override fun evaluate(values: Map<String, Double>): Double {
        val interpreter = InterpreterListener(values)
        ParseTreeWalker().walk(interpreter, cst)

        return interpreter.result
    }
}
