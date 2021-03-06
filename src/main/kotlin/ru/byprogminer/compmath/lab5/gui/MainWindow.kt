package ru.byprogminer.compmath.lab5.gui

import ru.byprogminer.compmath.lab4.parser.parse
import ru.byprogminer.compmath.lab5.APP_NAME
import ru.byprogminer.compmath.lab5.APP_VERSION
import ru.byprogminer.compmath.lab5.Store
import ru.byprogminer.compmath.lab5.gui.util.ColorIconFactory
import ru.byprogminer.compmath.lab5.gui.util.createImageIcon
import ru.byprogminer.compmath.lab5.gui.util.documentAdapter
import ru.byprogminer.compmath.lab5.gui.util.scale
import ru.byprogminer.compmath.lab5.util.ReactiveHolder
import ru.byprogminer.compmath.lab5.util.toPlainString
import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.text.JTextComponent
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.max

class MainWindow(store: ReactiveHolder<Store>): JFrame("$APP_NAME v$APP_VERSION") {

    companion object {

        private val SWAP_ICON = createImageIcon("swap.png").scale(16, 16)

        private val INVALID_VALUE_COLOR = Color.RED
    }

    private val _contentPane = JPanel(GridBagLayout())
    private val controlPanel = JPanel(GridBagLayout())
    private val expressionPanel = JPanel(GridBagLayout())
    private val expressionLabel = JLabel("f(x, y) =")
    private val expressionField = JTextField(15)
    private val startPointPanel = JPanel(GridBagLayout())
    private val startPointXLabel = JLabel("<html>x<sub><small>0</small></sub> =</html>")
    private val startPointXField = JTextField(5)
    private val startPointYLabel = JLabel("<html>y<sub><small>0</small></sub> =</html>")
    private val startPointYField = JTextField(5)
    private val otherPanel = JPanel(GridBagLayout())
    private val otherEndXPanel = JPanel(GridBagLayout())
    private val otherEndXLabel = JLabel("<html>x<sub><small>n</small></sub> =</html>")
    private val otherEndXField = JTextField(5)
    private val otherPrecisionPanel = JPanel(GridBagLayout())
    private val otherPrecisionLabel = JLabel("Precision:")
    private val otherPrecisionField = JTextField(5)
    private val resultPanel = JPanel(GridBagLayout())
    private val resultLabel = JLabel("Result:")
    private val resultRungeKuttaColorButton = JButton()
    private val resultAdamsColorButton = JButton()
    private val resultArea = JTextArea(3, 15)
    private val resultAreaPane = JScrollPane(resultArea)
    private val plotPanel = JPanel(GridBagLayout())
    private val plotPlot = Plot(store)
    private val plotAbscissaLabel = JLabel("${Store.ABSCISSA_VARIABLE}:")
    private val plotAbscissaFromLabel = JLabel("from")
    private val plotAbscissaBeginField = JTextField(8)
    private val plotAbscissaToLabel = JLabel("to")
    private val plotAbscissaEndField = JTextField(8)
    private val plotAbscissaSwapButton = JButton(SWAP_ICON)
    private val plotOrdinateLabel = JLabel("${Store.ORDINATE_VARIABLE}:")
    private val plotOrdinateFromLabel = JLabel("from")
    private val plotOrdinateBeginField = JTextField(8)
    private val plotOrdinateToLabel = JLabel("to")
    private val plotOrdinateEndField = JTextField(8)
    private val plotOrdinateSwapButton = JButton(SWAP_ICON)
    private val plotButtonsPanel = JPanel(GridBagLayout())
    private val plotButtonsFitButton = JButton("Fit 1x1")

    private val defaultTextFieldBackgroundColor = expressionField.background

    private var onStoreChangeRun = false

