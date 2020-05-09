package ru.byprogminer.compmath.lab5

import ru.byprogminer.compmath.lab4.expression.InvalidExpression
import ru.byprogminer.compmath.lab4.math.LagrangeMethod
import ru.byprogminer.compmath.lab5.gui.MainWindow
import ru.byprogminer.compmath.lab5.gui.util.randomColor
import ru.byprogminer.compmath.lab5.math.RungeKuttaMethod
import ru.byprogminer.compmath.lab5.util.EventManager
import ru.byprogminer.compmath.lab5.util.reactiveHolder
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

const val APP_NAME = "Adams method function differentiator"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val store = reactiveHolder(Store(
            InvalidExpression(""),
            .0,
            .0,
            1.0,

            0.1,
            4,

            null,
            InvalidExpression(""),
            randomColor(),

            -10.0,
            10.0,
            -10.0,
            10.0
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

        if (
                st.expression != oldStore.expression ||
                st.startX != oldStore.startX ||
                st.startY != oldStore.startY ||
                st.endX != oldStore.endX ||
                st.precision != oldStore.precision ||
                st.order != oldStore.order
        ) {
            storeHolder.mutateIfOther { s ->
                s.copy(
                        derivativePoints = emptyMap(),
                        derivativeInterpolation = InvalidExpression("")
                )
            }

            thread {
                if (
                        !st.expressionValid ||
                        st.startX == null || st.startY == null ||
                        st.endX == null || st.precision == null
                ) {
                    return@thread
                }

                val points: Map<Double, Double> = RungeKuttaMethod.solve(
                        st.expression,
                        st.startX,
                        st.startY,
                        st.endX,
                        st.precision,
                        Store.ABSCISSA_VARIABLE,
                        Store.ORDINATE_VARIABLE
                )

                val interpolation = LagrangeMethod.interpolate(points, Store.ABSCISSA_VARIABLE)
                storeHolder.mutateIfOther { s -> s.copy(
                        derivativePoints = points,
                        derivativeInterpolation = interpolation
                ) }
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
