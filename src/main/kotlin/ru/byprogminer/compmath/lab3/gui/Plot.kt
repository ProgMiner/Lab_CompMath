package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class Plot(private val store: ReactiveHolder<Store>): JPanel(null) {

    companion object {

        val BACKGROUND_COLOR: Color = Color.WHITE
        val GRID_COLOR: Color = Color.LIGHT_GRAY
        val INTERVAL_COLOR: Color = Color.GRAY
        val AXES_COLOR: Color = Color.BLACK
        val ABSCISSA_COLOR: Color = Color.RED
        val ORDINATE_COLOR: Color = Color.GREEN
        val APPLICATE_COLOR: Color = Color.BLUE

        const val ARROWS_LENGTH = 45
    }

    private var buffer: BufferedImage? = null

    init {
        background = BACKGROUND_COLOR
        foreground = AXES_COLOR

        addComponentListener(object: ComponentAdapter() {

            fun componentUpdated() {
                buffer = null
                repaint()
            }

            override fun componentMoved(e: ComponentEvent?) {
                componentUpdated()
            }

            override fun componentResized(e: ComponentEvent?) {
                componentUpdated()
            }

            override fun componentShown(e: ComponentEvent?) {
                componentUpdated()
            }
        })

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

        graphics.color = background
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
        val realGridStep = ceil(abs(min(intervalX, intervalY) / 20.0)).toInt()

        if (realGridStep != 0) {
            val gridStepX = realGridStep * zoomX * signX
            var currentGridLineX = centerX - (centerX / gridStepX).toInt() * gridStepX
            var currentRealGridLineX = (store.plotAbscissaBegin + currentGridLineX / zoomX).toInt()
            while (currentGridLineX < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(currentGridLineX.toInt(), 0, currentGridLineX.toInt(), height)

                graphics.color = foreground
                graphics.drawLine(currentGridLineX.toInt(), (centerY - 3).toInt(), currentGridLineX.toInt(), (centerY + 3).toInt())

                if (currentRealGridLineX != 0) {
                    graphics.drawString(currentRealGridLineX.toString(), currentGridLineX.toInt(), (centerY - 5).toInt())
                }

                currentGridLineX += gridStepX
                currentRealGridLineX += realGridStep * signX
            }

            val gridStepY = realGridStep * zoomY * signY
            var currentGridLineY = centerY - (centerY / gridStepY).toInt() * gridStepY
            var currentRealGridLineY = (store.plotOrdinateEnd + currentGridLineY / zoomY).toInt()
            while (currentGridLineY < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(0, currentGridLineY.toInt(), width, currentGridLineY.toInt())

                graphics.color = foreground
                graphics.drawLine((centerX - 3).toInt(), currentGridLineY.toInt(), (centerX + 3).toInt(), currentGridLineY.toInt())

                if (currentRealGridLineY != 0) {
                    graphics.drawString(currentRealGridLineY.toString(), (centerX + 3).toInt(), (currentGridLineY - 2).toInt())
                }

                currentGridLineY += gridStepY
                currentRealGridLineY += realGridStep * signY
            }
        }

        // Interval
        if (store.begin != null && store.end != null && store.cuts != null) {
            graphics.color = INTERVAL_COLOR

            val step = (store.end - store.begin) * zoomX / store.cuts
            var x = centerX + store.begin * zoomX
            for (i in 0..store.cuts) {
                graphics.drawLine(x.toInt(), 0, x.toInt(), height)
                x += step
            }

            graphics.color = Color(0x1F000000 or (INTERVAL_COLOR.rgb and 0xFFFFFF), true)
            graphics.fillRect((centerX + store.begin * zoomX).toInt(), 0, ((store.end - store.begin) * zoomX).toInt(), height)
        }

        // Axes
        graphics.color = foreground
        graphics.drawLine(0, centerY.toInt(), width, centerY.toInt())
        graphics.drawLine(centerX.toInt(), 0, centerX.toInt(), height)

        // Arrows
        if (graphics is Graphics2D) {
            graphics.stroke = BasicStroke(3f)
        }

        // Abscissa arrow
        val abscissaArrowY = min(max(centerY, .0), height.toDouble())

        val abscissaArrowX = if (signX >= 0) {
            min(max(centerX, .0), (width - ARROWS_LENGTH).toDouble())
        } else {
            min(max(centerX, ARROWS_LENGTH.toDouble()), width.toDouble())
        }

        graphics.color = ABSCISSA_COLOR
        graphics.drawLine(abscissaArrowX.toInt(), abscissaArrowY.toInt(), (abscissaArrowX + ARROWS_LENGTH * signX).toInt(), abscissaArrowY.toInt())
        graphics.drawLine((abscissaArrowX + ARROWS_LENGTH * 0.78 * signX).toInt(), (abscissaArrowY - ARROWS_LENGTH * 0.11).toInt(), (abscissaArrowX + ARROWS_LENGTH * signX).toInt(), abscissaArrowY.toInt())
        graphics.drawLine((abscissaArrowX + ARROWS_LENGTH * 0.78 * signX).toInt(), (abscissaArrowY + ARROWS_LENGTH * 0.11).toInt(), (abscissaArrowX + ARROWS_LENGTH * signX).toInt(), abscissaArrowY.toInt())

        // Ordinate arrow
        val ordinateArrowX = min(max(centerX, .0), width.toDouble())

        val ordinateArrowY = if (signY >= 0) {
            min(max(centerY,  ARROWS_LENGTH.toDouble()), height.toDouble())
        } else {
            min(max(centerY,  .0), (height - ARROWS_LENGTH).toDouble())
        }

        graphics.color = ORDINATE_COLOR
        graphics.drawLine(ordinateArrowX.toInt(), ordinateArrowY.toInt(), ordinateArrowX.toInt(), (ordinateArrowY + ARROWS_LENGTH * signY).toInt())
        graphics.drawLine((ordinateArrowX - ARROWS_LENGTH * 0.11).toInt(), (ordinateArrowY + ARROWS_LENGTH * 0.78 * signY).toInt(), ordinateArrowX.toInt(), (ordinateArrowY + ARROWS_LENGTH * signY).toInt())
        graphics.drawLine((ordinateArrowX + ARROWS_LENGTH * 0.11).toInt(), (ordinateArrowY + ARROWS_LENGTH * 0.78 * signY).toInt(), ordinateArrowX.toInt(), (ordinateArrowY + ARROWS_LENGTH * signY).toInt())

        // Applicate point
        graphics.color = APPLICATE_COLOR
        graphics.drawLine(ordinateArrowX.toInt(), abscissaArrowY.toInt(), ordinateArrowX.toInt(), abscissaArrowY.toInt())

        if (graphics is Graphics2D) {
            graphics.stroke = BasicStroke(1f)
        }

        // Plots
        if (store.plotAbscissaVariable != null) {
            val equations = when (store.mode) {
                Store.Mode.EQUATION -> listOf(store.equation to store.equationColor)
                Store.Mode.EQUATION_SYSTEM -> store.equations.toList()
            }.filter { eq -> try {
                eq.first.variables
                return@filter true
            } catch (e: UnsupportedOperationException) {
                return@filter false
            } }

            if (store.mode == Store.Mode.EQUATION && graphics is Graphics2D) {
                graphics.stroke = BasicStroke(2f)
            }

            val futures = mutableListOf<CompletableFuture<Void>>()
            for ((equation, color) in equations) {
                futures.add(CompletableFuture.runAsync {
                    var realX = store.plotAbscissaBegin

                    var prevY: Int? = null
                    var prevLeft: Int? = null
                    var prevRight: Int? = null
                    for (x in 0 until width) {
                        realX += 1 / zoomX

                        val (realLeft, realRight) = equation.evaluate(store.plotSlice + mapOf(store.plotAbscissaVariable to realX))

                        synchronized(graphics) {
                            val actualPrevY = prevY
                            val actualPrevLeft = prevLeft
                            val actualPrevRight = prevRight

                            graphics.color = color
                            when (store.plotMode) {
                                Store.PlotMode.EQUATIONS -> {
                                    if (realLeft.isFinite()) {
                                        val left = (centerY + realLeft * zoomY).toInt()

                                        if (actualPrevLeft != null) {
                                            graphics.drawLine(x - 1, actualPrevLeft, x, left)
                                        }

                                        prevLeft = left
                                    }

                                    if (realRight.isFinite()) {
                                        val right = (centerY + realRight * zoomY).toInt()

                                        if (actualPrevRight != null) {
                                            graphics.drawLine(x - 1, actualPrevRight, x, right)
                                        }

                                        prevRight = right
                                    }
                                }

                                Store.PlotMode.FUNCTIONS -> {
                                    if (realLeft.isFinite() && realRight.isFinite()) {
                                        val y = (centerY + (realLeft - realRight) * zoomY).toInt()

                                        if (actualPrevY != null) {
                                            graphics.drawLine(x - 1, actualPrevY, x, y)
                                        }

                                        prevY = y
                                    }
                                }
                            }
                        }
                    }
                })
            }

            CompletableFuture.allOf(*futures.toTypedArray()).join()

            if (store.mode == Store.Mode.EQUATION && graphics is Graphics2D) {
                graphics.stroke = BasicStroke(1f)
            }
        }

        buffer = newBuffer
    }

    private val sizeWithoutBorder: Dimension
        get() {
            val borderInsets = border.getBorderInsets(this)

            return Dimension(
                    width + 1 - borderInsets.left - borderInsets.right,
                    height + 1 - borderInsets.top - borderInsets.bottom
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
