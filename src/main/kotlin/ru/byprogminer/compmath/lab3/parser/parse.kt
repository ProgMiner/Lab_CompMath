package ru.byprogminer.compmath.lab3.parser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.byprogminer.compmath.lab3.equation.Equation
import ru.byprogminer.compmath.lab3.equation.InterpretingEquation
import ru.byprogminer.compmath.lab3.equation.InvalidEquation

fun parse(equation: String): Equation = try {
    val cst = parseEquation(equation)

    val variablesSearchListener = VariablesSearchListener()
    ParseTreeWalker().walk(variablesSearchListener, cst)

    InterpretingEquation(equation, variablesSearchListener.variables, cst)
} catch (e: Exception) {
    InvalidEquation(equation)
}

private fun parseEquation(equation: String): EquationParser.EquationContext {
    val lexer = EquationLexer(CharStreams.fromString(equation))

    lexer.removeErrorListeners()
    lexer.addErrorListener(ThrowingErrorListener())

    val parser = EquationParser(CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(ThrowingErrorListener())

    return parser.equation()
}
