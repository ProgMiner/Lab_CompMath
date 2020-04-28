package ru.byprogminer.compmath.lab4

import ru.byprogminer.compmath.lab4.expression.InvalidExpression
import ru.byprogminer.compmath.lab4.gui.MainWindow
import ru.byprogminer.compmath.lab4.gui.util.randomColor
import ru.byprogminer.compmath.lab4.math.LagrangeMethod
import ru.byprogminer.compmath.lab4.util.EventManager
import ru.byprogminer.compmath.lab4.util.reactiveHolder
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

const val APP_NAME = "Lagrange method function interpolator"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val exprColor = randomColor()

    @Suppress("RemoveExplicitTypeArguments") val store = reactiveHolder(Store(
            InvalidExpression(""),
            exprColor,
            null,

            InvalidExpression(""),
            Color(255 - exprColor.red, 255 - exprColor.green, 255 - exprColor.blue),
            emptyList(),
            null,

            emptyList(),

            null,
            -100.0,
            100.0,
            -100.0,
            100.0
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

        if (st.function != oldStore.function) {
            val vars = st.variables

            storeHolder.mutateIfOther { store ->
                store.copy(
                        plotAbscissaVariable = when (store.plotAbscissaVariable) {
                            in vars -> store.plotAbscissaVariable
                            else -> vars.min()
                        }
                )
            }
        }

        if (st.function != oldStore.function || st.interpolationPoints != oldStore.interpolationPoints) {
            storeHolder.mutateIfOther { s ->
                s.copy(interpolation = InvalidExpression(""))
            }

            thread {
                if (!st.function.isValid || st.function.variables.size != 1) {
                    return@thread
                }

                val interpolation = LagrangeMethod.interpolate(st.function, st.interpolationPoints.toSet())
                storeHolder.mutateIfOther { s ->
                    s.copy(interpolation = interpolation)
                }
            }
        }

        if (st.function != oldStore.function || st.valuePoints != oldStore.valuePoints) {
            storeHolder.mutateIfOther { s ->
                s.copy(functionValues = null)
            }

            thread {
                if (!st.function.isValid) {
                    return@thread
                }

                val variable = st.function.variables.first()
                val values = st.valuePoints.map { point ->
                    point to st.function.evaluate(mapOf(variable to point))
                }.toMap()

                storeHolder.mutateIfOther { s ->
                    s.copy(functionValues = values)
                }
            }
        }

        if (st.interpolation != oldStore.interpolation || st.valuePoints != oldStore.valuePoints) {
            storeHolder.mutateIfOther { s ->
                s.copy(interpolationValues = null)
            }

            thread {
                if (!st.interpolation.isValid) {
                    return@thread
                }

                val variable = st.function.variables.first()
                val values = st.valuePoints.map { point ->
                    point to st.interpolation.evaluate(mapOf(variable to point))
                }.toMap()

                storeHolder.mutateIfOther { s ->
                    s.copy(interpolationValues = values)
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
