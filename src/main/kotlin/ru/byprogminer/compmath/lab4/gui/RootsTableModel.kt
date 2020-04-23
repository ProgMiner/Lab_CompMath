package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class RootsTableModel(storeHolder: ReactiveHolder<Store>): AbstractTableModel() {

    private var columns = listOf("Iterations")
    private var rows = emptyList<List<String>>()

    init {
        storeHolder.onChange.listeners.add { oldStore ->
            val store = storeHolder.get()

            val variables = store.variables.sorted().toList()
            val columns = variables + listOf("Iterations")
            if (this.columns != columns) {
                SwingUtilities.invokeLater {
                    this.columns = columns

                    fireTableStructureChanged()
                }
            }

            if (store.roots != oldStore.roots) {
                val rows1 = (store.roots ?: emptySet())
                        .map { values -> columns.map { col ->
                            (values.first[col]?.toPlainString(400) ?: values.second.toString())
                        } }

                if (rows1 != this.rows) {
                    SwingUtilities.invokeLater {
                        this.rows = rows1

                        fireTableDataChanged()
                    }
                }
            }
        }
    }

    override fun getRowCount() = rows.size

    override fun getColumnCount() = columns.size
    override fun getColumnName(columnIndex: Int) = columns[columnIndex]
    override fun getColumnClass(columnIndex: Int) = String::class.java

    override fun getValueAt(rowIndex: Int, columnIndex: Int) = rows.getOrNull(rowIndex)?.getOrNull(columnIndex)
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {}
    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = columnIndex != columns.lastIndex
}
