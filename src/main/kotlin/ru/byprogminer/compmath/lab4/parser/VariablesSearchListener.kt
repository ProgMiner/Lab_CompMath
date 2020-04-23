package ru.byprogminer.compmath.lab4.parser

class VariablesSearchListener: ExpressionBaseListener() {

    val variables: Set<String>
        get() = _variables.toSet()

    private val _variables = mutableSetOf<String>()

    override fun exitVariable(ctx: ExpressionParser.VariableContext) {
        _variables.add(ctx.getName())
    }
}
