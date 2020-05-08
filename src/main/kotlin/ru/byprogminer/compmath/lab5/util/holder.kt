package ru.byprogminer.compmath.lab5.util

import java.util.concurrent.atomic.AtomicReference

fun<T> holder(value: T): Holder<T> = object : Holder<T> { override fun get() = value }

private open class SimpleMutableHolder<T>(value: T): MutableHolder<T> {

    protected val atomic = AtomicReference(value)

    override fun get(): T = atomic.get()

    override fun set(value: T): T = atomic.getAndSet(value)
    override fun mutate(map: (T) -> T): T = atomic.getAndUpdate(map)
}

fun<T> mutableHolder(value: T): MutableHolder<T> = SimpleMutableHolder(value)

fun<T> reactiveHolder(value: T): ReactiveHolder<T> = object : SimpleMutableHolder<T>(value), ReactiveHolder<T> {

    override val onChange = EventManager<T, ReactiveHolder<T>>(this)

    override fun set(value: T): T {
        val previous = super.set(value)

        onChange.fire(previous)
        return previous
    }

    override fun setIfOther(value: T): T {
        val previous = super.set(value)

        if (previous != value) {
            onChange.fire(previous)
        }

        return previous
    }

    override fun mutate(map: (T) -> T): T {
        val previous = super.mutate(map)

        onChange.fire(previous)
        return previous
    }

    protected fun getUpdateAndGet(map: (T) -> T): Pair<T, T> {
        var prev: T
        var next: T

        do {
            prev = atomic.get()
            next = map(prev)
        } while (!atomic.compareAndSet(prev, next))

        return prev to next
    }

    override fun mutateIfOther(map: (T) -> T): T {
        val (previous, current) = getUpdateAndGet(map)

        if (previous != current) {
            onChange.fire(previous)
        }

        return previous
    }
}
