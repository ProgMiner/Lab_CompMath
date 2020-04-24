package ru.byprogminer.compmath.lab4.parser

import ru.byprogminer.compmath.lab1.utils.Fraction
import java.util.*
import kotlin.math.*

class InterpreterListener(private val values: Map<String, Fraction>): ExpressionBaseListener() {

    private val stack = Stack<Fraction>()

    val result: Fraction by lazy { stack.pop() }

    override fun exitExprAbs(ctx: ExpressionParser.ExprAbsContext?) {
        stack.push(stack.pop().abs())
    }

    override fun exitExprUnaryMinus(ctx: ExpressionParser.ExprUnaryMinusContext) {
        stack.push(stack.pop().unaryMinus())
    }

    override fun exitExprPower(ctx: ExpressionParser.ExprPowerContext) {
        val (left, right) = popLeftRight()

        stack.push(Fraction(left.toDouble().pow(right.toDouble())))
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

        val power = ctx.pow?.let { stack.pop().toDouble() }
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

        stack.push(Fraction(power?.let { pow -> value.pow(pow) } ?: value))
    }

    override fun exitExprNumber(ctx: ExpressionParser.ExprNumberContext) {
        stack.push(Fraction(ctx.NUMBER().text.toDouble()))
    }

    override fun exitVariable(ctx: ExpressionParser.VariableContext) {
        stack.push(values[ctx.getName()] ?: throw IllegalArgumentException())
    }

    private fun popLeftRight(): Pair<Fraction, Fraction> {
        val right = stack.pop()
        val left = stack.pop()

        return left to right
    }
}
