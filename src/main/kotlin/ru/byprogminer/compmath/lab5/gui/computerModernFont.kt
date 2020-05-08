package ru.byprogminer.compmath.lab5.gui

import java.awt.Font
import java.awt.GraphicsEnvironment

private const val COMPUTER_MODERN_FONT_NAME = "cmunti.ttf"

val computerModernFont: Font = Font.createFont(Font.TRUETYPE_FONT, ClassPathHelper::class.java
        .getResourceAsStream(COMPUTER_MODERN_FONT_NAME)).deriveFont(Font.BOLD, 16f)
        .also { GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(it) }

private object ClassPathHelper
