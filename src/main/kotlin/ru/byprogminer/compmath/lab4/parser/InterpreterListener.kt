package ru.byprogminer.compmath.lab4.parser

import java.util.*
import kotlin.math.*

class InterpreterListener(private val values: Map<String, Double>): ExpressionBaseListener() {

    private val stack = Stack<Double>()

    val result: Double by lazy { stack.pop() }

    override fun exitExprAbs(ctx: ExpressionParser.ExprAbsContext?) {
        stack.push(abs(stack.pop()))
    }

    override fun exitExprUnaryMinus(ctx: ExpressionParser.ExprUnaryMinusContext) {
        stack.push(stack.pop().unaryMinus())
    }

    override fun exitExprPower(ctx: ExpressionParser.ExprPowerContext) {
        val (left, right) = popLeftRight()

        stack.push(left.pow(right))
    }

    override fun exitExprMultiplyDivide(ctx: ExpressionParser.ExprMultiplyDivideContext) {
        val (left, right) = popLeftRight()

        stack.push(when (ctx.op.text) {
            "*" -> left * right
            "/" -> left / right

            else -> throw IllegalArgumentException()
        })
    }

    override fun exitExprPlusMinus(ctx: ExpressionParser.ExprPlusMinusContext) {
        val (left, right) = popLeftRight()

        stack.push(when (ctx.op.text) {
            "+" -> left + right
            "-" -> left - right

            else -> throw IllegalArgumentException()
        })
    }

    override fun exitExprFunction(ctx: ExpressionParser.ExprFunctionContext) {
        val operand = stack.pop().toDouble()

        val base = ctx.function().base?.let { stack.pop().toDouble() }

        val value = when (ctx.function().name.text) {
            "sqrt" -> sqrt(operand)
            "sin" -> sin(operand)
            "cos" -> cos(operand)
            "tan" -> tan(operand)
            "asin" -> asin(operand)
            "acos" -> acos(operand)
            "atan" -> atan(operand)
            "log" -> if (base != null) {
                log(operand, base)
            } else {
                ln(operand)
            }

            "ln" -> ln(operand)
            "lg" -> log10(operand)

            else -> throw IllegalArgumentException()
        }

        stack.push(value)
    }

    override fun exitExprNumber(ctx: ExpressionParser.ExprNumberContext) {
        stack.push(ctx.NUMBER().text.toDouble())
    }

    override fun exitVariable(ctx: ExpressionParser.VariableContext) {
        stack.push(values[ctx.getName()] ?: throw IllegalArgumentException())
    }

    private fun popLeftRight(): Pair<Double, Double> {
        val right = stack.pop()
        val left = stack.pop()

        return left to right
    }
}
