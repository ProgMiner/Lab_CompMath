package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.SwingUtilities

class Plot(private val store: ReactiveHolder<Store>): JPanel(null) {

    companion object {

        val BACKGROUND_COLOR: Color = Color.WHITE
        val GRID_COLOR: Color = Color.LIGHT_GRAY
        val AXES_COLOR: Color = Color.BLACK
    }

    var buffer: BufferedImage? = null

    init {
        // Dummy Kotlin
        @Suppress("RedundantLambdaArrow")
        store.onChange.listeners.add { _ ->
            buffer = null

            SwingUtilities.invokeLater {
                repaint()
            }
        }
    }

    private fun renderBuffer() {
        val newBuffer = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = newBuffer.graphics
        val store = store.get()

        graphics.color = BACKGROUND_COLOR
        graphics.fillRect(0, 0, width, height)

        graphics.color = GRID_COLOR
        //

        buffer = newBuffer
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (buffer == null) {
            renderBuffer()
        }

        g.drawImage(buffer, 0, 0, null)
    }
}
