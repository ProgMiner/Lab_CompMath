package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.gui.util.documentAdapter
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class OffsetWindow(store: ReactiveHolder<Store>): JFrame("Offset") {

    private val _contentPane = JPanel(GridBagLayout())
    private val _contentScrollPane = JScrollPane(_contentPane)
    private val placeholderLabel = JLabel("There isn't variables")
    private val variablesComponents = mutableMapOf<String, VariableComponents>()
    private val variablesButtonGroup = ButtonGroup()

    init {
        _contentPane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        _contentScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        _contentScrollPane.preferredSize = Dimension(420, 160)
        contentPane = _contentScrollPane
        pack()

        minimumSize = size

        addPlaceholder()
        onStoreChange(store.get(), store)
        store.onChange.listeners.add(this::onStoreChange)
    }

    private fun onStoreChange(oldStore: Store, storeHolder: ReactiveHolder<Store>) {
        SwingUtilities.invokeLater {
            val store = storeHolder.get()

            val vars = store.variables
            if (vars != oldStore.variables) {
                variablesComponents.filterKeys { v -> !vars.contains(v) }.forEach { (v, c) ->
                    variablesComponents.remove(v, c)
                    removeVariableComponents(c)
                }

                if (vars.isEmpty()) {
                    addPlaceholder()
                    return@invokeLater
                }

                removePlaceholder()
                variablesComponents.putAll(vars.filterNot { v -> variablesComponents.containsKey(v) }
                        .map { v -> v to makeVariableComponents(v, storeHolder) }.toMap())

                variablesComponents.toList().forEachIndexed { i, (_, c) ->
                    addVariableComponents(c, i)
                }
            }

            variablesComponents.forEach { (v, c) ->
                if (v == store.plotMainVariable) {
                    c.button.isSelected = true
                }

                if (store.begin != null && store.end != null) {
                    c.slider.value = calcOffsetToSlider(store.begin, store.end, store.plotOffset.getValue(v))
                    c.slider.toolTipText = ""
                } else {
                    c.slider.isEnabled = false
                    c.slider.toolTipText = "Interval isn't specified"
                }

                if (c.field.text.toDoubleOrNull() != store.plotOffset.getValue(v)) {
                    c.field.text = store.plotOffset.getValue(v).toString()
                }
            }
        }
    }

    private fun makeVariableComponents(variable: String, storeHolder: ReactiveHolder<Store>): VariableComponents {
        val button = JRadioButton(variable)

        button.addActionListener {
            storeHolder.mutate { store -> store.copy(plotMainVariable = variable) }
        }
        variablesButtonGroup.add(button)

        val slider = JSlider(0, 1000)
        slider.addChangeListener {
            val store = storeHolder.get()

            if (store.begin != null && store.end != null) {
                val offset = calcOffsetToSlider(store.begin, store.end, store.plotOffset.getValue(variable))

                if (offset != slider.value) {
                    storeHolder.mutate { st ->
                        st.copy(plotOffset = st.plotOffset + mapOf(variable to calcOffsetFromSlider(store.begin, store.end, slider.value)))
                    }
                }
            }
        }

        val field = JTextField(8)
        field.document.addDocumentListener(documentAdapter {
            val offset = field.text.toDoubleOrNull()

            if (offset != null) {
                storeHolder.mutate {store ->
                    store.copy(plotOffset = store.plotOffset + mapOf(variable to offset))
                }
            }
        })

        return VariableComponents(button, slider, field)
    }

    private fun addVariableComponents(variableComponents: VariableComponents, i: Int) {
        val bottomInset = if (i == variablesComponents.size - 1) 0 else 5

        _contentPane.add(variableComponents.button, GridBagConstraints(0, i, 1, 1, .0, .0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, Insets(0, 0, bottomInset, 5), 0, 0))
        _contentPane.add(variableComponents.slider, GridBagConstraints(1, i, 1, 1, 5.0, .0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Insets(0, 0, bottomInset, 5), 0, 0))
        _contentPane.add(variableComponents.field, GridBagConstraints(2, i, 1, 1, 1.0, .0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, Insets(0, 0, bottomInset, 0), 0, 0))
        _contentScrollPane.revalidate()
        _contentScrollPane.repaint()
    }

    private fun removeVariableComponents(variableComponents: VariableComponents) {
        _contentPane.remove(variableComponents.button)
        _contentPane.remove(variableComponents.slider)
        _contentPane.remove(variableComponents.field)
        _contentScrollPane.revalidate()
    }

    private fun addPlaceholder() {
        _contentPane.add(placeholderLabel, GridBagConstraints(0, 0, 1, 1, .0, .0, GridBagConstraints.CENTER, GridBagConstraints.NONE, Insets(0, 0, 0, 0), 0, 0))
        _contentScrollPane.revalidate()
    }

    private fun removePlaceholder() {
        _contentPane.remove(placeholderLabel)
        _contentScrollPane.revalidate()
    }

    private fun calcOffsetFromSlider(begin: Double, end: Double, offset: Int): Double =
            begin + (end - begin) * offset / 1000.0

    private fun calcOffsetToSlider(begin: Double, end: Double, offset: Double): Int =
            ((offset - begin) / (end - begin) * 1000).toInt()

    private data class VariableComponents(
            val button: JRadioButton,
            val slider: JSlider,
            val field: JTextField
    )
}
