package ru.byprogminer.compmath.lab3.gui.util

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

abstract class DocumentAdapter: DocumentListener {

    override fun changedUpdate(e: DocumentEvent) = update(e)
    override fun insertUpdate(e: DocumentEvent) = update(e)
    override fun removeUpdate(e: DocumentEvent) = update(e)

    protected abstract fun update(e: DocumentEvent)
}
