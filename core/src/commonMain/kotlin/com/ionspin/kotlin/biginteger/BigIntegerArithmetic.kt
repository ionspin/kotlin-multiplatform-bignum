package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
interface BigIntegerArithmetic<BackingType, PrimitiveType> {
    val ZERO : BackingType
    val ONE : BackingType
    val base: PrimitiveType
    val basePowerOfTwo: Int
    /**
     * Hackers delight 5-11
     */
    fun numberOfLeadingZeroes(value: UInt): Int

    fun bitLength(value: BackingType): Int
    fun bitLength(value: UInt): Int
    fun removeLeadingZeroes(bigInteger: BackingType): BackingType
    fun shiftLeft(operand: BackingType, places: Int): BackingType
    fun shiftRight(operand: BackingType, places: Int): BackingType
    fun normalize(dividend: BackingType, divisor: BackingType): Triple<BackingType, BackingType, Int>
    fun normalize(operand : BackingType) : Pair<BackingType, Int>
    fun denormalize(
        remainderNormalized: BackingType,
        normalizationShift: Int
    ): BackingType

    fun compare(first: BackingType, second: BackingType): Int
    fun addition(first: BackingType, second: BackingType): BackingType
    fun substract(first: BackingType, second: BackingType): BackingType
    fun multiply(first: UInt, second: UInt): BackingType
    fun multiply(first: BackingType, second: UInt): BackingType
    fun multiply(first: BackingType, second: BackingType): BackingType
    /**
     * Based on Basecase DivRem algorithm from
     * Modern Computer Arithmetic, Richard Brent and Paul Zimmermann, Cambridge University Press, 2010.
     * Version 0.5.9
     * https://members.loria.fr/PZimmermann/mca/pub226.html
     */
    fun basicDivide(
        unnormalizedDividend: BackingType,
        unnormalizedDivisor: BackingType
    ): Pair<BackingType, BackingType>

    fun baseReciprocal(unnomrmalizedOperand : BackingType, precision : Int) : BackingType
}

