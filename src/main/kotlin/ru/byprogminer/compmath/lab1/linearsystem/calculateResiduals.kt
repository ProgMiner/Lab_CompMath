package ru.byprogminer.compmath.lab1.linearsystem

import ru.byprogminer.compmath.lab1.utils.Fraction

/**
 * Calculates residuals of the system and the found roots
 *
 * @param roots Vector of roots of the specified system
 *
 * @return vector of residuals of the specified roots for the specified linear system
 */
fun LinearSystem.calculateResiduals(roots: Array<Fraction>): Array<Fraction> =
        Array(A.rows) { row -> roots
                .mapIndexed { column, x -> x * A.row(row)[column] }
                .reduce { a, b -> a + b } - b[row]
        }
