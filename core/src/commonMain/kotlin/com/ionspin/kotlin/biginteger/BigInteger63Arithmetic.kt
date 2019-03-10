package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
object BigInteger63Arithmetic : BigIntegerArithmetic<ULongArray, ULong> {
    override val ZERO: ULongArray
        get() = TODO("not implemented yet")
    override val ONE: ULongArray
        get() = TODO("not implemented yet")
    override val base: ULong
        get() = TODO("not implemented yet")
    override val basePowerOfTwo: Int
        get() = TODO("not implemented yet")

    override fun numberOfLeadingZeroes(value: UInt): Int {
        TODO("not implemented yet")
    }

    override fun bitLength(value: ULongArray): Int {
        TODO("not implemented yet")
    }

    override fun bitLength(value: UInt): Int {
        TODO("not implemented yet")
    }

    override fun removeLeadingZeroes(bigInteger: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun shiftLeft(operand: ULongArray, places: Int): ULongArray {
        TODO("not implemented yet")
    }

    override fun shiftRight(operand: ULongArray, places: Int): ULongArray {
        TODO("not implemented yet")
    }

    override fun normalize(dividend: ULongArray, divisor: ULongArray): Triple<ULongArray, ULongArray, Int> {
        TODO("not implemented yet")
    }

    override fun normalize(operand: ULongArray): Pair<ULongArray, Int> {
        TODO("not implemented yet")
    }

    override fun denormalize(remainderNormalized: ULongArray, normalizationShift: Int): ULongArray {
        TODO("not implemented yet")
    }

    override fun compare(first: ULongArray, second: ULongArray): Int {
        TODO("not implemented yet")
    }

    override fun addition(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun substract(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun multiply(first: UInt, second: UInt): ULongArray {
        TODO("not implemented yet")
    }

    override fun multiply(first: ULongArray, second: UInt): ULongArray {
        TODO("not implemented yet")
    }

    override fun multiply(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun basicDivide(
        unnormalizedDividend: ULongArray,
        unnormalizedDivisor: ULongArray
    ): Pair<ULongArray, ULongArray> {
        TODO("not implemented yet")
    }

    override fun baseReciprocal(unnomrmalizedOperand: ULongArray, precision: Int): ULongArray {
        TODO("not implemented yet")
    }


}