package ru.byprogminer.compmath.lab5

import ru.byprogminer.compmath.lab4.expression.InvalidExpression
import ru.byprogminer.compmath.lab5.gui.MainWindow
import ru.byprogminer.compmath.lab5.gui.util.randomColor
import ru.byprogminer.compmath.lab5.util.EventManager
import ru.byprogminer.compmath.lab5.util.reactiveHolder
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities

const val APP_NAME = "Adams method function differentiator"
const val APP_VERSION = "1.0-SNAPSHOT"

fun main() {
    val store = reactiveHolder(Store(
            InvalidExpression(""),
            .0,
            .0,
            1.0,

            0.001,
            4,

            null,
            InvalidExpression(""),
            randomColor(),

            -100.0,
            100.0,
            -100.0,
            100.0
    ))

    store.onChange.listeners.add { oldStore, storeHolder ->
        val st = storeHolder.get()

//        if (st.function != oldStore.function || st.interpolationPoints != oldStore.interpolationPoints) {
//            storeHolder.mutateIfOther { s ->
//                s.copy(interpolation = InvalidExpression(""))
//            }
//
//            thread {
//                if (!st.function.isValid || st.function.variables.size != 1) {
//                    return@thread
//                }
//
//                val interpolation = LagrangeMethod.interpolate(st.function, st.interpolationPoints.toSet())
//                storeHolder.mutateIfOther { s ->
//                    s.copy(interpolation = interpolation)
//                }
//            }
//        }
//
//        if (st.function != oldStore.function || st.valuePoints != oldStore.valuePoints) {
//            storeHolder.mutateIfOther { s ->
//                s.copy(functionValues = null)
//            }
//
//            thread {
//                if (!st.function.isValid) {
//                    return@thread
//                }
//
//                val variable = st.function.variables.first()
//                val values = st.valuePoints.map { point ->
//                    point to st.function.evaluate(mapOf(variable to point))
//                }.toMap()
//
//                storeHolder.mutateIfOther { s ->
//                    s.copy(functionValues = values)
//                }
//            }
//        }
//
//        if (st.interpolation != oldStore.interpolation || st.valuePoints != oldStore.valuePoints) {
//            storeHolder.mutateIfOther { s ->
//                s.copy(interpolationValues = null)
//            }
//
//            thread {
//                if (!st.interpolation.isValid) {
//                    return@thread
//                }
//
//                val variable = st.function.variables.first()
//                val values = st.valuePoints.map { point ->
//                    point to st.interpolation.evaluate(mapOf(variable to point))
//                }.toMap()
//
//                storeHolder.mutateIfOther { s ->
//                    s.copy(interpolationValues = values)
//                }
//            }
//        }
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
