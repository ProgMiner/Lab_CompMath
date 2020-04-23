package ru.byprogminer.compmath.lab4.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab4.equation.Expression
import ru.byprogminer.compmath.lab4.equation.InterpretingExpression
import ru.byprogminer.compmath.lab4.equation.InvalidExpression

fun parse(equation: String): Expression = try {
    val cst = parseEquation(equation)

    val variablesSearchListener = VariablesSearchListener()
    ParseTreeWalker().walk(variablesSearchListener, cst)

    InterpretingExpression(equation, variablesSearchListener.variables, cst)
} catch (e: Exception) {
    InvalidExpression(equation)
}

private fun parseEquation(equation: String): ExpressionParser.ExpressionContext {
    val lexer = ExpressionLexer(CharStreams.fromString(equation))

    lexer.removeErrorListeners()
    lexer.addErrorListener(ThrowingErrorListener())

    val parser = ExpressionParser(CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(ThrowingErrorListener())

    return parser.expression()
}
