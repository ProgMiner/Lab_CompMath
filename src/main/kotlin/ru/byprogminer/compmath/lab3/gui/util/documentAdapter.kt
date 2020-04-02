package ru.byprogminer.compmath.lab3.gui.util

import javax.swing.event.DocumentEvent

fun documentAdapter(update: (e: DocumentEvent) -> Unit) = object: DocumentAdapter() {

    override fun update(e: DocumentEvent) = update(e)
}
