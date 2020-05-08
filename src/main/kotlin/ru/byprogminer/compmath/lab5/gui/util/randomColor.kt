package ru.byprogminer.compmath.lab5.gui.util

import java.awt.Color
import kotlin.random.Random

private val defaultRandom = Random(System.currentTimeMillis()).also {
    it.nextInt()
    it.nextInt()
    it.nextInt()
}

fun randomColor(random: Random = defaultRandom) = Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
