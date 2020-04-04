package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.APP_NAME
import ru.byprogminer.compmath.lab3.APP_VERSION
import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.equation.InvalidEquation
import ru.byprogminer.compmath.lab3.gui.util.*
import ru.byprogminer.compmath.lab3.method.BisectionMethod
import ru.byprogminer.compmath.lab3.method.NewtonsMethod
import ru.byprogminer.compmath.lab3.method.SimpleIterationsMethod
import ru.byprogminer.compmath.lab3.parser.parse
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import ru.byprogminer.compmath.lab3.util.reactiveHolder
import java.awt.*
import javax.swing.*

class MainWindow(store: ReactiveHolder<Store>): JFrame("$APP_NAME v$APP_VERSION") {

    companion object {

        private val ADD_ICON = createImageIcon("add.png").scale(16, 16)
        private val REMOVE_ICON = createImageIcon("remove.png").scale(16, 16)
    }

    private val _contentPane = JPanel(GridBagLayout())
    private val precisionPanel = JPanel(GridBagLayout())
    private val precisionPrecisionLabel = JLabel("Precision:")
    private val precisionPrecisionField = JTextField(5)
    private val precisionIterationsLabel = JLabel("Iterations:")
    private val precisionIterationsField = JTextField(5)
    private val modeTabbedPane = JTabbedPane()
    private val modeEquationPanel = JPanel(GridBagLayout())
    private val modeEquationEquationLabel = JLabel("Equation:")
    private val modeEquationColorButton = JButton()
    private val modeEquationEquationArea = JTextArea(3, 20)
    private val modeEquationEquationPane = JScrollPane(modeEquationEquationArea)
    private val modeEquationMethodPanel = JPanel(GridBagLayout())
    private val modeEquationMethodButtonGroup = ButtonGroup()
    private val modeEquationMethodButtons = mapOf(
            "Bisection method" to BisectionMethod,
            "Simple iterations method" to SimpleIterationsMethod
    ).map { (name, method) -> method to JRadioButton(name).also { it.addActionListener {
        store.mutate { store -> store.copy(method = method) }
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
        store.mutate { store -> store.copy(systemMethod = method) }
    } }.also(modeSystemMethodButtonGroup::add) }.toMap()

    private val plotPanel = JPanel() // TODO

    private val defaultTextAreaBackgroundColor = modeEquationEquationArea.background

    private val selectedSystemEquation = reactiveHolder<Int?>(null)

