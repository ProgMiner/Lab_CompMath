package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab4.APP_NAME
import ru.byprogminer.compmath.lab4.APP_VERSION
import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.gui.util.*
import ru.byprogminer.compmath.lab4.parser.parse
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import ru.byprogminer.compmath.lab4.util.reactiveHolder
import ru.byprogminer.compmath.lab4.util.toPlainString
import java.awt.*
import javax.swing.*
import kotlin.concurrent.thread
import kotlin.math.abs

class MainWindow(store: ReactiveHolder<Store>): JFrame("$APP_NAME v$APP_VERSION") {

    companion object {

        private val ADD_ICON = createImageIcon("add.png").scale(16, 16)
        private val REMOVE_ICON = createImageIcon("remove.png").scale(16, 16)
        private val SWAP_ICON = createImageIcon("swap.png").scale(16, 16)

        private val INVALID_VALUE_COLOR = Color.RED
    }

    private val _contentPane = JPanel(GridBagLayout())
    private val controlPanel = JPanel(GridBagLayout())
    private val functionsPanel = JPanel(GridBagLayout())
    private val functionsFunctionLabel = JLabel("f(x) =")
    private val functionsFunctionField = JTextField(15)
    private val functionsFunctionColorButton = JButton()
    private val functionsInterpolationLabel = JLabel("Ln(x) =")
    private val functionsInterpolationField = JTextField(15)
    private val functionsInterpolationColorButton = JButton()
    private val interpolationPointsPanel = JPanel(GridBagLayout())
    private val interpolationPointsLabel = JLabel("Interpolation points:")
    private val interpolationPointsAddButton = JButton(ADD_ICON)
    private val interpolationPointsRemoveButton = JButton(REMOVE_ICON)
    private val interpolationPointsTableModel = InterpolationPointsTableModel(store)
    private val interpolationPointsTable = JTable(interpolationPointsTableModel)
    private val interpolationPointsTablePane = JScrollPane(interpolationPointsTable)
    private val valuesPanel = JPanel(GridBagLayout())
    private val valuesLabel = JLabel("Values:")
    private val valuesAddButton = JButton(ADD_ICON)
    private val valuesRemoveButton = JButton(REMOVE_ICON)
    private val valuesTableModel = ValuesTableModel(store)
    private val valuesTable = JTable(valuesTableModel)
    private val valuesTablePane = JScrollPane(valuesTable)
    private val plotPanel = JPanel(GridBagLayout())
    private val plotPlot = Plot(store)
    private val plotAbscissaVariablePanel = JPanel(GridBagLayout())
    private val plotAbscissaVariableField = JTextField()
    private val plotAbscissaVariableColonLabel = JLabel(":")
    private val plotAbscissaFromLabel = JLabel("from")
    private val plotAbscissaBeginField = JTextField(8)
    private val plotAbscissaToLabel = JLabel("to")
    private val plotAbscissaEndField = JTextField(8)
    private val plotAbscissaSwapButton = JButton(SWAP_ICON)
    private val plotOrdinateLabel = JLabel("Ordinate:")
    private val plotOrdinateFromLabel = JLabel("from")
    private val plotOrdinateBeginField = JTextField(8)
    private val plotOrdinateToLabel = JLabel("to")
    private val plotOrdinateEndField = JTextField(8)
    private val plotOrdinateSwapButton = JButton(SWAP_ICON)
    private val plotButtonsPanel = JPanel(GridBagLayout())
    private val plotButtonsFitButton = JButton("Fit 1x1")

    private val defaultTextFieldBackgroundColor = functionsFunctionField.background

    private val selectedInterpolationPoint = reactiveHolder<Int?>(null)
    private val selectedValuesPoint = reactiveHolder<Int?>(null)
    private var onStoreChangeRun = false

