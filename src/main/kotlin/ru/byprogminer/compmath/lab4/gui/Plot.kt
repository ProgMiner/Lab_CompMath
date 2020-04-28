package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import ru.byprogminer.compmath.lab4.util.toPlainString
import java.awt.*
import java.awt.event.*
import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.*

/**
 * Interactive component for rendering plots
 *
 * @author Eridan Domoratskiy
 */
class Plot(private val store: ReactiveHolder<Store>): JPanel(null), ComponentListener,
        MouseListener, MouseMotionListener, MouseWheelListener {

    companion object {

        val BACKGROUND_COLOR: Color = Color.WHITE
        val GRID_COLOR: Color = Color.LIGHT_GRAY
        val POINTS_COLOR: Color = Color.DARK_GRAY
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

        graphics.font = font
        graphics.color = background
        graphics.fillRect(0, 0, width, height)

        val intervalX = ((store.plotAbscissaEnd ?: .0) - (store.plotAbscissaBegin ?: .0))
        val intervalY = ((store.plotOrdinateEnd ?: .0) - (store.plotOrdinateBegin ?: .0))
        val signX = if (intervalX < 0) -1 else 1
        val signY = if (intervalY < 0) 1 else -1

        val zoomX = width / intervalX
        val zoomY = -height / intervalY
        val centerX = -(store.plotAbscissaBegin ?: .0) * zoomX
        val centerY = -(store.plotOrdinateEnd ?: .0) * zoomY

        // Grid
        val realGridStep = max(roundGridStep(intervalX / 10.0), roundGridStep(intervalY / 10.0))
        if (realGridStep != .0) {

            val gridStepX = realGridStep * zoomX * signX
            var currentGridLineX = centerX - round(centerX / gridStepX) * gridStepX
            while (currentGridLineX < width) {

                graphics.color = GRID_COLOR
                graphics.drawLine(currentGridLineX.toInt(), 0, currentGridLineX.toInt(), height)

                currentGridLineX += gridStepX
            }

            val gridStepY = realGridStep * zoomY * signY
            var currentGridLineY = centerY - round(centerY / gridStepY) * gridStepY
            while (currentGridLineY < height) {

                graphics.color = GRID_COLOR
                graphics.drawLine(0, currentGridLineY.toInt(), width, currentGridLineY.toInt())

                currentGridLineY += gridStepY
            }
        }

        // Lines to points
        graphics.color = POINTS_COLOR

        // Lines to interpolation points
        if (store.function.isValid && store.function.variables.size == 1) {
            val variable = store.function.variables.first()

            for (realX in store.interpolationPoints) {
                val x = (centerX + realX * zoomX).toInt()
                val fv = mapOf(variable to realX)

                val functionsY = listOf(store.function, store.interpolation)
                        .filter { f -> f.isValid }.map { f -> f.evaluate(fv) }
                        .map { realY -> (centerY + realY * zoomY).toInt() }

                val y = (listOf(centerY.toInt()) + functionsY).toSortedSet()
                graphics.drawLine(x, max(y.first(), 0), x, min(y.last(), height))
            }
        }


        // Lines to values points
        for (realX in store.valuePoints) {
            val x = (centerX + realX * zoomX).toInt()
            val realFunctionY = store.functionValues?.get(realX) ?: .0
            val realInterpolationY = store.interpolationValues?.get(realX) ?: .0

            val y = sortedSetOf(
                    centerY.toInt(),
                    (centerY + realFunctionY * zoomY).toInt(),
                    (centerY + realInterpolationY * zoomY).toInt()
            )

            graphics.drawLine(x, max(y.first(), 0), x, min(y.last(), height))
        }

        // Grid text
        if (realGridStep != .0) {

            val gridStepX = realGridStep * zoomX * signX
            var currentRealGridLineX = -round(centerX / gridStepX + 1) * realGridStep * signX
            var currentGridLineX = centerX + currentRealGridLineX / realGridStep * gridStepX * signX
            while (currentGridLineX < width) {
                if (abs(currentGridLineX - centerX) > 1) {
                    graphics.color = foreground

                    val y = min(max(centerY, .0), height.toDouble())
                    graphics.drawLine(currentGridLineX.toInt(), (y - 3).toInt(), currentGridLineX.toInt(), (y + 3).toInt())

                    graphics.drawString(
                            currentRealGridLineX.toPlainString(),
                            (currentGridLineX + 2).toInt(),
                            min(max((centerY - 5).toInt(), graphics.fontMetrics.height + 2), height - 5)
                    )
                }

                currentGridLineX += gridStepX
                currentRealGridLineX += realGridStep * signX
            }

            val gridStepY = realGridStep * zoomY * signY
            var currentRealGridLineY = -round(centerY / gridStepY) * realGridStep * signY
            var currentGridLineY = centerY + currentRealGridLineY / realGridStep * gridStepY * signY
            while (currentGridLineY < height + gridStepY) {
                if (abs(currentGridLineY - centerY) > 1) {
                    graphics.color = foreground

                    val x = min(max(centerX, .0), width.toDouble())
                    graphics.drawLine((x - 3).toInt(), currentGridLineY.toInt(), (x + 3).toInt(), currentGridLineY.toInt())

                    val str = currentRealGridLineY.toPlainString()
                    graphics.drawString(str,
                            min(max((centerX + 3).toInt(), 5), width - graphics.fontMetrics.stringWidth(str) - 2),
                            (currentGridLineY - 2).toInt())
                }

                currentGridLineY += gridStepY
                currentRealGridLineY += realGridStep * signY
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
            val functions = listOf(
                    store.function to store.functionColor,
                    store.interpolation to store.interpolationColor
            ).filter { (f, _) -> f.isValid && f.variables.size == 1 }

            val realXStep = 1 / zoomX
            if (graphics is Graphics2D) {
                val futures = mutableListOf<CompletableFuture<Pair<Path2D, Color>>>()

                for ((function, color) in functions) {
                    futures.add(CompletableFuture.supplyAsync {
                        val points = (0 until width).asSequence().map { x ->
                            x to function.evaluate(mapOf(
                                    store.plotAbscissaVariable to (store.plotAbscissaBegin ?: .0) + x * realXStep
                            ))
                        }.map { (x, realY) ->
                            x.toDouble() to if (realY.isFinite()) {
                                centerY + realY * zoomY
                            } else {
                                null
                            }
                        }.map { (x, y) ->
                            x to when (y) {
                                null -> null
                                else -> min(max(y, .0 - height), .0 + 2 * height)
                            }
                        }.map(::listOf).reduce { acc, current ->
                            val (currentX, currentY) = current[0]

                            if (acc.size == 1) {
                                return@reduce acc + current
                            }

                            val (prevX, prevY) = acc.last()
                            val subAcc = acc.subList(0, acc.lastIndex)
                            if (currentY == null && prevY == null) {
                                return@reduce subAcc + current
                            }

                            val (prevPrevX, prevPrevY) = acc[acc.size - 2]
                            if (currentY == null || prevY == null || prevPrevY == null) {
                                return@reduce acc + current
                            }

                            val isOnLine = abs((currentX - prevPrevX) * (prevY - prevPrevY) /
                                    (prevX - prevPrevX) + prevPrevY - currentY) < 0.01

                            return@reduce if (isOnLine) {
                                subAcc + current
                            } else {
                                acc + current
                            }
                        }.toList()

                        val path = Path2D.Double()
                        for (i in points.indices) {
                            val prev = points.getOrNull(i - 1)
                            val cur = points[i]

                            val (curX, curY) = cur
                            if (prev?.second == null || curY == null) {
                                if (curY != null) {
                                    path.moveTo(curX, curY)
                                    path.lineTo(curX, curY)
                                }

                                continue
                            }

                            val next = points.getOrNull(i + 1)
                            val prevPrev = points.getOrNull(i - 2)
                            val (prevX, prevY) = prev.first to prev.second!!
                            if (prevPrev?.second == null || next?.second == null) {
                                path.lineTo(curX, curY)
                                continue
                            }

                            val (prevPrevX, prevPrevY) = prevPrev.first to prevPrev.second!!
                            val (nextX, nextY) = next.first to next.second!!

                            val a = (nextY - curY) / (nextX - curX)
                            val b = (prevY - prevPrevY) / (prevX - prevPrevX)

                            val y = (curY + a * (prevPrevX - curX - prevPrevY / b)) / (1 - a / b)
                            val x = (y - prevPrevY) / b + prevPrevX

                            if (x < prevX || x > curX || y < prevY || y > curY) {
                                path.lineTo(curX, curY)
                                continue
                            }

                            path.quadTo(x, y, curX, curY)
                        }

                        return@supplyAsync path to color
                    })
                }

                CompletableFuture.allOf(*futures.toTypedArray()).join()
                graphics.stroke = BasicStroke(2f)

                for (future in futures) {
                    val (path, color) = future.get()

                    graphics.color = color
                    graphics.draw(path)
                }

                graphics.stroke = BasicStroke(1f)
            } else {
                val futures = mutableListOf<CompletableFuture<Void>>()

                if (graphics is Graphics2D) {
                    graphics.stroke = BasicStroke(2f)
                }

                for ((function, color) in functions) {
                    futures.add(CompletableFuture.runAsync {
                        var realX = store.plotAbscissaBegin ?: .0
                        var prevY: Int? = null

                        for (x in 0 until width) {
                            realX += realXStep

                            val realResult = function.evaluate(mapOf(store.plotAbscissaVariable to realX))

                            synchronized(graphics) {
                                val actualPrevY = prevY

                                graphics.color = color
                                if (realResult.isFinite()) {
                                    val y = (centerY + realResult * zoomY).toInt()

                                    if (actualPrevY != null) {
                                        graphics.drawLine(x - 1, actualPrevY, x, y)
                                    }

                                    prevY = y
                                }
                            }
                        }
                    })
                }

                CompletableFuture.allOf(*futures.toTypedArray()).join()

                if (graphics is Graphics2D) {
                    graphics.stroke = BasicStroke(1f)
                }
            }
        }

        // Interpolation points
        if (store.function.isValid && store.function.variables.size == 1) {
            val variable = store.function.variables.first()

            for (realX in store.interpolationPoints) {
                val x = (centerX + realX * zoomX).toInt()
                val y = (centerY + store.function.evaluate(mapOf(variable to realX)) * zoomY).toInt()

                graphics.color = POINTS_COLOR
                graphics.fillArc(x - 2, y - 2, 5, 5, 0, 360)
            }
        }

        // Value points
        for (realX in store.valuePoints) {
            val x = (centerX + realX * zoomX).toInt()

            listOf(
                    store.functionValues?.get(realX) to store.functionColor,
                    store.interpolationValues?.get(realX) to store.interpolationColor
            ).filter { (realY, _) -> realY != null }.map { (realY, color) ->
                (centerY + realY!! * zoomY).toInt() to color
            }.forEach { (y, color) ->
                graphics.color = POINTS_COLOR
                graphics.fillArc(x - 3, y - 3, 7, 7, 0, 360)

                graphics.color = color
                graphics.fillArc(x - 2, y - 2, 5, 5, 0, 360)
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

    override fun mouseClicked(e: MouseEvent) {}

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
                if (
                        store.plotAbscissaBegin == null || store.plotAbscissaEnd == null ||
                        store.plotOrdinateBegin == null || store.plotOrdinateEnd == null
                ) {
                    return@mutateIfOther store
                }

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
        val amount: Double = e.wheelRotation.toDouble() * 1.68 *
                if (e.isControlDown) 10 else 1

        store.mutateIfOther { store ->
            if (
                    store.plotAbscissaBegin == null || store.plotAbscissaEnd == null ||
                    store.plotOrdinateBegin == null || store.plotOrdinateEnd == null
            ) {
                return@mutateIfOther store
            }

            val (width, height) = sizeWithoutBorder.run { width to height }
            val intervalX = store.plotAbscissaEnd - store.plotAbscissaBegin
            val intervalY = store.plotOrdinateEnd - store.plotOrdinateBegin

            val amountX = amount * intervalX / width / 2
            val amountY = amount * intervalY / height / 2

            // TODO mouse position priority

            return@mutateIfOther if (amountX > amountY) {
                val amountXByY = amountY * intervalX / intervalY

                store.copy(
                        plotAbscissaBegin = store.plotAbscissaBegin - amountXByY,
                        plotAbscissaEnd = store.plotAbscissaEnd + amountXByY,
                        plotOrdinateBegin = store.plotOrdinateBegin - amountY,
                        plotOrdinateEnd = store.plotOrdinateEnd + amountY
                )
            } else {
                val amountYByX = amountX * intervalY / intervalX

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
