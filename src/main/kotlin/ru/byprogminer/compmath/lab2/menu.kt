package ru.byprogminer.compmath.lab2

import java.io.BufferedReader
import java.io.PrintStream

data class MenuItem(val text: String) {

    companion object {

        val SEPARATOR = MenuItem()
    }

    private constructor(): this("")

    override fun toString() = text
}

fun menu(out: PrintStream, `in`: BufferedReader, items: List<MenuItem>, prompt: String = "> "): Int? {
    var i = 1

    for (item in items) {
        if (item === MenuItem.SEPARATOR) {
            out.println()
        } else {
            out.println("${i++}. $item")
        }
    }

    var input: Int?
    while (true) {
        out.print(prompt)
        out.flush()

        input = (`in`.readLine() ?: return null).toIntOrNull()

        when {
            input == null -> out.println("Please, enter integer.")
            input < 1 || input >= i -> out.println("Please, enter one of listed item numbers.")
            else -> return input - 1
        }
    }
}

fun menu(items: List<MenuItem>, prompt: String = "> ") =
        menu(System.out, System.`in`.bufferedReader(), items, prompt)
