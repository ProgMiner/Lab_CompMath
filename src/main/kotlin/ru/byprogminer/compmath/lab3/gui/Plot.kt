package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import ru.byprogminer.compmath.lab3.util.toPlainString
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.*

class Plot(private val store: ReactiveHolder<Store>): JPanel(null), ComponentListener,
        MouseListener, MouseMotionListener, MouseWheelListener {

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

    private var mouseX = -1
    private var mouseY = -1

    private val sizeWithoutBorder: Dimension
        get() {
            val borderInsets = border.getBorderInsets(this)

            return Dimension(
                    width + 1 - borderInsets.left - borderInsets.right,
                    height + 1 - borderInsets.top - borderInsets.bottom
            )
        }

    init {
        background = BACKGROUND_COLOR
        foreground = AXES_COLOR

        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
        addComponentListener(this)

        // Dummy Kotlin
        @Suppress("RedundantLambdaArrow")
        store.onChange.listeners.add { _ ->
            SwingUtilities.invokeLater(this::invalidateBuffer)
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

        val realGridStepX = roundGridStep(intervalX / 10.0)
        if (realGridStepX != .0) {
            val gridStepX = realGridStepX * zoomX * signX

            var currentGridLineX = centerX - (centerX / gridStepX).toInt() * gridStepX
            while (currentGridLineX < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(currentGridLineX.toInt(), 0, currentGridLineX.toInt(), height)

                graphics.color = foreground
                graphics.drawLine(currentGridLineX.toInt(), (centerY - 3).toInt(), currentGridLineX.toInt(), (centerY + 3).toInt())

                currentGridLineX += gridStepX
            }
        }

        val realGridStepY = roundGridStep(intervalY / 10.0)
        if (realGridStepY != .0) {
            val gridStepY = realGridStepY * zoomY * signY

            var currentGridLineY = centerY - (centerY / gridStepY).toInt() * gridStepY
            while (currentGridLineY < height) {

                graphics.color = GRID_COLOR
                graphics.drawLine(0, currentGridLineY.toInt(), width, currentGridLineY.toInt())

                graphics.color = foreground
                graphics.drawLine((centerX - 3).toInt(), currentGridLineY.toInt(), (centerX + 3).toInt(), currentGridLineY.toInt())

                currentGridLineY += gridStepY
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
            graphics.fillRect((centerX + store.begin * zoomX * signX).toInt(), 0, ((store.end - store.begin) * zoomX * signX).toInt(), height)
        }

        // Grid text
        if (realGridStepX != .0) {
            val gridStepX = realGridStepX * zoomX * signX

            var currentRealGridLineX = -(centerX / gridStepX + 1).toInt() * realGridStepX * signX
            var currentGridLineX = centerX + currentRealGridLineX / realGridStepX * gridStepX * signX
            while (currentGridLineX < width) {
                if (abs(currentGridLineX - centerX) > 1) {
                    graphics.color = foreground

                    graphics.drawString(
                            currentRealGridLineX.toPlainString(),
                            (currentGridLineX + 2).toInt(),
                            min(max((centerY - 5).toInt(), graphics.fontMetrics.height + 2), height - 5)
                    )
                }

                currentGridLineX += gridStepX
                currentRealGridLineX += realGridStepX * signX
            }
        }


        if (realGridStepY != .0) {
            val gridStepY = realGridStepY * zoomY * signY

            var currentRealGridLineY = -(centerY / gridStepY).toInt() * realGridStepY * signY
            var currentGridLineY = centerY + currentRealGridLineY / realGridStepY * gridStepY * signY
            while (currentGridLineY < height + gridStepY) {
                if (abs(currentGridLineY - centerY) > 1) {
                    graphics.color = foreground

                    val str = currentRealGridLineY.toPlainString()
                    graphics.drawString(str,
                            min(max((centerX + 3).toInt(), 5), width - graphics.fontMetrics.stringWidth(str) - 2),
                            (currentGridLineY - 2).toInt())
                }

                currentGridLineY += gridStepY
                currentRealGridLineY += realGridStepY * signY
            }
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
            min(max(centerY, .0), (height - ARROWS_LENGTH).toDouble())
        } else {
            min(max(centerY, ARROWS_LENGTH.toDouble()), height.toDouble())
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

    private fun invalidateBuffer() {
        buffer = null
        repaint()
    }

    private fun roundGridStep(baseGridStep: Double): Double {
        if (baseGridStep == .0) {
            return .0
        }

        if (baseGridStep < 0) {
            return roundGridStep(-baseGridStep)
        }

        val baseTenPower = log10(baseGridStep)
        val basePoweredTen = 10.0.pow(floor(baseTenPower))

        return listOf(basePoweredTen, basePoweredTen * 2, basePoweredTen * 5)
                .map { step -> step to abs(step - baseGridStep) / baseGridStep }
                .minBy { (_, dev) -> dev }!!.first
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (buffer == null) {
            renderBuffer()
        }

        val borderInsets = border.getBorderInsets(this)
        g.drawImage(buffer, borderInsets.left - 1, borderInsets.top - 1, null)
    }

    override fun mouseClicked(e: MouseEvent) {
        // TODO add roots selecting
    }

    override fun mouseMoved(e: MouseEvent) {
        mouseX = e.x
        mouseY = e.y
    }

    override fun mouseDragged(e: MouseEvent) {
        val mousePressed = e.button or MouseEvent.BUTTON1 != MouseEvent.NOBUTTON

        if (mousePressed) {
            val deltaX = e.x - mouseX
            val deltaY = e.y - mouseY

            store.mutateIfOther { store ->
                val (width, height) = sizeWithoutBorder.run { width to height }

                val intervalX = store.plotAbscissaEnd - store.plotAbscissaBegin
                val intervalY = store.plotOrdinateEnd - store.plotOrdinateBegin

                val zoomX = intervalX / width
                val zoomY = -intervalY / height
                return@mutateIfOther store.copy(
                        plotAbscissaBegin = store.plotAbscissaBegin - deltaX * zoomX,
                        plotAbscissaEnd = store.plotAbscissaEnd - deltaX * zoomX,
                        plotOrdinateBegin = store.plotOrdinateBegin - deltaY * zoomY,
                        plotOrdinateEnd = store.plotOrdinateEnd - deltaY * zoomY
                )
            }
        }

        mouseX = e.x
        mouseY = e.y
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        val amount: Double = e.wheelRotation.toDouble() * 1.68

        store.mutateIfOther { store ->
            val (width, height) = sizeWithoutBorder.run { width to height }

            val intervalX = store.plotAbscissaEnd - store.plotAbscissaBegin
            val intervalY = store.plotOrdinateEnd - store.plotOrdinateBegin

            val zoomX = intervalX / width
            val zoomY = intervalY / height
            val amountX = amount * zoomX / 2
            val amountY = amount * zoomY / 2

            // TODO mouse position priority

            return@mutateIfOther if (amountX > amountY) {
                val amountXByY = amountY * zoomX / zoomY

                store.copy(
                        plotAbscissaBegin = store.plotAbscissaBegin - amountXByY,
                        plotAbscissaEnd = store.plotAbscissaEnd + amountXByY,
                        plotOrdinateBegin = store.plotOrdinateBegin - amountY,
                        plotOrdinateEnd = store.plotOrdinateEnd + amountY
                )
            } else {
                val amountYByX = amountX * zoomY / zoomX

                store.copy(
                        plotAbscissaBegin = store.plotAbscissaBegin - amountX,
                        plotAbscissaEnd = store.plotAbscissaEnd + amountX,
                        plotOrdinateBegin = store.plotOrdinateBegin - amountYByX,
                        plotOrdinateEnd = store.plotOrdinateEnd + amountYByX
                )
            }
        }
    }

    override fun componentMoved(e: ComponentEvent?) {
        invalidateBuffer()
    }

    override fun componentResized(e: ComponentEvent?) {
        invalidateBuffer()
    }

    override fun componentShown(e: ComponentEvent?) {
        invalidateBuffer()
    }

    override fun componentHidden(e: ComponentEvent?) {}

    override fun mouseEntered(e: MouseEvent) {}

    override fun mouseExited(e: MouseEvent) {}

    override fun mousePressed(e: MouseEvent) {}

    override fun mouseReleased(e: MouseEvent) {}
}
