package ru.byprogminer.compmath.lab3.gui.util

import java.awt.Image
import javax.swing.ImageIcon

fun ImageIcon.scale(width: Int, height: Int, scale: Int = Image.SCALE_SMOOTH) =
        ImageIcon(image.getScaledInstance(width, height, scale))
