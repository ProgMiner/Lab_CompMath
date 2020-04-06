package ru.byprogminer.compmath.lab3.gui

import ru.byprogminer.compmath.lab3.Store
import ru.byprogminer.compmath.lab3.equation.Equation
import ru.byprogminer.compmath.lab3.gui.util.ColorIconFactory
import ru.byprogminer.compmath.lab3.parser.parse
import ru.byprogminer.compmath.lab3.util.ReactiveHolder
import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class EquationsTableModel(private val store: ReactiveHolder<Store>): AbstractTableModel() {

    private var rows = listOf<Pair<Equation, Color>>()

    init {
        store.onChange.listeners.add { old, holder ->
            val equations = holder.get().equations

            if (equations != old.equations) {
                SwingUtilities.invokeLater {
                    rows = equations

                    fireTableDataChanged()
                }
            }
        }
    }

    override fun getRowCount() = rows.size

    override fun getColumnCount() = 2

    override fun getColumnName(column: Int) = when (column) {
        0 -> "Equation"
        1 -> "Color"

        else -> throw IllegalArgumentException()
    }

    override fun getColumnClass(column: Int) = when (column) {
        0 -> Any::class.java
        1 -> ImageIcon::class.java

        else -> throw IllegalArgumentException()
    }

    override fun isCellEditable(row: Int, column: Int) = true

    override fun getValueAt(row: Int, column: Int): Any = rows[row].let { when (column) {
        0 -> it.first
        1 -> ColorIconFactory.getIcon(it.second)

        else -> IllegalArgumentException()
    } }

    override fun setValueAt(value: Any?, row: Int, column: Int) {
        when (column) {
            0 -> if (value is String) {
                store.mutateIfOther { store ->
                    store.copy(equations = store.equations.mapIndexed { i, pair -> when (i) {
                        row -> pair.copy(first = parse(value))
                        else -> pair
                    } })
                }
            }

            1 -> if (value is Color) {
                store.mutateIfOther { store ->
                    store.copy(equations = store.equations.mapIndexed { i, pair -> when (i) {
                        row -> pair.copy(second = value)
                        else -> pair
                    } })
                }
            }
        }
    }
}
