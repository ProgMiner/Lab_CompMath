package ru.byprogminer.compmath.lab3.util

interface ReactiveHolder<T>: MutableHolder<T> {

    val onChange: EventManager<T, ReactiveHolder<T>>
}
