package ru.byprogminer.compmath.lab3.util

import java.util.concurrent.atomic.AtomicReference

fun<T> holder(value: T): Holder<T> = object : Holder<T> { override fun get() = value }

private open class SimpleMutableHolder<T>(value: T): MutableHolder<T> {

    private val atomic = AtomicReference(value)

    override fun get(): T = synchronized(atomic) { atomic.get() }

    override fun set(value: T): T = synchronized(atomic) { atomic.getAndSet(value) }
    override fun mutate(map: (T) -> T): T =
        synchronized(atomic) {
            val old = atomic.get()

            atomic.set(map(old))
            return@synchronized old
        }
}

fun<T> mutableHolder(value: T): MutableHolder<T> = SimpleMutableHolder(value)

fun<T> reactiveHolder(value: T): ReactiveHolder<T> = object : SimpleMutableHolder<T>(value), ReactiveHolder<T> {

    override val onChange = EventManager<T, ReactiveHolder<T>>(this)

    override fun set(value: T): T {
        val previous = super.set(value)

        onChange.fire(previous)
        return previous
    }

    override fun mutate(map: (T) -> T): T {
        val previous = super.mutate(map)

        onChange.fire(previous)
        return previous
    }
}
