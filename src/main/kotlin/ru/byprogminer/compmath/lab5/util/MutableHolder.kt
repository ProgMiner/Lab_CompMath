package ru.byprogminer.compmath.lab5.util

interface MutableHolder<T>: Holder<T> {

    fun set(value: T): T
    fun mutate(map: (T) -> T): T
}
