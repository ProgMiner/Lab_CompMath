package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

class Plot(private val store: ReactiveHolder<Store>): JPanel(null) {

    companion object {

        val BACKGROUND_COLOR: Color = Color.WHITE
        val GRID_COLOR: Color = Color.LIGHT_GRAY
        val AXES_COLOR: Color = Color.BLACK
        val ABSCISSA_COLOR: Color = Color.RED
        val ORDINATE_COLOR: Color = Color.GREEN
        val APPLICATE_COLOR: Color = Color.BLUE
    }

    var buffer: BufferedImage? = null

    init {
        // Dummy Kotlin
        @Suppress("RedundantLambdaArrow")
        store.onChange.listeners.add { _ ->
            SwingUtilities.invokeLater {
                buffer = null
                repaint()
            }
        }
    }

    private fun renderBuffer() {
        val (width, height) = sizeWithoutBorder.run { width to height }

        val newBuffer = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = newBuffer.graphics
        val store = store.get()

        if (graphics is Graphics2D) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }

        graphics.color = BACKGROUND_COLOR
        graphics.fillRect(0, 0, width, height)

        val intervalX = store.plotAbscissaEnd - store.plotAbscissaBegin
        val intervalY = store.plotOrdinateEnd - store.plotOrdinateBegin
        val signX = if (intervalX < 0) -1 else 1
        val signY = if (intervalY < 0) 1 else -1

        val zoomX = width / intervalX
        val zoomY = -height / intervalY
        val centerX = -store.plotAbscissaBegin * zoomX
        val centerY = -store.plotOrdinateEnd * zoomY

        // Grid
        val realGridStep = ceil(abs(min(intervalX, intervalY) / 100.0)).toInt() * 5

        if (intervalX != .0) {
            val gridStepX = realGridStep * zoomX * signX

            var currentGridLineX = centerX - (centerX / gridStepX).toInt() * gridStepX
            while (currentGridLineX < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(currentGridLineX.toInt(), 0, currentGridLineX.toInt(), height)

                graphics.color = AXES_COLOR
                graphics.drawLine(currentGridLineX.toInt(), (centerY - 3).toInt(), currentGridLineX.toInt(), (centerY + 3).toInt())

                currentGridLineX += gridStepX
            }

            // TODO text
        }

        if (intervalY != .0) {
            val gridStepY = realGridStep * zoomY * signY

            var currentGridLineY = centerY - (centerY / gridStepY).toInt() * gridStepY
            while (currentGridLineY < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(0, currentGridLineY.toInt(), width, currentGridLineY.toInt())

                graphics.color = AXES_COLOR
                graphics.drawLine((centerX - 3).toInt(), currentGridLineY.toInt(), (centerX + 3).toInt(), currentGridLineY.toInt())

                currentGridLineY += gridStepY
            }

            // TODO text
        }

        // Axes
        graphics.color = AXES_COLOR
        graphics.drawLine(0, centerY.toInt(), width, centerY.toInt())
        graphics.drawLine(centerX.toInt(), 0, centerX.toInt(), height)

        // Arrows
        if (graphics is Graphics2D) {
            graphics.stroke = BasicStroke(3f)
        }

        // Abscissa arrow
        graphics.color = ABSCISSA_COLOR
        graphics.drawLine(centerX.toInt(), centerY.toInt(), (centerX + 45 * signX).toInt(), centerY.toInt())
        graphics.drawLine((centerX + 35 * signX).toInt(), (centerY - 5).toInt(), (centerX + 45 * signX).toInt(), centerY.toInt())
        graphics.drawLine((centerX + 35 * signX).toInt(), (centerY + 5).toInt(), (centerX + 45 * signX).toInt(), centerY.toInt())

        // Ordinate arrow
        graphics.color = ORDINATE_COLOR
        graphics.drawLine(centerX.toInt(), centerY.toInt(), centerX.toInt(), (centerY + 45 * signY).toInt())
        graphics.drawLine((centerX - 5).toInt(), (centerY + 35 * signY).toInt(), centerX.toInt(), (centerY + 45 * signY).toInt())
        graphics.drawLine((centerX + 5).toInt(), (centerY + 35 * signY).toInt(), centerX.toInt(), (centerY + 45 * signY).toInt())

        // Applicate point
        graphics.color = APPLICATE_COLOR
        graphics.drawLine(centerX.toInt(), centerY.toInt(), centerX.toInt(), centerY.toInt())

        if (graphics is Graphics2D) {
            graphics.stroke = BasicStroke(1f)
        }

        // TODO plots

        // TODO roots

        buffer = newBuffer
    }

    private val sizeWithoutBorder: Dimension
        get() {
            val borderInsets = border.getBorderInsets(this)

            return Dimension(
                    width + 2 - borderInsets.left - borderInsets.right,
                    height + 2 - borderInsets.top - borderInsets.bottom
            )
        }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (buffer == null) {
            renderBuffer()
        }

        val borderInsets = border.getBorderInsets(this)
        g.drawImage(buffer, borderInsets.left - 1, borderInsets.top - 1, null)
    }
}
