package ru.byprogminer.compmath.lab4.gui.util

import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

abstract class ListDataAdapter: ListDataListener {

    override fun contentsChanged(e: ListDataEvent) = update(e)
    override fun intervalAdded(e: ListDataEvent) = update(e)
    override fun intervalRemoved(e: ListDataEvent) = update(e)

    protected abstract fun update(e: ListDataEvent)
}
