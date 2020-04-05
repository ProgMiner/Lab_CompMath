package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.math.variables
import ru.byprogminer.compmath.lab3.util.EventManager
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class RootsTableModel(private val store: ReactiveHolder<Store>): AbstractTableModel() {

    private var columns = emptyList<String>()
    private var rows = emptyList<List<Number>>()

    private var previousRootsHandler: (
            Set<Pair<Map<String, Double>, Int>>,
            ReactiveHolder<Set<Pair<Map<String, Double>, Int>>>,
            EventManager<Set<Pair<Map<String, Double>, Int>>, ReactiveHolder<Set<Pair<Map<String, Double>, Int>>>>
    ) -> Unit

    init {
        previousRootsHandler = store.get().roots.onChange.listeners.addWithoutValue(this::onRootsChange)

        store.onChange.listeners.add { oldStore, storeHolder ->
            val store = storeHolder.get()

            val variables = when (store.mode) {
                Store.Mode.EQUATION -> try {
                    store.equation.variables
                } catch (e: UnsupportedOperationException) {
                    emptySet<String>()
                }

                Store.Mode.EQUATION_SYSTEM -> store.equations.map { (eq, _) -> eq }.variables
            }.sorted().toList()

            if (columns != variables) {
                columns = variables + listOf("Iterations")

                SwingUtilities.invokeLater {
                    fireTableStructureChanged()
                }
            }

            if (store.roots != oldStore.roots) {
                oldStore.roots.onChange.listeners.remove(previousRootsHandler)
                previousRootsHandler = store.roots.onChange.listeners.addWithoutValue(this::onRootsChange)

                onRootsChange(store.roots)
            }
        }
    }

    private fun onRootsChange(rootsHolder: ReactiveHolder<Set<Pair<Map<String, Double>, Int>>>) {
        val roots = rootsHolder.get()

        val rows = roots.map { values -> columns.map { col -> (values.first[col] ?: values.second) as Number } }

        if (rows != this.rows) {
            this.rows = rows

            SwingUtilities.invokeLater {
                fireTableDataChanged()
            }
        }
    }

    override fun getRowCount() = rows.size

    override fun getColumnCount() = columns.size
    override fun getColumnName(columnIndex: Int) = columns[columnIndex]
    override fun getColumnClass(columnIndex: Int) = Number::class.java

    override fun getValueAt(rowIndex: Int, columnIndex: Int) = rows.getOrNull(rowIndex)?.getOrNull(columnIndex)
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {}
    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = false
}
