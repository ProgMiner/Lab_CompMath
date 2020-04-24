package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class ValuesTableModel(storeHolder: ReactiveHolder<Store>): AbstractTableModel() {

    private var rows = emptyList<List<String>>()

    init {
        storeHolder.onChange.listeners.add { oldStore ->
            val store = storeHolder.get()

            if (store.points != oldStore.points || store.pointValues != oldStore.pointValues) {
                val rows = store.points.map { point -> listOf(point.toString(),
                        store.pointValues?.getValue(point)?.first?.toString() ?: "",
                        store.pointValues?.getValue(point)?.second?.toString() ?: "") }

                if (rows != this.rows) {
                    SwingUtilities.invokeLater {
                        this.rows = rows

                        fireTableDataChanged()
                    }
                }
            }
        }
    }

    override fun getRowCount() = rows.size

    override fun getColumnCount() = 3
    override fun getColumnName(columnIndex: Int) = when (columnIndex) {
        0 -> "x"
        1 -> "F(x)"
        2 -> "Ln(x)"

        else -> throw IllegalArgumentException()
    }

    override fun getColumnClass(columnIndex: Int) = String::class.java

    override fun getValueAt(rowIndex: Int, columnIndex: Int) = rows.getOrNull(rowIndex)?.getOrNull(columnIndex)
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {}
    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true
}
