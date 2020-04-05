package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.math.variables
import ru.byprogminer.compmath.lab3.util.EventManager
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class RootsTableModel(private val store: ReactiveHolder<Store>): AbstractTableModel() {

    private var columns = emptyList<String>()
    private var rows = emptyList<List<Double>>()

    private var previousRootsHandler: (Set<Map<String, Double>>, ReactiveHolder<Set<Map<String, Double>>>, EventManager<Set<Map<String, Double>>, ReactiveHolder<Set<Map<String, Double>>>>) -> Unit

    init {
        previousRootsHandler = store.get().roots.onChange.listeners.addWithoutValue(this::onRootsChange)

        store.onChange.listeners.add { oldStore, storeHolder ->
            val store = storeHolder.get()

            val variables = when (store.mode) {
                Store.Mode.EQUATION -> store.equation.variables
                Store.Mode.EQUATION_SYSTEM -> store.equations.map { (eq, _) -> eq }.variables
            }.sorted().toList()

            if (columns != variables) {
                columns = variables

                SwingUtilities.invokeLater {
                    fireTableStructureChanged()
                }
            }

            if (store.roots != oldStore.roots) {
                oldStore.roots.onChange.listeners.remove(previousRootsHandler)
                previousRootsHandler = store.roots.onChange.listeners.addWithoutValue(this::onRootsChange)
            }
        }
    }

    private fun onRootsChange(rootsHolder: ReactiveHolder<Set<Map<String, Double>>>) {
        val roots = rootsHolder.get()

        val rows = roots.map { values -> columns.map { col -> values[col] ?: throw IllegalArgumentException() } }

        if (rows != this.rows) {
            this.rows = rows

            SwingUtilities.invokeLater {
                fireTableDataChanged()
            }
        }
    }

    override fun getRowCount() = store.get().roots.get().size

    override fun getColumnCount() = columns.size

    override fun getColumnName(columnIndex: Int) = columns[columnIndex]

    override fun getColumnClass(columnIndex: Int) = Double::class.java

    override fun getValueAt(rowIndex: Int, columnIndex: Int) = rows[rowIndex][columnIndex]

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) =
        throw UnsupportedOperationException()

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = false
}
