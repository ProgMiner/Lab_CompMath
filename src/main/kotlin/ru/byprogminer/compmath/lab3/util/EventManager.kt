package ru.byprogminer.compmath.lab3.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EventManager<V, T>(private val target: T, private val executorService: ExecutorService = DEFAULT_EXECUTOR_SERVICE) {

    companion object {

        val DEFAULT_EXECUTOR_SERVICE: ExecutorService = Executors.newSingleThreadExecutor()
    }

    private val listenersSet by lazy { mutableSetOf<(V, T, EventManager<V, T>) -> Unit>() }

    val listeners by lazy { object : Listeners<V, T> {

        override fun add(listener: (V, T, EventManager<V, T>) -> Unit) {
            listenersSet.add(listener)
        }

        override fun remove(listener: (V, T, EventManager<V, T>) -> Unit) {
            listenersSet.remove(listener)
        }

        override fun clear() = listenersSet.clear()
    } }

    fun fire(value: V) {
        listenersSet.forEach { executorService.submit { it(value, target, this) } }
    }

    interface Listeners<V, T> {

        fun add(listener: (V, T, EventManager<V, T>) -> Unit)
        fun remove(listener: (V, T, EventManager<V, T>) -> Unit)
        fun clear()

        fun add(listener: (V, T) -> Unit): (V, T, EventManager<V, T>) -> Unit =
            { value: V, target: T, _: EventManager<V, T> -> listener(value, target) }.also { add(it) }

        fun addWithoutValue(listener: (T) -> Unit): (V, T, EventManager<V, T>) -> Unit =
            { _: V, target: T, _: EventManager<V, T> -> listener(target) }.also { add(it) }

        fun add(listener: (V) -> Unit): (V, T, EventManager<V, T>) -> Unit =
            { value: V, _: T, _: EventManager<V, T> -> listener(value) }.also { add(it) }

        fun add(listener: () -> Unit): (V, T, EventManager<V, T>) -> Unit =
            { _: V, _: T, _: EventManager<V, T> -> listener() }.also { add(it) }
    }
}
