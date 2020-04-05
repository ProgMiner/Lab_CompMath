package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.equation.InvalidEquation
import ru.byprogminer.compmath.lab3.gui.MainWindow
import ru.byprogminer.compmath.lab3.gui.util.randomColor
import ru.byprogminer.compmath.lab3.math.BisectionMethod
import ru.byprogminer.compmath.lab3.math.Interval
import ru.byprogminer.compmath.lab3.math.NewtonsMethod
import ru.byprogminer.compmath.lab3.math.Precision
import ru.byprogminer.compmath.lab3.util.EventManager
import ru.byprogminer.compmath.lab3.util.reactiveHolder
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

const val APP_NAME = "Nonlinear equation solver"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val store = reactiveHolder(Store(
            Store.Mode.EQUATION,
            -100.0,
            100.0,
            40,
            0.001,
            10000,
            InvalidEquation(""),
            randomColor(),
            BisectionMethod,
            emptyList(),
            NewtonsMethod,
            reactiveHolder(emptySet())
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

        if (
                st.mode != oldStore.mode ||
                st.begin != oldStore.begin || st.end != oldStore.end || st.cuts != oldStore.cuts ||
                st.precision != oldStore.precision || st.iterations != oldStore.iterations ||
                st.equation != oldStore.equation || st.method != oldStore.method ||
                st.equations != oldStore.equations || st.systemMethod != oldStore.systemMethod
        ) {
            val roots = reactiveHolder<Set<Pair<Map<String, Double>, Int>>>(emptySet())

            if (st.begin != null && st.end != null && st.cuts != null && st.precision != null && st.iterations != null) {
                thread {
                    when (st.mode) {
                        Store.Mode.EQUATION -> try {
                            st.equation.variables
                        } catch (e: UnsupportedOperationException) {
                            return@thread
                        }

                        Store.Mode.EQUATION_SYSTEM -> if (st.equations.isEmpty()) {
                            return@thread
                        }
                    }

                    val precision = Precision(st.precision, st.iterations)
                    val interval = Interval(st.begin, st.end, st.cuts)

                    roots.set(when (st.mode) {
                        Store.Mode.EQUATION -> st.method.solve(st.equation, interval, precision)
                        Store.Mode.EQUATION_SYSTEM ->
                            st.systemMethod.solve(st.equations.map { (eq, _) -> eq }, interval, precision)
                    })
                }
            }

            storeHolder.mutate { s -> s.copy(roots = roots) }
        }
    }

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
