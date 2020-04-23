package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab4.APP_NAME
import ru.byprogminer.compmath.lab4.APP_VERSION
import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.equation.InvalidExpression
import ru.byprogminer.compmath.lab4.gui.util.*
import ru.byprogminer.compmath.lab4.parser.parse
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import ru.byprogminer.compmath.lab4.util.reactiveHolder
import ru.byprogminer.compmath.lab4.util.toPlainString
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel
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
    private val functionPanel = JPanel(GridBagLayout())
    private val functionLabel = JLabel("f(x) =")
    private val functionField = JTextField(15)
    private val functionColorButton = JButton()
    private val interpolationFunctionPanel = JPanel(GridBagLayout())
    private val interpolationFunctionLabel = JLabel("Ln(x) =")
    private val interpolationFunctionField = JTextField(15)
    private val interpolationFunctionColorButton = JButton()
    private val interpolationPointsPanel = JPanel(GridBagLayout())
    private val interpolationPointsLabel = JLabel("Interpolation points:")
    private val interpolationPointsAddButton = JButton(ADD_ICON)
    private val interpolationPointsRemoveButton = JButton(REMOVE_ICON)
    private val interpolationPointsListModel = DefaultListModel<Fraction>()
    private val interpolationPointsList = JList(interpolationPointsListModel)
    private val valuesPanel = JPanel(GridBagLayout())
    private val valuesLabel = JLabel("Values:")
    private val valuesAddButton = JButton(ADD_ICON)
    private val valuesRemoveButton = JButton(REMOVE_ICON)
    private val valuesTableModel = DefaultTableModel()
    private val valuesTable = JTable(valuesTableModel)
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

    private val defaultTextFieldBackgroundColor = functionField.background

    private val selectedInterpolationPoint = reactiveHolder<Int?>(null)
    private val selectedValuesPoint = reactiveHolder<Int?>(null)
    private var onStoreChangeRun = false

    init {
        modeEquationPanel.add(modeEquationEquationLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeEquationColorButton.preferredSize = Dimension(20, 20)
        modeEquationColorButton.minimumSize = Dimension(20, 20)
        modeEquationColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose equation color", store.get().expressionColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(expressionColor = newColor) }
            }
        }
        modeEquationPanel.add(modeEquationColorButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        modeEquationEquationArea.document.addDocumentListener(documentAdapter { manualChange {
            store.mutateIfOther { store -> store.copy(expression = parse(modeEquationEquationArea.text)) }
        } })
        modeEquationPanel.add(modeEquationEquationPane, GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        modeEquationMethodButtons.toList().forEachIndexed { i, (_, button) ->
            modeEquationMethodPanel.add(button, GridBagConstraints(0, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, if (i == modeEquationMethodButtons.size - 1) 0 else 5, 0), 0, 0))
        }
        modeEquationMethodPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Method"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        modeEquationPanel.add(modeEquationMethodPanel, GridBagConstraints(0, 2, 2, 1, 1.0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        modeEquationPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        modeTabbedPane.add(modeEquationPanel, "Equation")

        modeSystemPanel.add(modeSystemEquationsLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeSystemAddEquationButton.preferredSize = Dimension(20, 20)
        modeSystemAddEquationButton.minimumSize = Dimension(20, 20)
        modeSystemAddEquationButton.addActionListener {
            store.mutateIfOther { store -> store.copy(equations = store.equations + listOf(InvalidExpression("") to randomColor())) }
        }
        modeSystemPanel.add(modeSystemAddEquationButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        modeSystemRemoveEquationButton.isEnabled = false
        modeSystemRemoveEquationButton.preferredSize = Dimension(20, 20)
        modeSystemRemoveEquationButton.minimumSize = Dimension(20, 20)
        modeSystemRemoveEquationButton.addActionListener {
            val index = selectedSystemEquation.get()

            store.mutateIfOther { store -> store.copy(equations = store.equations.filterIndexed { i, _ -> i != index }) }
        }
        selectedSystemEquation.onChange.listeners.addWithoutValue { holder ->
            modeSystemRemoveEquationButton.isEnabled = holder.get() != null
        }
        modeSystemPanel.add(modeSystemRemoveEquationButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        modeSystemEquationsTable.selectionModel.addListSelectionListener { manualChange {
            selectedSystemEquation.setIfOther(modeSystemEquationsTable.selectedRow.let {
                when (it) {
                    -1 -> null
                    else -> it
                }
            })
        } }
        modeSystemEquationsTableModel.addTableModelListener {
            val oldIndex = selectedSystemEquation.get()

            if (oldIndex != null) {
                SwingUtilities.invokeLater {
                    val index = if (modeSystemEquationsTableModel.rowCount <= oldIndex) {
                        modeSystemEquationsTableModel.rowCount - 1
                    } else {
                        oldIndex
                    }

                    if (index >= 0) {
                        modeSystemEquationsTable.setRowSelectionInterval(index, index)
                    }
                }
            }
        }
        modeSystemEquationsTable.rowHeight = 24
        modeSystemEquationsPane.preferredSize = Dimension(150, 300)
        modeSystemPanel.add(modeSystemEquationsPane, GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        modeSystemApproximationWindow.setLocationRelativeTo(this)
        modeSystemApproximationButton.addActionListener {
            modeSystemApproximationWindow.isVisible = !modeSystemApproximationWindow.isVisible
        }
        modeSystemPanel.add(modeSystemApproximationButton, GridBagConstraints(0, 2, 3, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        modeSystemMethodButtons.toList().forEachIndexed { i, (_, button) ->
            modeSystemMethodPanel.add(button, GridBagConstraints(0, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, if (i == modeEquationMethodButtons.size - 1) 0 else 5, 0), 0, 0))
        }
        modeSystemMethodPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Method"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        modeSystemPanel.add(modeSystemMethodPanel, GridBagConstraints(0, 3, 3, 1, .0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        modeSystemPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        modeTabbedPane.add(modeSystemPanel, "Equations system")
        modeTabbedPane.addChangeListener { manualChange {
            store.mutateIfOther { store -> store.copy(mode = Store.Mode.values()[modeTabbedPane.selectedIndex]) }
        } }
        _contentPane.add(modeTabbedPane, GridBagConstraints(0, 1, 1, 1, .0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.VERTICAL, Insets(0, 0, 0, 5), 0, 0))

        plotRootsPlotPlot.border = BorderFactory.createLineBorder(null)
        plotRootsPlotPanel.add(plotRootsPlotPlot, GridBagConstraints(0, 0, 7, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        plotRootsPlotAbscissaVariableComboBox.addActionListener { manualChange {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaVariable = plotRootsPlotAbscissaVariableComboBoxModel.selectedItem)
            }
        } }
        plotRootsPlotAbscissaVariablePanel.add(plotRootsPlotAbscissaVariableComboBox, GridBagConstraints(0, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotAbscissaVariablePanel.add(plotRootsPlotAbscissaVariableColonLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaVariablePanel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaFromLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotAbscissaBeginField.document.addDocumentListener(documentAdapter { manualChange {
            val value = plotRootsPlotAbscissaBeginField.text.toDoubleOrNull()

            if (value != null) {
                store.mutateIfOther { store ->
                    store.copy(plotAbscissaBegin = value)
                }
            }

            plotRootsPlotAbscissaBeginField.background = if (value != null) {
                defaultTextFieldBackgroundColor
            } else {
                INVALID_VALUE_COLOR
            }
        } })
        plotRootsPlotPanel.add(plotRootsPlotAbscissaBeginField, GridBagConstraints(2, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaToLabel, GridBagConstraints(3, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotAbscissaEndField.document.addDocumentListener(documentAdapter { manualChange {
            val value = plotRootsPlotAbscissaEndField.text.toDoubleOrNull()

            if (value != null) {
                store.mutateIfOther { store ->
                    store.copy(plotAbscissaEnd = value)
                }
            }

            plotRootsPlotAbscissaEndField.background = if (value != null) {
                defaultTextFieldBackgroundColor
            } else {
                INVALID_VALUE_COLOR
            }
        } })
        plotRootsPlotPanel.add(plotRootsPlotAbscissaEndField, GridBagConstraints(4, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotAbscissaSwapButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(
                        plotAbscissaBegin = store.plotAbscissaEnd,
                        plotAbscissaEnd = store.plotAbscissaBegin
                )
            }
        }
        plotRootsPlotAbscissaSwapButton.minimumSize = Dimension(20, 20)
        plotRootsPlotAbscissaSwapButton.preferredSize = Dimension(20, 20)
        plotRootsPlotPanel.add(plotRootsPlotAbscissaSwapButton, GridBagConstraints(5, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotPanel.add(plotRootsPlotOrdinateLabel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotOrdinateFromLabel, GridBagConstraints(1, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotOrdinateBeginField.document.addDocumentListener(documentAdapter { manualChange {
            val value = plotRootsPlotOrdinateBeginField.text.toDoubleOrNull()

            if (value != null) {
                store.mutateIfOther { store ->
                    store.copy(plotOrdinateBegin = value)
                }
            }

            plotRootsPlotOrdinateBeginField.background = if (value != null) {
                defaultTextFieldBackgroundColor
            } else {
                INVALID_VALUE_COLOR
            }
        } })
        plotRootsPlotPanel.add(plotRootsPlotOrdinateBeginField, GridBagConstraints(2, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotOrdinateToLabel, GridBagConstraints(3, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotOrdinateEndField.document.addDocumentListener(documentAdapter { manualChange {
            val value = plotRootsPlotOrdinateEndField.text.toDoubleOrNull()

            if (value != null) {
                store.mutateIfOther { store ->
                    store.copy(plotOrdinateEnd = value)
                }
            }

            plotRootsPlotOrdinateEndField.background = if (value != null) {
                defaultTextFieldBackgroundColor
            } else {
                INVALID_VALUE_COLOR
            }
        } })
        plotRootsPlotPanel.add(plotRootsPlotOrdinateEndField, GridBagConstraints(4, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotOrdinateSwapButton.addActionListener {
            store.mutateIfOther { store ->
                store.copy(
                        plotOrdinateBegin = store.plotOrdinateEnd,
                        plotOrdinateEnd = store.plotOrdinateBegin
                )
            }
        }
        plotRootsPlotOrdinateSwapButton.minimumSize = Dimension(20, 20)
        plotRootsPlotOrdinateSwapButton.preferredSize = Dimension(20, 20)
        plotRootsPlotPanel.add(plotRootsPlotOrdinateSwapButton, GridBagConstraints(5, 2, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotPanel.add(plotRootsPlotModeLabel, GridBagConstraints(0, 3, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotRootsPlotModeButtons.toList().forEachIndexed { i, (_, button) ->
            plotRootsPlotModePanel.add(button, GridBagConstraints(i, 0, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, if (i == plotRootsPlotModeButtons.size - 1) 0 else 5), 0, 0))
        }
        plotRootsPlotPanel.add(plotRootsPlotModePanel, GridBagConstraints(1, 3, 5, 1, .0, .0, GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotButtonsFitButton.addActionListener {
            store.mutateIfOther { store ->
                val width = plotRootsPlotPlot.width
                val height = plotRootsPlotPlot.height

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

        plotRootsPlotButtonsSliceWindow.setLocationRelativeTo(this)
        plotRootsPlotButtonsSliceButton.addActionListener {
            plotRootsPlotButtonsSliceWindow.isVisible = !plotRootsPlotButtonsSliceWindow.isVisible
        }
        plotButtonsPanel.add(plotRootsPlotButtonsSliceButton, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotPanel.add(plotButtonsPanel, GridBagConstraints(6, 1, 1, 3, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        plotRootsTabbedPane.add(plotRootsPlotPanel, "Plot")

        plotRootsRootsTable.layout = GridBagLayout()
        plotRootsRootsTable.fillsViewportHeight = true
        plotRootsRootsTable.add(plotRootsRootsTableNoEquationsPlaceholder, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))

        plotRootsRootsTableNoRootsPlaceholder.isVisible = false
        plotRootsRootsTable.add(plotRootsRootsTableNoRootsPlaceholder, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))

        plotRootsRootsTableEquationsOfSeveralVariablesPlaceholder.isVisible = false
        plotRootsRootsTable.add(plotRootsRootsTableEquationsOfSeveralVariablesPlaceholder, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))

        plotRootsRootsTableProgressBar.isVisible = false
        plotRootsRootsTableProgressBar.isIndeterminate = true
        plotRootsRootsTable.add(plotRootsRootsTableProgressBar, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        plotRootsTabbedPane.add(plotRootsRootsPane, "Roots")
        _contentPane.add(plotRootsTabbedPane, GridBagConstraints(1, 0, 0, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        contentPane = _contentPane
        pack()

        plotRootsPlotPlot.preferredSize = Dimension(plotRootsPlotPlot.height, plotRootsPlotPlot.height)
        pack()

        minimumSize = size

        thread { onStoreChange(store) }
        store.onChange.listeners.addWithoutValue(this::onStoreChange)
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeAndWait {
            onStoreChangeRun = true

            // mode
            modeTabbedPane.selectedIndex = store.mode.ordinal

            // start
            precisionIntervalIntervalStartField.background = when (store.begin) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalIntervalStartField.text.toDoubleOrNull() != store.begin) {
                precisionIntervalIntervalStartField.text = store.begin?.toPlainString() ?: ""
            }

            // end
            precisionIntervalIntervalEndField.background = when (store.end) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalIntervalEndField.text.toDoubleOrNull() != store.end) {
                precisionIntervalIntervalEndField.text = store.end?.toPlainString() ?: ""
            }

            // step
            precisionIntervalIntervalCutsField.background = when (store.cuts) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalIntervalCutsField.text.toIntOrNull() != store.cuts) {
                precisionIntervalIntervalCutsField.text = store.cuts?.toString() ?: ""
            }

            // precision
            precisionIntervalPrecisionPrecisionField.background = when (store.precision) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalPrecisionPrecisionField.text.toDoubleOrNull() != store.precision) {
                precisionIntervalPrecisionPrecisionField.text = store.precision?.toPlainString() ?: ""
            }

            // iterations
            precisionIntervalPrecisionIterationsField.background = when (store.iterations) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalPrecisionIterationsField.text.toIntOrNull() != store.iterations) {
                precisionIntervalPrecisionIterationsField.text = store.iterations?.toString() ?: ""
            }

            // equationColor
            modeEquationColorButton.icon = ColorIconFactory.getIcon(store.expressionColor)

            // equation
            val equationValid = store.expression.toString().trim() == "" || store.variables.isNotEmpty()

            modeEquationEquationArea.background = when {
                equationValid -> defaultTextAreaBackgroundColor
                else -> INVALID_VALUE_COLOR
            }

            // method
            modeEquationMethodButtons[store.method]?.isSelected = true

            // systemMethod
            modeSystemMethodButtons[store.systemMethod]?.isSelected = true

            // roots
            val manyVarsEquation = store.mode == Store.Mode.EQUATION && store.variables.size > 1
            val validEquation = store.variables.isNotEmpty()
            val noRoots = store.roots.isNullOrEmpty()
            val inProgress = store.roots == null

            plotRootsRootsTableNoEquationsPlaceholder.isVisible = !validEquation
            plotRootsRootsTableNoRootsPlaceholder.isVisible = !inProgress && noRoots
            plotRootsRootsTableEquationsOfSeveralVariablesPlaceholder.isVisible = manyVarsEquation
            plotRootsRootsTableProgressBar.isVisible = !manyVarsEquation && validEquation && inProgress
            plotRootsRootsTable.repaint()

            // plotAbscissaVariable
            plotRootsPlotAbscissaVariableComboBox.isEnabled = store.plotAbscissaVariable != null

            if (plotRootsPlotAbscissaVariableComboBox.selectedItem != store.plotAbscissaVariable) {
                plotRootsPlotAbscissaVariableComboBox.selectedItem = store.plotAbscissaVariable
            }

            // plotAbscissaBegin
            if (plotRootsPlotAbscissaBeginField.text.toDoubleOrNull() != store.plotAbscissaBegin) {
                plotRootsPlotAbscissaBeginField.text = store.plotAbscissaBegin.toPlainString()
            }

            // plotAbscissaEnd
            if (plotRootsPlotAbscissaEndField.text.toDoubleOrNull() != store.plotAbscissaEnd) {
                plotRootsPlotAbscissaEndField.text = store.plotAbscissaEnd.toPlainString()
            }

            // plotOrdinateBegin
            if (plotRootsPlotOrdinateBeginField.text.toDoubleOrNull() != store.plotOrdinateBegin) {
                plotRootsPlotOrdinateBeginField.text = store.plotOrdinateBegin.toPlainString()
            }

            // plotOrdinateEnd
            if (plotRootsPlotOrdinateEndField.text.toDoubleOrNull() != store.plotOrdinateEnd) {
                plotRootsPlotOrdinateEndField.text = store.plotOrdinateEnd.toPlainString()
            }

            // plotMode
            plotRootsPlotModeButtons[store.plotMode]?.isSelected = true

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
