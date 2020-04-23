package ru.byprogminer.compmath.lab4.gui.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.ImageIcon

object ColorIconFactory {

    private const val WIDTH = 14
    private const val HEIGHT = 14

    private val cache = WeakHashMap<Color, ImageIcon>()

    fun getIcon(color: Color): ImageIcon = cache.computeIfAbsent(color, this::makeIcon)

    private fun makeIcon(color: Color): ImageIcon {
        val image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.graphics

        graphics.color = color
        graphics.fillRect(0, 0, WIDTH, HEIGHT)

        return ImageIcon(image)
    }
}
