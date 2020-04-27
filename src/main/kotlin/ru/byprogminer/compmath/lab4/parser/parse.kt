package ru.byprogminer.compmath.lab4.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab4.expression.Expression
import ru.byprogminer.compmath.lab4.expression.InterpretingExpression
import ru.byprogminer.compmath.lab4.expression.InvalidExpression

fun parse(expression: String): Expression = try {
    val cst = parseExpression(expression)

    val variablesSearchListener = VariablesSearchListener()
    ParseTreeWalker().walk(variablesSearchListener, cst)

    InterpretingExpression(expression, variablesSearchListener.variables, cst)
} catch (e: Exception) {
    InvalidExpression(expression)
}

private fun parseExpression(expression: String): ExpressionParser.ExpressionContext {
    val lexer = ExpressionLexer(CharStreams.fromString(expression))

    lexer.removeErrorListeners()
    lexer.addErrorListener(ThrowingErrorListener())

    val parser = ExpressionParser(CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(ThrowingErrorListener())

    return parser.expression()
}
