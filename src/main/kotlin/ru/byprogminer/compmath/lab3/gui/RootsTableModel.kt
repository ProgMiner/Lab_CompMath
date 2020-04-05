package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class RootsTableModel(storeHolder: ReactiveHolder<Store>): AbstractTableModel() {

    private var columns = listOf("Iterations")
    private var rows = emptyList<List<Number>>()

    init {
        storeHolder.onChange.listeners.add { oldStore ->
            val store = storeHolder.get()

            val variables = store.variables.sorted().toList()

            if (columns != variables) {
                columns = variables + listOf("Iterations")

                SwingUtilities.invokeLater {
                    fireTableStructureChanged()
                }
            }

            if (store.roots != oldStore.roots) {
                val rows1 = store.roots.map { values -> columns.map { col -> (values.first[col] ?: values.second) as Number } }
                if (rows1 != this.rows) {
                    this.rows = rows1

                    SwingUtilities.invokeLater {
                        fireTableDataChanged()
                    }
                }
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
