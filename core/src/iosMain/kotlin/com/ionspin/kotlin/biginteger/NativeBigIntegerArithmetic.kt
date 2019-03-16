package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
actual object NativeBigIntegerArithmetic : BigIntegerArithmetic<IntArray, Int> {
    override val ZERO: IntArray
        get() = TODO("not implemented yet")
    override val ONE: IntArray
        get() = TODO("not implemented yet")
    val base: Int
        get() = TODO("not implemented yet")
    override val basePowerOfTwo: Int
        get() = TODO("not implemented yet")

    override fun numberOfLeadingZeroes(value: Int): Int {
        TODO("not implemented yet")
    }

    override fun bitLength(value: IntArray): Int {
        TODO("not implemented yet")
    }



    override fun shiftLeft(operand: IntArray, places: Int): IntArray {
        TODO("not implemented yet")
    }

    override fun shiftRight(operand: IntArray, places: Int): IntArray {
        TODO("not implemented yet")
    }

    override fun compare(first: IntArray, second: IntArray): Int {
        TODO("not implemented yet")
    }

    override fun add(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun substract(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }



    override fun multiply(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun divide(first: IntArray, second: IntArray): Pair<IntArray, IntArray> {
        TODO("not implemented yet")
    }

    override fun parseForBase(number: String, base: Int) : IntArray {
        TODO("not implemented yet")
    }

    override fun toString(operand: IntArray, base: Int): String {
        TODO("not implemented yet")
    }
}