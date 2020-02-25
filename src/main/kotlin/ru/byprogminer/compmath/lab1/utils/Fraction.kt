package ru.byprogminer.compmath.lab1.utils

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.max

/**
 * Number in fractional form
 *
 * @author Eridan Domoratskiy
 */
class Fraction(numerator: BigInteger, denominator: BigInteger): Number(), Comparable<Fraction> {

    companion object {

        val ZERO = Fraction(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Fraction(BigInteger.ONE, BigInteger.ONE)
    }

    private val numerator: BigInteger
    private val denominator: BigInteger

    constructor(a: BigInteger): this(a, BigInteger.ONE)
    constructor(a: Long): this(BigInteger.valueOf(a), BigInteger.ONE)
    constructor(a: BigDecimal): this(a.unscaledValue(), BigInteger.TEN.pow(max(a.scale(), 0)))
    constructor(a: Double): this(BigDecimal.valueOf(a))

    private constructor(fraction: Fraction): this(fraction.numerator, fraction.denominator)
    constructor(s: String): this(s.let {
        if (s.contains('/')) {
            val components = s.split('/')

            if (components.size != 2) {
                throw NumberFormatException("for input string: $s")
            }

            Fraction(BigDecimal(components[0])) / Fraction(BigDecimal(components[1]))
        } else {
            Fraction(BigDecimal(s))
        }
    })

    init {
        fun init(numerator: BigInteger, denominator: BigInteger): Pair<BigInteger, BigInteger> {
            val gcd = gcd(numerator, denominator)

            return numerator / gcd to denominator / gcd
        }

        val (n, d) = if (denominator > BigInteger.ZERO) {
            init(numerator, denominator)
        } else {
            init(BigInteger.ZERO - numerator, BigInteger.ZERO - denominator)
        }

        this.numerator = n
        this.denominator = d
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

    override fun toDouble(): Double =
            (BigDecimal(numerator).setScale(128) / denominator.toBigDecimal()).toDouble()
    override fun toFloat(): Float = toDouble().toFloat()

    override fun toString(): String = if (denominator != BigInteger.ONE) {
        "$numerator/$denominator"
    } else {
        "$numerator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fraction
        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false
        return true
    }

    override fun hashCode(): Int = (numerator to denominator).hashCode()
}
