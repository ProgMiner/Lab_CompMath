package ru.byprogminer.compmath.lab5.util

interface ReactiveHolder<T>: MutableHolder<T> {

    val onChange: EventManager<T, ReactiveHolder<T>>

    fun setIfOther(value: T): T
    fun mutateIfOther(map: (T) -> T): T
}