    init {
        expressionLabel.font = computerModernFont
        expressionPanel.add(expressionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        expressionField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(expression = parse(expressionField.text))
            }
        } })
        expressionField.font = computerModernFont
        expressionPanel.add(expressionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))

        expressionPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null,
                        "y`+ f(x, y) = 0",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        computerModernFont),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        controlPanel.add(expressionPanel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        startPointXLabel.font = computerModernFont
        startPointXLabel.horizontalAlignment = JLabel.RIGHT
        startPointPanel.add(startPointXLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        startPointXField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(startX = startPointXField.text.toDoubleOrNull())
            }
        } })
        startPointXField.font = computerModernFont
        startPointPanel.add(startPointXField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        startPointYLabel.font = computerModernFont
        startPointYLabel.horizontalAlignment = JLabel.RIGHT
        startPointPanel.add(startPointYLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        startPointYField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(startY = startPointYField.text.toDoubleOrNull())
            }
        } })
        startPointYField.font = computerModernFont
        startPointPanel.add(startPointYField, GridBagConstraints(1, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        startPointPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null,
                        "<html>y<sub><small>0</small></sub> = y(x<sub><small>0</small></sub>)",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        computerModernFont),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        controlPanel.add(startPointPanel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        otherEndXLabel.font = computerModernFont
        otherEndXPanel.add(otherEndXLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        otherEndXField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(endX = otherEndXField.text.toDoubleOrNull())
            }
        } })
        otherEndXField.font = computerModernFont
        otherEndXPanel.add(otherEndXField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        otherPanel.add(otherEndXPanel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        otherPrecisionPanel.add(otherPrecisionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        otherPrecisionField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(precision = otherPrecisionField.text.toDoubleOrNull())
            }
        } })
        otherPrecisionField.font = computerModernFont
        otherPrecisionPanel.add(otherPrecisionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        otherPanel.add(otherPrecisionPanel, GridBagConstraints(0, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        otherPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Other"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        controlPanel.add(otherPanel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        resultPanel.add(resultLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        resultRungeKuttaColorButton.preferredSize = Dimension(20, 20)
        resultRungeKuttaColorButton.minimumSize = Dimension(20, 20)
        resultRungeKuttaColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose Runge-Kutta solution plot color", store.get().adamsSolutionInterpolationColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(rungeKuttaSolutionInterpolationColor = newColor) }
            }
        }
        resultPanel.add(resultRungeKuttaColorButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        resultAdamsColorButton.preferredSize = Dimension(20, 20)
        resultAdamsColorButton.minimumSize = Dimension(20, 20)
        resultAdamsColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose Adams solution plot color", store.get().adamsSolutionInterpolationColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(adamsSolutionInterpolationColor = newColor) }
            }
        }
        resultPanel.add(resultAdamsColorButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        resultArea.isEditable = false
        resultArea.lineWrap = true
        resultArea.wrapStyleWord = true
        resultArea.font = computerModernFont
        resultPanel.add(resultAreaPane, GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        controlPanel.add(resultPanel, GridBagConstraints(0, 3, 1, 1, .0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        _contentPane.add(controlPanel, GridBagConstraints(0, 0, 1, 1, .0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, Insets(0, 0, 0, 5), 0, 0))

        plotPlot.font = computerModernFont
        plotPlot.border = BorderFactory.createLineBorder(null)
        plotPanel.add(plotPlot, GridBagConstraints(0, 0, 7, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        plotAbscissaLabel.font = computerModernFont
        plotPanel.add(plotAbscissaLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))
        plotPanel.add(plotAbscissaFromLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotAbscissaBeginField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaBegin = plotAbscissaBeginField.text.toDoubleOrNull())
            }
        } })
        plotAbscissaBeginField.font = computerModernFont
        plotPanel.add(plotAbscissaBeginField, GridBagConstraints(2, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotPanel.add(plotAbscissaToLabel, GridBagConstraints(3, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotAbscissaEndField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaEnd = plotAbscissaEndField.text.toDoubleOrNull())
            }
        } })
        plotAbscissaEndField.font = computerModernFont
        plotPanel.add(plotAbscissaEndField, GridBagConstraints(4, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        plotAbscissaSwapButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(
                        plotAbscissaBegin = store.plotAbscissaEnd,
                        plotAbscissaEnd = store.plotAbscissaBegin
                )
            }
        }
        plotAbscissaSwapButton.minimumSize = Dimension(20, 20)
        plotAbscissaSwapButton.preferredSize = Dimension(20, 20)
        plotPanel.add(plotAbscissaSwapButton, GridBagConstraints(5, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotOrdinateLabel.font = computerModernFont
        plotPanel.add(plotOrdinateLabel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))
        plotPanel.add(plotOrdinateFromLabel, GridBagConstraints(1, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotOrdinateBeginField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotOrdinateBegin = plotOrdinateBeginField.text.toDoubleOrNull())
            }
        } })
        plotOrdinateBeginField.font = computerModernFont
        plotPanel.add(plotOrdinateBeginField, GridBagConstraints(2, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))
        plotPanel.add(plotOrdinateToLabel, GridBagConstraints(3, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotOrdinateEndField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotOrdinateEnd = plotOrdinateEndField.text.toDoubleOrNull())
            }
        } })
        plotOrdinateEndField.font = computerModernFont
        plotPanel.add(plotOrdinateEndField, GridBagConstraints(4, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        plotOrdinateSwapButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(
                        plotOrdinateBegin = store.plotOrdinateEnd,
                        plotOrdinateEnd = store.plotOrdinateBegin
                )
            }
        }
        plotOrdinateSwapButton.minimumSize = Dimension(20, 20)
        plotOrdinateSwapButton.preferredSize = Dimension(20, 20)
        plotPanel.add(plotOrdinateSwapButton, GridBagConstraints(5, 2, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotButtonsFitButton.addActionListener {
            store.mutateIfOther { store ->
                if (
                        store.plotAbscissaBegin == null || store.plotAbscissaEnd == null ||
                        store.plotOrdinateBegin == null || store.plotOrdinateEnd == null
                ) {
                    return@mutateIfOther store
                }

                val width = plotPlot.width
                val height = plotPlot.height

                val intervalAbscissa = store.plotAbscissaEnd - store.plotAbscissaBegin
                val intervalOrdinate = store.plotOrdinateEnd - store.plotOrdinateBegin
                val centerAbscissa = store.plotAbscissaBegin + intervalAbscissa / 2
                val centerOrdinate = store.plotOrdinateBegin + intervalOrdinate / 2

                val intervalOrdinateForAbscissa = intervalAbscissa * height / width
                val intervalAbscissaForOrdinate = intervalOrdinate * width / height

                val areaForAbscissa = intervalAbscissa * intervalOrdinateForAbscissa
                val areaForOrdinate = intervalAbscissaForOrdinate * intervalOrdinate

                val (newIntervalAbscissa, newIntervalOrdinate) =
                        if (areaForAbscissa >= areaForOrdinate) {
                            intervalAbscissa to (abs(intervalOrdinateForAbscissa) * if (intervalOrdinate < 0) -1 else 1)
                        } else {
                            (abs(intervalAbscissaForOrdinate) * if (intervalAbscissa < 0) -1 else 1) to intervalOrdinate
                        }

                return@mutateIfOther store.copy(
                        plotAbscissaBegin = centerAbscissa - newIntervalAbscissa / 2,
                        plotAbscissaEnd = centerAbscissa + newIntervalAbscissa / 2,
                        plotOrdinateBegin = centerOrdinate - newIntervalOrdinate / 2,
                        plotOrdinateEnd = centerOrdinate + newIntervalOrdinate / 2
                )
            }
        }
        plotButtonsPanel.add(plotButtonsFitButton, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        plotPanel.add(plotButtonsPanel, GridBagConstraints(6, 1, 1, 3, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))

        _contentPane.add(plotPanel, GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        contentPane = _contentPane
        pack()

        thread {
            onStoreChange(store)
            store.onChange.listeners.addWithoutValue(this::onStoreChange)

            SwingUtilities.invokeLater {
                val plotSide = max(plotPlot.width, plotPlot.height)
                plotPlot.preferredSize = Dimension(plotSide, plotSide)
                plotPlot.size = Dimension(plotSide, plotSide)
                pack()

                minimumSize = size
                pack()
            }
        }
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeAndWait {
            onStoreChangeRun = true

            // expression
            val expressionValid = store.expression.toString().trim() == "" || store.expressionValid
            expressionField.background = when (expressionValid) {
                true -> defaultTextFieldBackgroundColor
                else -> INVALID_VALUE_COLOR
            }

            mapOf(
                    startPointXField to store.startX, // startX
                    startPointYField to store.startY, // startY
                    otherEndXField to store.endX, // endX
                    otherPrecisionField to store.precision, // precision
                    plotAbscissaBeginField to store.plotAbscissaBegin, // plotAbscissaBegin
                    plotAbscissaEndField to store.plotAbscissaEnd, // plotAbscissaEnd
                    plotOrdinateBeginField to store.plotOrdinateBegin, // plotOrdinateBegin
                    plotOrdinateEndField to store.plotOrdinateEnd // plotOrdinateEnd
            ).forEach { (field, value) ->
                updateText(field, value, String::toDoubleOrNull, Double?::toPlainString)
                updateBackground(field, value)
            }

            // rungeKuttaSolutionInterpolationColor
            resultRungeKuttaColorButton.icon = ColorIconFactory.getIcon(store.rungeKuttaSolutionInterpolationColor)

            // adamsSolutionInterpolation
            updateText(resultArea, store.adamsSolutionInterpolation) { toString() }

            // adamsSolutionInterpolationColor
            resultAdamsColorButton.icon = ColorIconFactory.getIcon(store.adamsSolutionInterpolationColor)

            println("Store changed: $store")
        }

        SwingUtilities.invokeLater {
            onStoreChangeRun = false
        }
    }

    private inline fun <T> updateText(
            component: JTextComponent,
            value: T,
            fromString: String.() -> T,
            toString: T.() -> String?
    ) {
        if (value != null && component.text.fromString() != value) {
            component.text = (value as T).toString()
        }
    }

    private inline fun <T> updateText(
            component: JTextComponent,
            value: T,
            toString: T.() -> String?
    ) {
        if (value != null && component.text != (value as T).toString()) {
            component.text = (value as T).toString()
        }
    }
    private fun <T> updateBackground(
            component: JComponent,
            value: T,
            elseColor: Color,
            nullColor: Color = INVALID_VALUE_COLOR
    ) {
        component.background = when (value) {
            null -> nullColor
            else -> elseColor
        }
    }

    private fun <T> updateBackground(component: JComponent, value: T) {
        updateBackground(component, value, defaultTextFieldBackgroundColor)
    }

    private inline fun manualChange(block: () -> Unit) {
        if (!onStoreChangeRun) {
            block()
        }
    }
}
