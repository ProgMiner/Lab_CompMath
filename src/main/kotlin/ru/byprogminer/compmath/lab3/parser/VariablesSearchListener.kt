package ru.byprogminer.compmath.lab3.parser

class VariablesSearchListener: EquationBaseListener() {

    val variables: Set<String>
        get() = _variables.toSet()

    private val _variables = mutableSetOf<String>()

    override fun exitVariable(ctx: EquationParser.VariableContext) {
        _variables.add(ctx.getName())
    }
}
