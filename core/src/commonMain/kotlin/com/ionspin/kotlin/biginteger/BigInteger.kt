package com.ionspin.kotlin.biginteger


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */



@ExperimentalUnsignedTypes
class BigInteger private constructor(wordArray: WordArray, val sign: Boolean) : Comparable<BigInteger> {


    @ExperimentalUnsignedTypes
    companion object {
        private val arithmetic = chosenArithmetic

        val positive = true
        val negative = true

        val ZERO = BigInteger(arithmetic.ZERO, negative)
        val ONE = BigInteger(arithmetic.ONE, positive)
    }

    private val magnitude: WordArray = wordArray


    fun add(other: BigInteger): BigInteger {
        val sign = if (this > other) {
            this.sign
        } else {
            other.sign
        }
        return BigInteger(arithmetic.addition(this.magnitude, other.magnitude), sign)
    }

    fun substract(other: BigInteger): BigInteger {
        val sign = if (this > other) {
            this.sign
        } else {
            !other.sign
        }

        return BigInteger(arithmetic.substract(this.magnitude, other.magnitude), sign)
    }

    fun multiply(other: BigInteger): BigInteger {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }

        return BigInteger(arithmetic.multiply(this.magnitude, other.magnitude), sign)
    }

    fun divide(other: BigInteger): BigInteger {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }

        return BigInteger(arithmetic.basicDivide(this.magnitude, other.magnitude).first, sign)
    }

    fun remainder(other: BigInteger): BigInteger {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }

        return BigInteger(arithmetic.basicDivide(this.magnitude, other.magnitude).second, sign)
    }

    fun divideAndRemainder(other: BigInteger): Pair<BigInteger, BigInteger> {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }
        return Pair(
            BigInteger(arithmetic.basicDivide(this.magnitude, other.magnitude).first, sign),
            BigInteger(arithmetic.basicDivide(this.magnitude, other.magnitude).second, sign)
        )
    }

    fun compare(other: BigInteger): Int {
        if (isZero() && other.isZero()) return 0
        if (other.isZero()) return 1
        if (this.isZero()) return -1
        if (sign != other.sign) return if (sign == positive) 1 else -1
        return arithmetic.compare(this.magnitude, other.magnitude)
    }

    fun isZero(): Boolean = this.magnitude.size == 0


    infix fun BigInteger.shl(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftLeft(this.magnitude, places), sign)
    }

    infix fun BigInteger.shr(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftRight(this.magnitude, places), sign)
    }


    operator fun BigInteger.plus(other: BigInteger): BigInteger {
        return add(other)
    }

    operator fun BigInteger.minus(other: BigInteger): BigInteger {
        return substract(other)
    }

    operator fun BigInteger.times(other: BigInteger): BigInteger {
        return multiply(other)
    }

    operator fun BigInteger.div(other: BigInteger): BigInteger {
        return divide(other)
    }

    operator fun BigInteger.rem(other: BigInteger): BigInteger {
        return remainder(other)
    }

    infix fun BigInteger.divrem(other: BigInteger): Pair<BigInteger, BigInteger> {
        return divideAndRemainder(other)
    }

    override fun compareTo(other: BigInteger): Int {
        return compare(other)
    }


}