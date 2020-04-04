package ru.byprogminer.compmath.lab3.equation

abstract class AbstractEquation(private val equation: String): Equation {

    override fun toString() = equation
}