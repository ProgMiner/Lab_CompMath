package ru.byprogminer.compmath.lab5.gui.util

import javax.swing.ImageIcon

fun <T: Any> T.createImageIconOrNull(path: String, description: String? = null) =
    this::class.java.getResource(path)?.let { ImageIcon(it, description) }

fun <T: Any> T.createImageIcon(path: String, description: String? = null): ImageIcon {
    return createImageIconOrNull(path, description) ?: throw IllegalArgumentException("$path not found")
}
