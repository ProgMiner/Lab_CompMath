package ru.byprogminer.compmath.lab3.parser

import java.util.*
import kotlin.math.*
import kotlin.properties.Delegates

class InterpreterListener(private val values: Map<String, Double>): EquationBaseListener() {

    private val stack = Stack<Double>()

    val result: Double
        get() = stack.pop()

    var left by Delegates.notNull<Double>()
        private set

    var right by Delegates.notNull<Double>()
        private set

    override fun exitEquation(ctx: EquationParser.EquationContext) {
        val (left, right) = popLeftRight()

        this.left = left
        this.right = right
        stack.push(left - right)
    }

    override fun exitExprAbs(ctx: EquationParser.ExprAbsContext?) {
        stack.push(stack.pop().absoluteValue)
    }

    override fun exitExprUnaryMinus(ctx: EquationParser.ExprUnaryMinusContext) {
        stack.push(-stack.pop())
    }

    override fun exitExprPower(ctx: EquationParser.ExprPowerContext) {
        val (left, right) = popLeftRight()

        stack.push(left.pow(right))
    }

    override fun exitExprMultiplyDivide(ctx: EquationParser.ExprMultiplyDivideContext) {
        val (left, right) = popLeftRight()

        stack.push(when (ctx.op.text) {
            "*" -> left * right
            "/" -> left / right

            else -> throw IllegalArgumentException()
        })
    }

    override fun exitExprPlusMinus(ctx: EquationParser.ExprPlusMinusContext) {
        val (left, right) = popLeftRight()

        stack.push(when (ctx.op.text) {
            "+" -> left + right
            "-" -> left - right

            else -> throw IllegalArgumentException()
        })
    }

    override fun exitExprFunction(ctx: EquationParser.ExprFunctionContext) {
        val operand = stack.pop()

        stack.push(when (ctx.op.text) {
            "sqrt" -> sqrt(operand)
            "sin" -> sin(operand)
            "cos" -> cos(operand)
            "tan" -> tan(operand)
            "asin" -> asin(operand)
            "acos" -> acos(operand)
            "atan" -> atan(operand)
            "log", "ln" -> ln(operand)
            "lg" -> log10(operand)

            else -> throw IllegalArgumentException()
        })
    }

    override fun exitExprLog(ctx: EquationParser.ExprLogContext) {
        stack.push(log(stack.pop(), stack.pop()))
    }

    override fun exitExprNumber(ctx: EquationParser.ExprNumberContext) {
        stack.push(ctx.NUMBER().text.toDouble())
    }

    override fun exitVariable(ctx: EquationParser.VariableContext) {
        stack.push(values[ctx.getName()] ?: throw IllegalArgumentException())
    }

    private fun popLeftRight(): Pair<Double, Double> {
        val right = stack.pop()
        val left = stack.pop()

        return left to right
    }
}
