package ru.byprogminer.compmath.lab1

import ru.byprogminer.compmath.lab1.linearsystem.*
import ru.byprogminer.compmath.lab1.utils.Matrix
import ru.byprogminer.compmath.lab1.utils.Fraction
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.NumberFormatException
import kotlin.random.Random
import kotlin.system.exitProcess

private inline fun<T> input(prompt: String = "", map: (String) -> T?): T? {
    var input: T? = null

    do {
        print(prompt)

        try {
            input = map(readLine() ?: return null)
        } catch (e: Exception) {
            println(e.message)
        }
    } while (input == null)

    return input
}

private fun input(prompt: String = "") = input(prompt) { it }

private fun mapInt(s: String) = s.toIntOrNull() ?: throw IllegalArgumentException("Please, enter integer.")

private fun mapNotNegativeInt(s: String) = (mapInt(s)).let { if (it < 0) {
    throw IllegalArgumentException("Please, enter not negative integer.")
} else { it } }

private fun mapFraction(s: String, m: String) = try {
    Fraction(s.trim())
} catch (e: NumberFormatException) {
    throw IllegalArgumentException(m)
}

private fun mapFraction(s: String) =
        mapFraction(s, "Please, enter valid floating-point number or fraction in format a/b.")

fun main() {
    loop@while (true) {
        println("Welcome to linear system solver! Select and enter one of action numbers to work:")

        val action = menu(listOf(
                MenuItem("Specify linear system handy"),
                MenuItem("Specify linear system from file"),
                MenuItem("Generate random linear system"),
                MenuItem("Quit")
        ))

        val system = when (action) {
            0 -> readFromStdin()
            1 -> readFromFile()
            2 -> generateRandomSystem()

            else -> break@loop
        }

        if (system != null) {
            calculate(system)
        }

        println()
    }
}

private fun readFromStdin(): LinearSystem? {
    val n = input("Count of equations: ", ::mapNotNegativeInt) ?: exitProcess(0)

    println("Enter lines of matrix in format: a_i1 a_i2 ... a_im | b_i")
    val lines = (0 until n).map { input("${it + 1}. ") { s -> s.split('|').let { row ->
        val b = row.getOrNull(1)?.let(::mapFraction)
                ?: throw IllegalArgumentException("Please, specify element of vector b for this equation.")

        val a = row[0].trim().split(Regex("\\s+")).map(String::trim).map(::mapFraction)

        return@let a to b
    } } ?: exitProcess(0) }

    if (lines.isEmpty()) {
        return null
    }

    val varsCount = lines[0].first.size
    if (lines.any { (a, _) -> a.size != varsCount }) {
        println("You specified system with different count of variables. Abort.")
        return null
    }

    val system = LinearSystem(Matrix(n, varsCount), Array(n) { lines[it].second })
    for (i in 0 until system.A.rows) {
        for (j in 0 until varsCount) {
            system.A.row(i)[j] = lines[i].first[j]
        }
    }

    return system
}

private fun readFromFile(): LinearSystem? {
    println("File must be in format:\n" +
            "a_11 a_12 ⋯ a_1m | b_1\n" +
            "a_21 a_22 ⋯ a_2m | b_2\n" +
            " ⋮     ⋮   ⋱  ⋮   |  ⋮\n" +
            "a_n1 a_n2 ⋯ a_nm | b_n\n" +
            "\n" +
            "Where a_ij is coefficient and b_i is free coefficient.")

    val name = input("Path to file: ") ?: exitProcess(0)
    val file = Paths.get(name)

    if (!Files.isRegularFile(file)) {
        println("File is not exists or not regular file. Abort.")
        return null
    }

    fun mapFraction(s: String) = mapFraction(s, "Parsing error at \"$s\".")
    val lines = try {
        Files.readAllLines(file).map { s -> s.split('|').let { row ->
            val b = row.getOrNull(1)?.let(::mapFraction)
                    ?: throw IllegalArgumentException("b_${row + 1} is not specified.")

            val a = row[0].trim().split(Regex("\\s+")).map(String::trim).map(::mapFraction)
            return@let a to b
        } }
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return null
    }

    val varsCount = lines[0].first.size
    if (lines.any { (a, _) -> a.size != varsCount }) {
        println("File has rows with different count of coefficients. Abort.")
        return null
    }

    val system = LinearSystem(Matrix(lines.size, varsCount), Array(lines.size) { lines[it].second })
    for (i in 0 until system.A.rows) {
        for (j in 0 until varsCount) {
            system.A.row(i)[j] = lines[i].first[j]
        }
    }

    return system
}

private fun generateRandomSystem(): LinearSystem? {
    val n = input("Count of variables: ", ::mapNotNegativeInt) ?: exitProcess(0)

    if (n == 0) {
        return null
    }

    val random = Random(System.currentTimeMillis())
    random.nextInt()
    random.nextInt()
    random.nextInt()

    fun nextRandom() = Fraction((random.nextInt(10000) * random.nextFloat()).toDouble())

    val linearSystem = LinearSystem(Matrix(n, n), Array(n) { nextRandom() })
    for (i in 0 until linearSystem.A.rows * linearSystem.A.cols) {
        linearSystem.A[i] = nextRandom()
    }

    return linearSystem
}

private fun calculate(system: LinearSystem) {
    println("Source matrix:")
    println(system)
    println()

    if (system.A.rows != system.A.cols) {
        println("Count of equations is not equals to count of variables. System does not have a single solution.")
        return
    }

    system.makeTriangle()
    println("Triangle matrix:")
    println(system)
    println()

    val det = system.A.calculateDeterminant()
    println("Determinant: $det.")
    println()

    if (det == Fraction.ZERO) {
        println("Matrix determinant is 0. System does not have a single solution.")
        return
    }

    println("Roots:")
    val roots = system.calculateRoots()
    println(roots.mapIndexed { i, x -> "x_$i = $x" }.joinToString("\n"))
    println()

    println("Residuals:")
    val residuals = system.calculateResiduals(roots)
    println(residuals.mapIndexed { i, r -> "r_$i = $r" }.joinToString("\n"))
    println()
}
