/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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

        fun parseString(string : String, base : Int) : BigInteger{
            val signed = (string[0] == '-' || string[0] == '+')
            return if (signed) {
                val isNegative = string[0] == '-'
                BigInteger(arithmetic.parseForBase(string.substring(1, string.length), base), isNegative)
            } else {
                BigInteger(arithmetic.parseForBase(string, base), positive)
            }

        }

        fun fromLong(long : Long) = BigInteger(arithmetic.fromLong(long), long > 0)
        fun fromInt(int : Int) = BigInteger(arithmetic.fromInt(int), int > 0)
        fun fromShort(short : Short) = BigInteger(arithmetic.fromShort(short), short > 0)
        fun fromByte(byte : Byte) = BigInteger(arithmetic.fromByte(byte), byte > 0)
    }

    private val magnitude: WordArray = wordArray


    fun add(other: BigInteger): BigInteger {
        val sign = if (this > other) {
            this.sign
        } else {
            other.sign
        }
        return BigInteger(arithmetic.add(this.magnitude, other.magnitude), sign)
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

        return BigInteger(arithmetic.divide(this.magnitude, other.magnitude).first, sign)
    }

    fun remainder(other: BigInteger): BigInteger {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }

        return BigInteger(arithmetic.divide(this.magnitude, other.magnitude).second, sign)
    }

    fun divideAndRemainder(other: BigInteger): Pair<BigInteger, BigInteger> {
        val sign = if (this.sign != other.sign) {
            negative
        } else {
            positive
        }
        return Pair(
            BigInteger(arithmetic.divide(this.magnitude, other.magnitude).first, sign),
            BigInteger(arithmetic.divide(this.magnitude, other.magnitude).second, sign)
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

    infix fun BigInteger.divrem(other: BigInteger): QuotientAndRemainder {
        val result = divideAndRemainder(other)
        return QuotientAndRemainder(result.first, result.second)
    }

    override fun compareTo(other: BigInteger): Int {
        return compare(other)
    }

    override fun toString() : String {
        return toString(10)
    }

    fun toString(base : Int) : String {
        val sign = if (!sign) {
            "-"
        } else {
            ""
        }
        return sign + arithmetic.toString(this.magnitude, base)
    }

    data class QuotientAndRemainder(val quotient : BigInteger, val remainder : BigInteger)


}