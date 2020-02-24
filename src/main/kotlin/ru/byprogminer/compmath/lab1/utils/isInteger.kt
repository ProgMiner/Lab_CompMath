package ru.byprogminer.compmath.lab1.utils

import kotlin.math.floor

fun Double.isInteger() = (this == floor(this)) && this.isFinite()
