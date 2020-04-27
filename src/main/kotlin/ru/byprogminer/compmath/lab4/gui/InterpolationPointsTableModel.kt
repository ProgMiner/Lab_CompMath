package ru.byprogminer.compmath.lab4.gui

import ru.byprogminer.compmath.lab4.Store
import ru.byprogminer.compmath.lab4.util.ReactiveHolder
import ru.byprogminer.compmath.lab4.util.toPlainString
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class InterpolationPointsTableModel(private val storeHolder: ReactiveHolder<Store>): AbstractTableModel() {

    private var rows = emptyList<String>()

    init {
        storeHolder.onChange.listeners.add { oldStore ->
            val store = storeHolder.get()

            if (store.interpolationPoints != oldStore.interpolationPoints) {
                val rows = store.interpolationPoints.map { it.toPlainString() }

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

    override fun getColumnCount() = 1
    override fun getColumnName(columnIndex: Int) = when (columnIndex) {
        0 -> "x"

        else -> throw IllegalArgumentException()
    }

    override fun getColumnClass(columnIndex: Int) = String::class.java

    override fun getValueAt(rowIndex: Int, columnIndex: Int) = rows.getOrNull(rowIndex)
    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (columnIndex == 0 && value is String) {
            val newValue = value.toDoubleOrNull()

            if (newValue != null) {
                storeHolder.mutateIfOther { store ->
                    store.copy(interpolationPoints = store.interpolationPoints.mapIndexed { i, oldValue -> when (i) {
                        rowIndex -> newValue
                        else -> oldValue
                    } })
                }
            }
        }
    }
    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = true
}
