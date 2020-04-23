package ru.byprogminer.compmath.lab4.parser

fun ExpressionParser.VariableContext.getName() =
    VARIABLE().text + NUMBER().joinToString("") { "_${it.text}" }