    init {
        functionsFunctionLabel.font = computerModernFont
        functionsFunctionLabel.horizontalAlignment = JLabel.RIGHT
        functionsPanel.add(functionsFunctionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        functionsFunctionField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(function = parse(functionsFunctionField.text))
            }
        } })
        functionsFunctionField.font = computerModernFont
        functionsPanel.add(functionsFunctionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        functionsFunctionColorButton.preferredSize = Dimension(20, 20)
        functionsFunctionColorButton.minimumSize = Dimension(20, 20)
        functionsFunctionColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose function color", store.get().functionColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(functionColor = newColor) }
            }
        }
        functionsPanel.add(functionsFunctionColorButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        functionsInterpolationLabel.font = computerModernFont
        functionsInterpolationLabel.horizontalAlignment = JLabel.RIGHT
        functionsPanel.add(functionsInterpolationLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        functionsInterpolationField.isEnabled = false
        functionsInterpolationField.font = computerModernFont
        functionsPanel.add(functionsInterpolationField, GridBagConstraints(1, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        functionsInterpolationColorButton.preferredSize = Dimension(20, 20)
        functionsInterpolationColorButton.minimumSize = Dimension(20, 20)
        functionsInterpolationColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose interpolation color", store.get().interpolationColor)
            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(interpolationColor = newColor) }
            }
        }
        functionsPanel.add(functionsInterpolationColorButton, GridBagConstraints(2, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))
        controlPanel.add(functionsPanel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        controlPanel.add(JSeparator(JSeparator.HORIZONTAL), GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        interpolationPointsPanel.add(interpolationPointsLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        interpolationPointsAddButton.preferredSize = Dimension(20, 20)
        interpolationPointsAddButton.minimumSize = Dimension(20, 20)
        interpolationPointsAddButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(interpolationPoints = store.interpolationPoints + listOf(.0))
            }
        }
        interpolationPointsPanel.add(interpolationPointsAddButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        interpolationPointsRemoveButton.isEnabled = false
        interpolationPointsRemoveButton.preferredSize = Dimension(20, 20)
        interpolationPointsRemoveButton.minimumSize = Dimension(20, 20)
        interpolationPointsRemoveButton.addActionListener {
            val index = selectedInterpolationPoint.get()

            store.mutateIfOther { store ->
                store.copy(interpolationPoints = store.interpolationPoints.filterIndexed { i, _ -> i != index })
            }
        }
        selectedInterpolationPoint.onChange.listeners.addWithoutValue { holder ->
            interpolationPointsRemoveButton.isEnabled = holder.get() != null
        }
        interpolationPointsPanel.add(interpolationPointsRemoveButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        interpolationPointsTable.font = computerModernFont
        interpolationPointsTable.selectionModel.addListSelectionListener {
            selectedInterpolationPoint.setIfOther(interpolationPointsTable.selectedRow.let {
                when (it) {
                    -1 -> null
                    else -> it
                }
            })
        }
        interpolationPointsTableModel.addTableModelListener {
            val oldIndex = selectedInterpolationPoint.get()

            if (oldIndex != null) {
                SwingUtilities.invokeLater {
                    val index = if (interpolationPointsTableModel.rowCount <= oldIndex) {
                        interpolationPointsTableModel.rowCount - 1
                    } else {
                        oldIndex
                    }

                    if (index >= 0) {
                        interpolationPointsTable.setRowSelectionInterval(index, index)
                    }
                }
            }
        }
        interpolationPointsTable.rowHeight = 24
        interpolationPointsTable.tableHeader = null
        interpolationPointsTablePane.preferredSize = Dimension(150, 200)
        interpolationPointsPanel.add(interpolationPointsTablePane, GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))
        controlPanel.add(interpolationPointsPanel, GridBagConstraints(0, 2, 1, 1, .0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))
        controlPanel.add(JSeparator(JSeparator.HORIZONTAL), GridBagConstraints(0, 3, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        valuesPanel.add(valuesLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        valuesAddButton.preferredSize = Dimension(20, 20)
        valuesAddButton.minimumSize = Dimension(20, 20)
        valuesAddButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(valuePoints = store.valuePoints + listOf(.0))
            }
        }
        valuesPanel.add(valuesAddButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        valuesRemoveButton.isEnabled = false
        valuesRemoveButton.preferredSize = Dimension(20, 20)
        valuesRemoveButton.minimumSize = Dimension(20, 20)
        valuesRemoveButton.addActionListener {
            val index = selectedValuesPoint.get()

            store.mutateIfOther { store ->
                store.copy(valuePoints = store.valuePoints.filterIndexed { i, _ -> i != index })
            }
        }
        selectedValuesPoint.onChange.listeners.addWithoutValue { holder ->
            valuesRemoveButton.isEnabled = holder.get() != null
        }
        valuesPanel.add(valuesRemoveButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        valuesTable.font = computerModernFont
        valuesTable.tableHeader.font = computerModernFont
        valuesTable.selectionModel.addListSelectionListener {
            selectedValuesPoint.setIfOther(valuesTable.selectedRow.let {
                when (it) {
                    -1 -> null
                    else -> it
                }
            })
        }
        valuesTableModel.addTableModelListener {
            val oldIndex = selectedValuesPoint.get()

            if (oldIndex != null) {
                SwingUtilities.invokeLater {
                    val index = if (valuesTableModel.rowCount <= oldIndex) {
                        valuesTableModel.rowCount - 1
                    } else {
                        oldIndex
                    }

                    if (index >= 0) {
                        valuesTable.setRowSelectionInterval(index, index)
                    }
                }
            }
        }
        valuesTable.rowHeight = 24
        valuesTablePane.preferredSize = Dimension(150, 300)
        valuesPanel.add(valuesTablePane, GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))
        controlPanel.add(valuesPanel, GridBagConstraints(0, 4, 1, 1, .0, 1.68, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))
        _contentPane.add(controlPanel, GridBagConstraints(0, 0, 1, 1, .0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, Insets(0, 0, 0, 5), 0, 0))

        plotPlot.font = computerModernFont
        plotPlot.border = BorderFactory.createLineBorder(null)
        plotPanel.add(plotPlot, GridBagConstraints(0, 0, 7, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        plotAbscissaVariableField.isEnabled = false
        plotAbscissaVariableField.font = computerModernFont
        plotAbscissaVariablePanel.add(plotAbscissaVariableField, GridBagConstraints(0, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        plotAbscissaVariablePanel.add(plotAbscissaVariableColonLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        plotPanel.add(plotAbscissaVariablePanel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotPanel.add(plotAbscissaFromLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotAbscissaBeginField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaBegin = plotAbscissaBeginField.text.toDoubleOrNull())
            }
        } })
        plotAbscissaBeginField.font = computerModernFont
        plotPanel.add(plotAbscissaBeginField, GridBagConstraints(2, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotPanel.add(plotAbscissaToLabel, GridBagConstraints(3, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotAbscissaEndField.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaEnd = plotAbscissaEndField.text.toDoubleOrNull())
            }
        } })
        plotAbscissaEndField.font = computerModernFont
        plotPanel.add(plotAbscissaEndField, GridBagConstraints(4, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

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

        plotPanel.add(plotOrdinateLabel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))
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
        plotPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        _contentPane.add(plotPanel, GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        contentPane = _contentPane
        pack()

        plotPanel.preferredSize = Dimension(plotPanel.height, plotPanel.height)
        pack()

        minimumSize = size

        thread { onStoreChange(store) }
        store.onChange.listeners.addWithoutValue(this::onStoreChange)
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeAndWait {
            onStoreChangeRun = true

            // function
            val equationValid = store.function.toString().trim() == "" || store.variables.size == 1

            functionsFunctionField.background = when {
                equationValid -> defaultTextFieldBackgroundColor
                else -> INVALID_VALUE_COLOR
            }

            // functionColor
            functionsFunctionColorButton.icon = ColorIconFactory.getIcon(store.functionColor)

            // interpolation
            functionsInterpolationField.text = store.interpolation.toString()

            // interpolationColor
            functionsInterpolationColorButton.icon = ColorIconFactory.getIcon(store.interpolationColor)

            // plotAbscissaVariable
            if (plotAbscissaVariableField.text != store.plotAbscissaVariable) {
                plotAbscissaVariableField.text = store.plotAbscissaVariable
            }

            // plotAbscissaBegin
            if (plotAbscissaBeginField.text.toDoubleOrNull() != store.plotAbscissaBegin) {
                plotAbscissaBeginField.text = store.plotAbscissaBegin?.toPlainString()
            }

            plotAbscissaBeginField.background = when (store.plotAbscissaBegin) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            // plotAbscissaEnd
            if (plotAbscissaEndField.text.toDoubleOrNull() != store.plotAbscissaEnd) {
                plotAbscissaEndField.text = store.plotAbscissaEnd?.toPlainString()
            }

            plotAbscissaEndField.background = when (store.plotAbscissaEnd) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            // plotOrdinateBegin
            if (plotOrdinateBeginField.text.toDoubleOrNull() != store.plotOrdinateBegin) {
                plotOrdinateBeginField.text = store.plotOrdinateBegin?.toPlainString()
            }

            plotOrdinateBeginField.background = when (store.plotOrdinateBegin) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            // plotOrdinateEnd
            if (plotOrdinateEndField.text.toDoubleOrNull() != store.plotOrdinateEnd) {
                plotOrdinateEndField.text = store.plotOrdinateEnd?.toPlainString()
            }

            plotOrdinateEndField.background = when (store.plotOrdinateEnd) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            println("Store changed: $store")
        }

        SwingUtilities.invokeLater {
            onStoreChangeRun = false
        }
    }

    private inline fun manualChange(block: () -> Unit) {
        if (!onStoreChangeRun) {
            block()
        }
    }
}
