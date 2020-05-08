package ru.byprogminer.compmath.lab5.util

fun<V, T> eventManagerFor(valueType: Class<V>, target: T) = EventManager<V, T>(target)
