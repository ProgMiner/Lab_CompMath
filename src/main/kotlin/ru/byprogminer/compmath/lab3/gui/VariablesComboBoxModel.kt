package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel
import javax.swing.SwingUtilities

class VariablesComboBoxModel(store: ReactiveHolder<Store>): AbstractListModel<String>(), ComboBoxModel<String> {

    private var variables = emptyList<String>()
    private var selected: String? = null

    init {
        store.onChange.listeners.addWithoutValue { storeHolder ->
            val st = storeHolder.get()

            val vars = st.variables.sorted()
            if (vars != variables) {
                SwingUtilities.invokeLater {
                    variables = vars
                    selected = vars.min()

                    fireContentsChanged(this, 0, vars.size - 1)
                }
            }
        }
    }

    override fun getSize() = variables.size

    override fun getElementAt(index: Int) = variables.getOrNull(index)

    override fun getSelectedItem() = selected

    override fun setSelectedItem(item: Any?) {
        if (item is String?) {
            val index = variables.indexOf(item)

            if (index >= 0) {
                selected = item

                fireContentsChanged(this, index, index)
            }
        }
    }
}
