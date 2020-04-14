package ru.byprogminer.compmath.lab3

import ru.byprogminer.compmath.lab3.equation.InvalidEquation
import ru.byprogminer.compmath.lab3.gui.MainWindow
import ru.byprogminer.compmath.lab3.gui.util.randomColor
import ru.byprogminer.compmath.lab3.math.*
import ru.byprogminer.compmath.lab3.util.EventManager
import ru.byprogminer.compmath.lab3.util.reactiveHolder
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

const val APP_NAME = "Nonlinear equation solver"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    @Suppress("RemoveExplicitTypeArguments") val store = reactiveHolder(Store(
            Store.Mode.EQUATION,

            -100.0,
            100.0,
            40,

            0.001,
            10000,

            InvalidEquation(""),
            randomColor(),
            BisectionMethod,

            emptyMap(),
            emptyList(),
            NewtonsMethod,

            null,

            null,
            -100.0,
            100.0,
            -100.0,
            100.0,
            Store.PlotMode.EQUATIONS,
            mapOf()
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

        if (st.mode != oldStore.mode || st.equation != oldStore.equation || st.equations != oldStore.equations) {
            val vars = st.variables

            storeHolder.mutateIfOther { store ->
                store.copy(
                        plotSlice = vars.map { v -> v to .0 }.toMap() + store.plotSlice,
                        plotAbscissaVariable = when (store.plotAbscissaVariable) {
                            in vars -> store.plotAbscissaVariable
                            else -> vars.min()
                        }
                )
            }
        }

        if (
                st.mode != oldStore.mode ||
                st.begin != oldStore.begin || st.end != oldStore.end || st.cuts != oldStore.cuts ||
                st.precision != oldStore.precision || st.iterations != oldStore.iterations ||
                st.equation != oldStore.equation || st.method != oldStore.method ||
                st.equations != oldStore.equations || st.systemMethod != oldStore.systemMethod
        ) {
            val startValues = st.startValues + st.equations
                    .map { (eq, _) -> eq }.variables
                    .map { v -> v to .0 }

            storeHolder.mutateIfOther { s -> s.copy(
                    startValues = startValues,
                    roots = null
            ) }

            if (st.begin != null && st.end != null && st.cuts != null && st.precision != null && st.iterations != null) {
                thread {
                    when (st.mode) {
                        Store.Mode.EQUATION -> try {
                            if (st.equation.variables.size > 1) {
                                return@thread
                            }
                        } catch (e: UnsupportedOperationException) {
                            return@thread
                        }

                        Store.Mode.EQUATION_SYSTEM -> if (st.equations.isEmpty()) {
                            return@thread
                        }
                    }

                    val precision = Precision(st.precision, st.iterations)
                    val interval = Interval(st.begin, st.end, st.cuts)

                    storeHolder.mutateIfOther { s -> s.copy(roots = when (st.mode) {
                        Store.Mode.EQUATION -> st.method.solve(st.equation, interval, precision)
                        Store.Mode.EQUATION_SYSTEM -> st.systemMethod
                                .solve(st.equations.map { (eq, _) -> eq }, startValues, precision)
                    }) }
                }
            }
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
