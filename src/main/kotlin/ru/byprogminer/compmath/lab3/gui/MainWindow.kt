package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.APP_NAME
import ru.byprogminer.compmath.lab3.APP_VERSION
import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.equation.InvalidEquation
import ru.byprogminer.compmath.lab3.gui.util.*
import ru.byprogminer.compmath.lab3.math.BisectionMethod
import ru.byprogminer.compmath.lab3.math.NewtonsMethod
import ru.byprogminer.compmath.lab3.math.SimpleIterationsMethod
import ru.byprogminer.compmath.lab3.parser.parse
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import ru.byprogminer.compmath.lab3.util.reactiveHolder
import java.awt.*
import javax.swing.*
import kotlin.math.abs

class MainWindow(store: ReactiveHolder<Store>): JFrame("$APP_NAME v$APP_VERSION") {

    companion object {

        private val ADD_ICON = createImageIcon("add.png").scale(16, 16)
        private val REMOVE_ICON = createImageIcon("remove.png").scale(16, 16)

        private val INVALID_VALUE_COLOR = Color.RED
    }

    private val _contentPane = JPanel(GridBagLayout())
    private val precisionIntervalTabbedPane = JTabbedPane()
    private val precisionIntervalIntervalPanel = JPanel(GridBagLayout())
    private val precisionIntervalIntervalStartLabel = JLabel("Start:")
    private val precisionIntervalIntervalStartField = JTextField(5)
    private val precisionIntervalIntervalEndLabel = JLabel("End:")
    private val precisionIntervalIntervalEndField = JTextField(5)
    private val precisionIntervalIntervalCutsLabel = JLabel("Cuts:")
    private val precisionIntervalIntervalCutsField = JTextField(5)
    private val precisionIntervalPrecisionPanel = JPanel(GridBagLayout())
    private val precisionIntervalPrecisionPrecisionLabel = JLabel("Precision:")
    private val precisionIntervalPrecisionPrecisionField = JTextField(5)
    private val precisionIntervalPrecisionIterationsLabel = JLabel("Iterations:")
    private val precisionIntervalPrecisionIterationsField = JTextField(5)
    private val modeTabbedPane = JTabbedPane()
    private val modeEquationPanel = JPanel(GridBagLayout())
    private val modeEquationEquationLabel = JLabel("Equation:")
    private val modeEquationColorButton = JButton()
    private val modeEquationEquationArea = JTextArea()
    private val modeEquationEquationPane = JScrollPane(modeEquationEquationArea)
    private val modeEquationMethodPanel = JPanel(GridBagLayout())
    private val modeEquationMethodButtonGroup = ButtonGroup()
    private val modeEquationMethodButtons = mapOf(
            "Bisection method" to BisectionMethod,
            "Simple iterations method" to SimpleIterationsMethod
    ).map { (name, method) -> method to JRadioButton(name).also { it.addActionListener {
        store.mutateIfOther { store -> store.copy(method = method) }
    } }.also(modeEquationMethodButtonGroup::add) }.toMap()

    private val modeSystemPanel = JPanel(GridBagLayout())
    private val modeSystemEquationsLabel = JLabel("System equations:")
    private val modeSystemAddEquationButton = JButton(ADD_ICON)
    private val modeSystemRemoveEquationButton = JButton(REMOVE_ICON)
    private val modeSystemEquationsTableModel = EquationsTableModel(store)
    private val modeSystemEquationsTable = JTable(modeSystemEquationsTableModel)
    private val modeSystemEquationsPane = JScrollPane(modeSystemEquationsTable)
    private val modeSystemMethodPanel = JPanel(GridBagLayout())
    private val modeSystemMethodButtonGroup = ButtonGroup()
    private val modeSystemMethodButtons = mapOf(
            "Newton's method" to NewtonsMethod
    ).map { (name, method) -> method to JRadioButton(name).also { it.addActionListener {
        store.mutateIfOther { store -> store.copy(systemMethod = method) }
    } }.also(modeSystemMethodButtonGroup::add) }.toMap()

