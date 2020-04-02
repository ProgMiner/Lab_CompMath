package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.gui.MainWindow
import ru.byprogminer.compmath.lab3.gui.util.randomColor
import ru.byprogminer.compmath.lab3.util.EventManager
import ru.byprogminer.compmath.lab3.util.reactiveHolder
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities


const val APP_NAME = "Nonlinear equation solver"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val store = reactiveHolder(Store(
            Store.Mode.EQUATION,
            0.001,
            10000,
            null,
            randomColor(),
            BisectionMethod,
            emptyList(),
            NewtonsMethod
    ))

    SwingUtilities.invokeLater {
        val mainWindow = MainWindow(store)

        mainWindow.addWindowListener(object: WindowAdapter() {

            override fun windowClosing(e: WindowEvent) {
                EventManager.DEFAULT_EXECUTOR_SERVICE.shutdown()
                mainWindow.dispose()
            }
        })

        mainWindow.setLocationRelativeTo(null)
        mainWindow.isVisible = true
    }
}
