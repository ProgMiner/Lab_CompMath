package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.gui.util.documentAdapter
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import ru.byprogminer.compmath.lab3.util.toPlainString
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import kotlin.concurrent.thread

class ApproximationWindow(store: ReactiveHolder<Store>): JFrame("Approximation") {

    private val _contentPane = JPanel(GridBagLayout())
    private val _contentScrollPane = JScrollPane(_contentPane)
    private val placeholderLabel = JLabel("There isn't variables")
    private val variablesComponents = mutableMapOf<String, VariableComponents>()

    private var onStoreChangeRun = false

    init {
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        _contentScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        _contentScrollPane.preferredSize = Dimension(240, 240)
        contentPane = _contentScrollPane
        pack()

        minimumSize = size

        addPlaceholder()
        thread { onStoreChange(store.get(), store) }
        store.onChange.listeners.add(this::onStoreChange)
    }

    private fun onStoreChange(oldStore: Store, storeHolder: ReactiveHolder<Store>) {
        val store = storeHolder.get()

        SwingUtilities.invokeAndWait {
            onStoreChangeRun = true

            val vars = store.variables
            if (vars != oldStore.variables) {
                variablesComponents.filterKeys { v -> !vars.contains(v) }.forEach { (v, c) ->
                    variablesComponents.remove(v, c)
                    removeVariableComponents(c)
                }

                if (vars.isEmpty()) {
                    addPlaceholder()
                    return@invokeAndWait
                }

                removePlaceholder()
                variablesComponents.putAll(vars.filterNot { v -> variablesComponents.containsKey(v) }
                        .map { v -> v to makeVariableComponents(v, storeHolder) }.toMap())

                variablesComponents.toList().sortedBy { (v, _) -> v }.forEachIndexed { i, (_, c) ->
                    addVariableComponents(c, i)
                }
            }

            variablesComponents.forEach { (v, c) ->
                if (c.field.text.toDoubleOrNull() != store.startValues.getValue(v)) {
                    c.field.text = store.startValues.getValue(v).toPlainString()
                }
            }
        }

        SwingUtilities.invokeLater {
            onStoreChangeRun = false
        }
    }

    private fun makeVariableComponents(variable: String, storeHolder: ReactiveHolder<Store>): VariableComponents {
        val label = JLabel(variable)

        val field = JTextField(8)
        field.document.addDocumentListener(documentAdapter { manualChange {
            val offset = field.text.toDoubleOrNull()

            if (offset != null) {
                storeHolder.mutateIfOther {store ->
                    store.copy(startValues = store.startValues + mapOf(variable to offset))
                }
            }
        } })

        return VariableComponents(label, field)
    }

    private fun addVariableComponents(variableComponents: VariableComponents, i: Int) {
        val bottomInset = if (i == variablesComponents.size - 1) 0 else 5

        _contentPane.add(variableComponents.label, GridBagConstraints(0, i, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, bottomInset, 5), 0, 0))
        _contentPane.add(variableComponents.field, GridBagConstraints(1, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, bottomInset, 0), 0, 0))
        _contentScrollPane.revalidate()
    }

    private fun removeVariableComponents(variableComponents: VariableComponents) {
        _contentPane.remove(variableComponents.label)
        _contentPane.remove(variableComponents.field)

        _contentPane.repaint()
        _contentScrollPane.revalidate()
    }

    private fun addPlaceholder() {
        _contentPane.add(placeholderLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        _contentScrollPane.revalidate()
    }

    private fun removePlaceholder() {
        _contentPane.remove(placeholderLabel)

        _contentPane.repaint()
        _contentScrollPane.revalidate()
    }

    private inline fun manualChange(block: () -> Unit) {
        if (!onStoreChangeRun) {
            block()
        }
    }

    private data class VariableComponents(
            val label: JLabel,
            val field: JTextField
    )
}