    private val plotRootsTabbedPane = JTabbedPane()
    private val plotRootsPlotPanel = JPanel(GridBagLayout())
    private val plotRootsPlotPlot = Plot(store)
    private val plotRootsPlotAbscissaVariablePanel = JPanel(GridBagLayout())
    private val plotRootsPlotAbscissaVariableComboBoxModel = VariablesComboBoxModel(store)
    private val plotRootsPlotAbscissaVariableComboBox = JComboBox(plotRootsPlotAbscissaVariableComboBoxModel)
    private val plotRootsPlotAbscissaVariableColonLabel = JLabel(":")
    private val plotRootsPlotAbscissaFromLabel = JLabel("from")
    private val plotRootsPlotAbscissaBeginField = JTextField(8)
    private val plotRootsPlotAbscissaToLabel = JLabel("to")
    private val plotRootsPlotAbscissaEndField = JTextField(8)
    private val plotRootsPlotOrdinateLabel = JLabel("Ordinate:")
    private val plotRootsPlotOrdinateFromLabel = JLabel("from")
    private val plotRootsPlotOrdinateBeginField = JTextField(8)
    private val plotRootsPlotOrdinateToLabel = JLabel("to")
    private val plotRootsPlotOrdinateEndField = JTextField(8)
    private val plotRootsPlotModeLabel = JLabel("Mode:")
    private val plotRootsPlotModePanel = JPanel(GridBagLayout())
    private val plotRootsPlotModeButtonGroup = ButtonGroup()
    private val plotRootsPlotModeButtons = mapOf(
            "Equations" to Store.PlotMode.EQUATIONS,
            "Functions" to Store.PlotMode.FUNCTIONS
    ).map { (name, mode) -> mode to JRadioButton(name).also { it.addActionListener {
        store.mutateIfOther { store -> store.copy(plotMode = mode) }
    } }.also(plotRootsPlotModeButtonGroup::add) }.toMap()

    private val plotRootsPlotButtonsPanel = JPanel(GridBagLayout())
    private val plotRootsPlotButtonsFitButton = JButton("Fit 1x1")
    private val plotRootsPlotButtonsSliceButton = JButton("Slice")
    private val plotRootsPlotButtonsSliceWindow = SliceWindow(store)
    private val plotRootsRootsTableModel = RootsTableModel(store)
    private val plotRootsRootsTable = JTable(plotRootsRootsTableModel)
    private val plotRootsRootsTableNoEquationsPlaceholder = JLabel("Specify equation or equations system left")
    private val plotRootsRootsTableNoRootsPlaceholder = JLabel("Roots not found")
    private val plotRootsRootsTableEquationsOfSeveralVariablesPlaceholder = JLabel("Equations of several variables is not supported")
    private val plotRootsRootsTableProgressBar = JProgressBar()
    private val plotRootsRootsPane = JScrollPane(plotRootsRootsTable)

    private val defaultTextFieldBackgroundColor = precisionIntervalPrecisionPrecisionField.background
    private val defaultTextAreaBackgroundColor = modeEquationEquationArea.background

    private val selectedSystemEquation = reactiveHolder<Int?>(null)

