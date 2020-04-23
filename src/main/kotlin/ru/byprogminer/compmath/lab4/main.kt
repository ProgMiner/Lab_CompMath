package ru.byprogminer.compmath.lab4

import ru.byprogminer.compmath.lab1.utils.Fraction
import ru.byprogminer.compmath.lab4.equation.Expression
import ru.byprogminer.compmath.lab4.equation.InvalidExpression
import ru.byprogminer.compmath.lab4.gui.MainWindow
import ru.byprogminer.compmath.lab4.gui.util.randomColor
import ru.byprogminer.compmath.lab4.util.EventManager
import ru.byprogminer.compmath.lab4.util.reactiveHolder
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

const val APP_NAME = "Nonlinear equation solver"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val exprColor = randomColor()

    @Suppress("RemoveExplicitTypeArguments") val store = reactiveHolder(Store(
            InvalidExpression(""),
            exprColor,

            InvalidExpression(""),
            Color(255 - exprColor.red, 255 - exprColor.green, 255 - exprColor.blue),
            emptyList(),

            emptyList(),
            null,

            null,
            Fraction(-100),
            Fraction(100),
            Fraction(-100),
            Fraction(100)
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

        if (st.expression != oldStore.expression) {
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

        if (st.expression != oldStore.expression || st.interpolationPoints != oldStore.interpolationPoints) {
            storeHolder.mutateIfOther { s -> s.copy(
                    interpolationPolynomial = InvalidExpression(""),
                    pointValues = null
            ) }

            thread {
                try {
                    if (st.expression.variables.size != 1) {
                        return@thread
                    }
                } catch (e: UnsupportedOperationException) {
                    return@thread
                }

                val interpolation = TODO() as Expression
                storeHolder.mutateIfOther { s ->
                    val pointValues = s.points.map { point ->
                        point to (st.expression.evaluate(mapOf(st.expression.variables.first() to point)) to
                                interpolation.evaluate(mapOf(st.expression.variables.first() to point)))
                    }.toMap()

                    s.copy(
                            interpolationPolynomial = interpolation,
                            pointValues = pointValues
                    )
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
