package ru.byprogminer.compmath.lab3.parser

fun EquationParser.VariableContext.getName() =
    VARIABLE().text + NUMBER().joinToString("") { "_${it.text}" }
