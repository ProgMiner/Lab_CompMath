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
import ru.byprogminer.compmath.lab5.util.toStringOrNull
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
    private val detailsPanel = JPanel(GridBagLayout())
    private val detailsEndXPanel = JPanel(GridBagLayout())
    private val detailsEndXLabel = JLabel("<html>x<sub><small>n</small></sub> =</html>")
    private val detailsEndXField = JTextField(5)
    private val detailsPrecisionPanel = JPanel(GridBagLayout())
    private val detailsPrecisionLabel = JLabel("Precision:")
    private val detailsPrecisionField = JTextField(5)
    private val detailsOrderPanel = JPanel(GridBagLayout())
    private val detailsOrderLabel = JLabel("Order:")
    private val detailsOrderField = JTextField(5)
    private val resultPanel = JPanel(GridBagLayout())
    private val resultLabel = JLabel("Result:")
    private val resultColorButton = JButton()
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

        detailsEndXLabel.font = computerModernFont
        detailsEndXPanel.add(detailsEndXLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        detailsEndXField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(endX = detailsEndXField.text.toDoubleOrNull())
            }
        } })
        detailsEndXField.font = computerModernFont
        detailsEndXPanel.add(detailsEndXField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        detailsPanel.add(detailsEndXPanel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        detailsPrecisionPanel.add(detailsPrecisionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        detailsPrecisionField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(precision = detailsPrecisionField.text.toDoubleOrNull())
            }
        } })
        detailsPrecisionField.font = computerModernFont
        detailsPrecisionPanel.add(detailsPrecisionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        detailsPanel.add(detailsPrecisionPanel, GridBagConstraints(0, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        detailsOrderPanel.add(detailsOrderLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))

        detailsOrderField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(order = detailsOrderField.text.toIntOrNull())
            }
        } })
        detailsOrderField.font = computerModernFont
        detailsOrderPanel.add(detailsOrderField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        detailsPanel.add(detailsOrderPanel, GridBagConstraints(0, 2, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        detailsPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Details"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        controlPanel.add(detailsPanel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        resultPanel.add(resultLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        resultColorButton.preferredSize = Dimension(20, 20)
        resultColorButton.minimumSize = Dimension(20, 20)
        resultColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose derivative plot color", store.get().derivativeInterpolationColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(derivativeInterpolationColor = newColor) }
            }
        }
        resultPanel.add(resultColorButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        resultArea.isEditable = false
        resultArea.font = computerModernFont
        resultPanel.add(resultAreaPane, GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
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
                val intervalAbscissaForOrdinate = intervalOrdinate * width / height.toLong()

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

        val plotSide = max(plotPanel.width, plotPanel.height)
        plotPanel.preferredSize = Dimension(plotSide, plotSide)
        pack()

        minimumSize = size

        thread { onStoreChange(store) }
        store.onChange.listeners.addWithoutValue(this::onStoreChange)
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeAndWait {
            onStoreChangeRun = true

            // expression
            val expressionValid = store.expression.toString().trim() == "" ||
                    store.variables.none { it == "x" || it == "y" }
            expressionField.background = when (expressionValid) {
                true -> defaultTextFieldBackgroundColor
                else -> INVALID_VALUE_COLOR
            }

            mapOf(
                    startPointXField to store.startX, // startX
                    startPointYField to store.startY, // startY
                    detailsEndXField to store.endX, // endX
                    detailsPrecisionField to store.precision, // precision
                    plotAbscissaBeginField to store.plotAbscissaBegin, // plotAbscissaBegin
                    plotAbscissaEndField to store.plotAbscissaEnd, // plotAbscissaEnd
                    plotOrdinateBeginField to store.plotOrdinateBegin, // plotOrdinateBegin
                    plotOrdinateEndField to store.plotOrdinateEnd // plotOrdinateEnd
            ).forEach { (field, value) ->
                updateText(field, value, String::toDoubleOrNull, Double?::toPlainString)
                updateBackground(field, value)
            }

            // order
            updateText(detailsOrderField, store.order, String::toIntOrNull, Int?::toStringOrNull)
            updateBackground(detailsOrderField, store.order)

            // derivativeInterpolation
            updateText(resultArea, store.derivativeInterpolation) { toString() }

            // derivativeInterpolationColor
            resultColorButton.icon = ColorIconFactory.getIcon(store.derivativeInterpolationColor)

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
        if (component.text.fromString() != value) {
            component.text = value.toString()
        }
    }

    private inline fun <T> updateText(
            component: JTextComponent,
            value: T,
            toString: T.() -> String?
    ) {
        if (component.text != value.toString()) {
            component.text = value.toString()
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