    init {
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalStartLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        precisionIntervalIntervalStartField.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(begin = precisionIntervalIntervalStartField.text.toDoubleOrNull()) }
        })
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalStartField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalEndLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        precisionIntervalIntervalEndField.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(end = precisionIntervalIntervalEndField.text.toDoubleOrNull()) }
        })
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalEndField, GridBagConstraints(1, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalCutsLabel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        precisionIntervalIntervalCutsField.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(cuts = precisionIntervalIntervalCutsField.text.toIntOrNull()) }
        })
        precisionIntervalIntervalPanel.add(precisionIntervalIntervalCutsField, GridBagConstraints(1, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        precisionIntervalIntervalPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        precisionIntervalTabbedPane.add(precisionIntervalIntervalPanel, "Interval")

        precisionIntervalPrecisionPanel.add(precisionIntervalPrecisionPrecisionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        precisionIntervalPrecisionPrecisionField.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(precision = precisionIntervalPrecisionPrecisionField.text.toDoubleOrNull()) }
        })
        precisionIntervalPrecisionPanel.add(precisionIntervalPrecisionPrecisionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        precisionIntervalPrecisionPanel.add(precisionIntervalPrecisionIterationsLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        precisionIntervalPrecisionIterationsField.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(iterations = precisionIntervalPrecisionIterationsField.text.toIntOrNull()) }
        })
        precisionIntervalPrecisionPanel.add(precisionIntervalPrecisionIterationsField, GridBagConstraints(1, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        precisionIntervalPrecisionPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        precisionIntervalTabbedPane.add(precisionIntervalPrecisionPanel, "Precision")
        _contentPane.add(precisionIntervalTabbedPane, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeEquationPanel.add(modeEquationEquationLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeEquationColorButton.preferredSize = Dimension(20, 20)
        modeEquationColorButton.minimumSize = Dimension(20, 20)
        modeEquationColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose equation color", store.get().equationColor)

            if (newColor != null) {
                store.mutateIfOther { store -> store.copy(equationColor = newColor) }
            }
        }
        modeEquationPanel.add(modeEquationColorButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        modeEquationEquationArea.document.addDocumentListener(documentAdapter {
            store.mutateIfOther { store -> store.copy(equation = parse(modeEquationEquationArea.text)) }
        })
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
            store.mutateIfOther { store -> store.copy(equations = store.equations + listOf(InvalidEquation("") to randomColor())) }
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

        modeSystemEquationsTable.selectionModel.addListSelectionListener {
            selectedSystemEquation.setIfOther(modeSystemEquationsTable.selectedRow.let {
                when (it) {
                    -1 -> null
                    else -> it
                }
            })
        }
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

        modeSystemMethodButtons.toList().forEachIndexed { i, (_, button) ->
            modeSystemMethodPanel.add(button, GridBagConstraints(0, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, if (i == modeEquationMethodButtons.size - 1) 0 else 5, 0), 0, 0))
        }
        modeSystemMethodPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Method"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        modeSystemPanel.add(modeSystemMethodPanel, GridBagConstraints(0, 2, 3, 1, .0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        modeSystemPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        modeTabbedPane.add(modeSystemPanel, "Equations system")
        modeTabbedPane.addChangeListener {
            store.mutateIfOther { store -> store.copy(mode = Store.Mode.values()[modeTabbedPane.selectedIndex]) }
        }
        _contentPane.add(modeTabbedPane, GridBagConstraints(0, 1, 1, 1, .0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.VERTICAL, Insets(0, 0, 0, 5), 0, 0))

        plotRootsPlotPlot.border = BorderFactory.createLineBorder(null)
        plotRootsPlotPanel.add(plotRootsPlotPlot, GridBagConstraints(0, 0, 7, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        plotRootsPlotAbscissaVariableComboBox.addActionListener {
            store.mutateIfOther { store ->
                store.copy(plotAbscissaVariable = plotRootsPlotAbscissaVariableComboBoxModel.selectedItem)
            }
        }
        plotRootsPlotAbscissaVariablePanel.add(plotRootsPlotAbscissaVariableComboBox, GridBagConstraints(0, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotAbscissaVariablePanel.add(plotRootsPlotAbscissaVariableColonLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaVariablePanel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaFromLabel, GridBagConstraints(1, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotAbscissaBeginField.document.addDocumentListener(documentAdapter {
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
        })
        plotRootsPlotPanel.add(plotRootsPlotAbscissaBeginField, GridBagConstraints(2, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotAbscissaToLabel, GridBagConstraints(3, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotAbscissaEndField.document.addDocumentListener(documentAdapter {
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
        })
        plotRootsPlotPanel.add(plotRootsPlotAbscissaEndField, GridBagConstraints(4, 1, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotPanel.add(plotRootsPlotOrdinateLabel, GridBagConstraints(0, 2, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotOrdinateFromLabel, GridBagConstraints(1, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotOrdinateBeginField.document.addDocumentListener(documentAdapter {
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
        })
        plotRootsPlotPanel.add(plotRootsPlotOrdinateBeginField, GridBagConstraints(2, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotOrdinateToLabel, GridBagConstraints(3, 2, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        plotRootsPlotOrdinateEndField.document.addDocumentListener(documentAdapter {
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
        })
        plotRootsPlotPanel.add(plotRootsPlotOrdinateEndField, GridBagConstraints(4, 2, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotModeLabel, GridBagConstraints(0, 3, 1, 1, .0, .0, GridBagConstraints.EAST, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotRootsPlotModeButtons.toList().forEachIndexed { i, (_, button) ->
            plotRootsPlotModePanel.add(button, GridBagConstraints(i, 0, 1, 1, 1.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, if (i == plotRootsPlotModeButtons.size - 1) 0 else 5), 0, 0))
        }
        plotRootsPlotPanel.add(plotRootsPlotModePanel, GridBagConstraints(1, 3, 5, 1, .0, .0, GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        plotRootsPlotButtonsFitButton.addActionListener {
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
        plotRootsPlotButtonsPanel.add(plotRootsPlotButtonsFitButton, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        plotRootsPlotButtonsSliceButton.addActionListener {
            plotRootsPlotButtonsSliceWindow.isVisible = !plotRootsPlotButtonsSliceWindow.isVisible
        }

        plotRootsPlotButtonsSliceWindow.setLocationRelativeTo(this)
        plotRootsPlotButtonsPanel.add(plotRootsPlotButtonsSliceButton, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        plotRootsPlotPanel.add(plotRootsPlotButtonsPanel, GridBagConstraints(5, 1, 1, 3, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
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

        onStoreChange(store)
        store.onChange.listeners.addWithoutValue(this::onStoreChange)
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeLater {

            // mode
            modeTabbedPane.selectedIndex = store.mode.ordinal

            // start
            precisionIntervalIntervalStartField.background = when (store.begin) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalIntervalStartField.text.toDoubleOrNull() != store.begin) {
                precisionIntervalIntervalStartField.text = store.begin?.toString() ?: ""
            }

            // end
            precisionIntervalIntervalEndField.background = when (store.end) {
                null -> INVALID_VALUE_COLOR
                else -> defaultTextFieldBackgroundColor
            }

            if (precisionIntervalIntervalEndField.text.toDoubleOrNull() != store.end) {
                precisionIntervalIntervalEndField.text = store.end?.toString() ?: ""
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
                precisionIntervalPrecisionPrecisionField.text = store.precision?.toString() ?: ""
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
            modeEquationColorButton.icon = ColorIconFactory.getIcon(store.equationColor)

            // equation
            val equationValid = store.equation.toString().trim() == "" || store.variables.isNotEmpty()

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
            plotRootsPlotAbscissaVariableComboBox.selectedItem = store.plotAbscissaVariable

            // plotAbscissaBegin
            if (plotRootsPlotAbscissaBeginField.text.toDoubleOrNull() != store.plotAbscissaBegin) {
                plotRootsPlotAbscissaBeginField.text = store.plotAbscissaBegin.toString()
            }

            // plotAbscissaEnd
            if (plotRootsPlotAbscissaEndField.text.toDoubleOrNull() != store.plotAbscissaEnd) {
                plotRootsPlotAbscissaEndField.text = store.plotAbscissaEnd.toString()
            }

            // plotOrdinateBegin
            if (plotRootsPlotOrdinateBeginField.text.toDoubleOrNull() != store.plotOrdinateBegin) {
                plotRootsPlotOrdinateBeginField.text = store.plotOrdinateBegin.toString()
            }

            // plotOrdinateEnd
            if (plotRootsPlotOrdinateEndField.text.toDoubleOrNull() != store.plotOrdinateEnd) {
                plotRootsPlotOrdinateEndField.text = store.plotOrdinateEnd.toString()
            }

            // plotMode
            plotRootsPlotModeButtons[store.plotMode]?.isSelected = true

            println("Store changed: $store")
        }
    }

    override fun dispose() {
        super.dispose()

        plotRootsPlotButtonsSliceWindow.dispose()
    }
}
