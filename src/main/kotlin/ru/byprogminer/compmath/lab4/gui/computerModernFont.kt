package ru.byprogminer.compmath.lab4.gui

import java.awt.Font

private const val COMPUTER_MODERN_FONT_NAME = "cmunti.ttf"

val computerModernFont: Font = Font.createFont(Font.TRUETYPE_FONT, ClassPathHelper::class.java
        .getResourceAsStream(COMPUTER_MODERN_FONT_NAME)).deriveFont(Font.BOLD, 16f)

private object ClassPathHelper
