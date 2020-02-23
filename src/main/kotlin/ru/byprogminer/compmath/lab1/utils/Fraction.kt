package ru.byprogminer.compmath.lab1.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

/**
 * Number in fractional form
 *
 * @author Eridan Domoratskiy
 */
class Fraction(numerator: BigInteger, denominator: BigInteger): Number(), Comparable<Fraction> {

    companion object {

        val ZERO = Fraction(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Fraction(BigInteger.ONE, BigInteger.ONE)

        fun valueOf(a: Long) = Fraction(BigInteger.valueOf(a), BigInteger.ONE)
        fun valueOf(a: BigInteger) = Fraction(a, BigInteger.ONE)
        fun valueOf(a: Double) = valueOf(BigDecimal.valueOf(a))
        fun valueOf(a: BigDecimal) = a.stripTrailingZeros().run {
            Fraction(a.unscaledValue(), BigInteger.TEN.pow(a.scale()))
        }
    }

    private val numerator: BigInteger
    private val denominator: BigInteger

    init {
        if (denominator < BigInteger.ZERO) {
            throw IllegalArgumentException("denominator cannot be negative")
        }

        val gcd = gcd(numerator, denominator)

        this.numerator = numerator / gcd
        this.denominator = denominator / gcd
    }

    operator fun plus(that: Fraction) = Fraction(
            this.numerator * that.denominator + that.numerator * this.denominator,
            this.denominator * that.denominator
    )

    operator fun minus(that: Fraction) = Fraction(
            this.numerator * that.denominator - that.numerator * this.denominator,
            this.denominator * that.denominator
    )

    operator fun times(that: Fraction) = Fraction(
            this.numerator * that.numerator,
            this.denominator * that.denominator
    )

    operator fun div(that: Fraction) = Fraction(
            this.numerator * that.denominator,
            this.denominator * that.numerator
    )

    override fun compareTo(other: Fraction): Int {
        val c = toDouble().compareTo(other.toDouble())

        return if (c == 0) {
            (this - other).numerator.toInt()
        } else {
            c
        }
    }

    override fun toLong(): Long = numerator.divide(denominator).toLong()
    override fun toInt(): Int = toLong().toInt()
    override fun toShort(): Short = toLong().toShort()
    override fun toChar(): Char = toLong().toChar()
    override fun toByte(): Byte = toLong().toByte()

    override fun toDouble(): Double = BigDecimal(numerator, MathContext.DECIMAL128)
            .divide(denominator.toBigDecimal()).toDouble()
    override fun toFloat(): Float = toDouble().toFloat()

    override fun toString(): String = if (denominator == BigInteger.ONE) {
        "$numerator"
    } else {
        "$numerator/$denominator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fraction
        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false
        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}
