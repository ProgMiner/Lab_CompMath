package ru.byprogminer.compmath.lab4.gui.util

import javax.swing.event.ListDataEvent

fun listDataAdapter(update: (e: ListDataEvent) -> Unit) = object: ListDataAdapter() {

    override fun update(e: ListDataEvent) = update(e)
}