    init {
        precisionPanel.add(precisionPrecisionLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        precisionPrecisionField.document.addDocumentListener(documentAdapter {
            store.mutate { store -> store.copy(precision = precisionPrecisionField.text.toDoubleOrNull()) }
        })
        precisionPanel.add(precisionPrecisionField, GridBagConstraints(1, 0, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        precisionPanel.add(precisionIterationsLabel, GridBagConstraints(0, 1, 1, 1, .0, .0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, Insets(0, 0, 0, 5), 0, 0))

        precisionIterationsField.document.addDocumentListener(documentAdapter {
            store.mutate { store -> store.copy(iterations = precisionIterationsField.text.toIntOrNull()) }
        })
        precisionPanel.add(precisionIterationsField, GridBagConstraints(1, 1, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        precisionPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Precision"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        _contentPane.add(precisionPanel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeEquationPanel.add(modeEquationEquationLabel, GridBagConstraints(0, 0, 1, 1, 1.0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        modeEquationColorButton.preferredSize = Dimension(20, 20)
        modeEquationColorButton.addActionListener {
            val newColor = JColorChooser.showDialog(this@MainWindow, "Choose equation color", store.get().equationColor)

            if (newColor != null) {
                store.mutate { store -> store.copy(equationColor = newColor) }
            }
        }
        modeEquationPanel.add(modeEquationColorButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        modeEquationEquationArea.document.addDocumentListener(documentAdapter {
            store.mutate { store -> store.copy(equation = parse(modeEquationEquationArea.text)) }
        })
        modeEquationPanel.add(modeEquationEquationPane, GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        modeEquationMethodButtons.toList().forEachIndexed { i, (_, button) ->
            modeEquationMethodPanel.add(button, GridBagConstraints(0, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, if (i == modeEquationMethodButtons.size - 1) 0 else 5, 5), 0, 0))
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
        modeSystemAddEquationButton.addActionListener {
            store.mutate { store -> store.copy(equations = store.equations + listOf(InvalidEquation("") to randomColor())) }
        }
        modeSystemPanel.add(modeSystemAddEquationButton, GridBagConstraints(1, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))

        modeSystemRemoveEquationButton.isEnabled = false
        modeSystemRemoveEquationButton.preferredSize = Dimension(20, 20)
        modeSystemRemoveEquationButton.addActionListener {
            val index = selectedSystemEquation.get()

            store.mutate { store -> store.copy(equations = store.equations.filterIndexed { i, _ -> i != index }) }
        }
        selectedSystemEquation.onChange.listeners.addWithoutValue { holder ->
            modeSystemRemoveEquationButton.isEnabled = holder.get() != null
        }
        modeSystemPanel.add(modeSystemRemoveEquationButton, GridBagConstraints(2, 0, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        modeSystemEquationsTable.selectionModel.addListSelectionListener {
            selectedSystemEquation.set(modeSystemEquationsTable.selectedRow.let { when (it) {
                -1 -> null
                else -> it
            } })
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
        modeSystemPanel.add(modeSystemEquationsPane, GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))

        modeSystemMethodButtons.toList().forEachIndexed { i, (_, button) ->
            modeSystemMethodPanel.add(button, GridBagConstraints(0, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, if (i == modeEquationMethodButtons.size - 1) 0 else 5, 5), 0, 0))
        }
        modeSystemMethodPanel.border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Method"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        modeSystemPanel.add(modeSystemMethodPanel, GridBagConstraints(0, 2, 3, 1, .0, .0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        modeSystemPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        modeTabbedPane.add(modeSystemPanel, "Equations system")
        modeTabbedPane.addChangeListener {
            store.mutate { store -> store.copy(mode = Store.Mode.values()[modeTabbedPane.selectedIndex]) }
        }
        _contentPane.add(modeTabbedPane, GridBagConstraints(0, 1, 1, 1, .0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.VERTICAL, Insets(0, 0, 0, 5), 0, 0))

        plotPanel.border = BorderFactory.createTitledBorder("Plot")
        _contentPane.add(plotPanel, GridBagConstraints(1, 0, 0, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0))
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        contentPane = _contentPane
        pack()

        plotPanel.preferredSize = Dimension(plotPanel.height, plotPanel.height)
        pack()

        minimumSize = size

        onStoreChange(store)
        store.onChange.listeners.addWithoutValue(this::onStoreChange)
    }

    private fun onStoreChange(storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        modeTabbedPane.selectedIndex = store.mode.ordinal
        if (precisionPrecisionField.text.toDoubleOrNull() != store.precision) {
            precisionPrecisionField.text = store.precision.toString()
        }

        if (precisionIterationsField.text.toIntOrNull() != store.iterations) {
            precisionIterationsField.text = store.iterations.toString()
        }

        modeEquationColorButton.icon = ColorIconFactory.getIcon(store.equationColor)

        run {
            if (store.equation.toString().trim() != "") {
                try {
                    store.equation.variables
                } catch (e: Exception) {
                    modeEquationEquationArea.background = Color.RED
                    return@run
                }
            }

            modeEquationEquationArea.background = defaultTextAreaBackgroundColor
        }

        modeEquationMethodButtons[store.method]?.isSelected = true
        modeSystemMethodButtons[store.systemMethod]?.isSelected = true

        println("Store changed: $store")
    }
}
