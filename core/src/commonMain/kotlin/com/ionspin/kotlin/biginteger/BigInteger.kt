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

enum class Sign {
    POSITIVE, NEGATIVE, ZERO;
    operator fun not() : Sign {
        return when (this) {
            POSITIVE -> NEGATIVE
            NEGATIVE -> POSITIVE
            ZERO -> ZERO
        }
    }
    fun toInt() : Int {
        return when (this) {
            POSITIVE -> 1
            NEGATIVE -> -1
            ZERO -> 0
        }
    }
}

@ExperimentalUnsignedTypes
class BigInteger private constructor(wordArray: WordArray, val sign: Sign) : Comparable<Any> {


    @ExperimentalUnsignedTypes
    companion object {
        private val arithmetic = chosenArithmetic

        val positive = true
        val negative = true

        val ZERO = BigInteger(arithmetic.ZERO, Sign.ZERO)
        val ONE = BigInteger(arithmetic.ONE, Sign.POSITIVE)

        fun parseString(string: String, base: Int): BigInteger {
            val signed = (string[0] == '-' || string[0] == '+')
            return if (signed) {
                if (string.length == 1) {
                    throw NumberFormatException("Invalid big integer: $string")
                }
                val isNegative = if (string[0] == '-') {
                    Sign.NEGATIVE
                } else {
                    Sign.POSITIVE
                }
                if (string.length == 2 && string[1] == '0') {
                    return ZERO
                }
                BigInteger(arithmetic.parseForBase(string.substring(startIndex = 1, endIndex = string.length), base), isNegative)
            } else {
                if (string.length == 1 && string[0] == '0') {
                    return ZERO
                }
                BigInteger(arithmetic.parseForBase(string, base), Sign.POSITIVE)
            }

        }

        private inline fun <reified T> determinSignFromNumber(number: Comparable<T>): Sign {
            return when (T::class) {
                Long::class -> {
                    number as Long
                    when {
                        number < 0 -> Sign.NEGATIVE
                        number > 0 -> Sign.POSITIVE
                        else -> Sign.ZERO
                    }
                }
                Int::class -> {
                    number as Int
                    when {
                        number < 0 -> Sign.NEGATIVE
                        number > 0 -> Sign.POSITIVE
                        else -> Sign.ZERO
                    }
                }
                Short::class -> {
                    number as Short
                    when {
                        number < 0 -> Sign.NEGATIVE
                        number > 0 -> Sign.POSITIVE
                        else -> Sign.ZERO
                    }
                }
                Byte::class -> {
                    number as Byte
                    when {
                        number < 0 -> Sign.NEGATIVE
                        number > 0 -> Sign.POSITIVE
                        else -> Sign.ZERO
                    }
                }
                else -> throw RuntimeException("Unsupported type ${T::class.simpleName}")
            }

        }


        fun fromLong(long: Long) = BigInteger(arithmetic.fromLong(long), determinSignFromNumber(long))
        fun fromInt(int: Int) = BigInteger(arithmetic.fromInt(int), determinSignFromNumber(int))
        fun fromShort(short: Short) = BigInteger(arithmetic.fromShort(short), determinSignFromNumber(short))
        fun fromByte(byte: Byte) = BigInteger(arithmetic.fromByte(byte), determinSignFromNumber(byte))
    }

    internal val magnitude: WordArray = wordArray

    private fun isResultZero(resultMagnitude : WordArray) : Boolean {
        return arithmetic.compare(resultMagnitude, arithmetic.ZERO) == 0
    }




    fun add(other: BigInteger): BigInteger {
        val comparison = arithmetic.compare(this.magnitude, other.magnitude)
        return if (other.sign == this.sign) {
            return BigInteger(arithmetic.add(this.magnitude, other.magnitude), sign)
        } else {
            when {
                comparison > 0 -> {
                    BigInteger(arithmetic.substract(this.magnitude, other.magnitude), sign)
                }
                comparison < 0 -> {
                    BigInteger(arithmetic.substract(other.magnitude, this.magnitude), other.sign)
                }
                else -> {
                    ZERO
                }
            }
        }

    }

    fun subtract(other: BigInteger): BigInteger {
        val comparison = arithmetic.compare(this.magnitude, other.magnitude)
        return if (other.sign == this.sign) {
            when {
                comparison > 0 -> {
                    BigInteger(arithmetic.substract(this.magnitude, other.magnitude), sign)
                }
                comparison < 0 -> {
                    BigInteger(arithmetic.substract(other.magnitude, this.magnitude), !sign)
                }
                else -> {
                    ZERO
                }
            }
        } else {
            return BigInteger(arithmetic.add(this.magnitude, other.magnitude), sign)
        }
    }

    fun multiply(other: BigInteger): BigInteger {
        if (this.isZero() || other.isZero()) {
            return ZERO
        }

        val sign = if (this.sign != other.sign) {
            Sign.NEGATIVE
        } else {
            Sign.POSITIVE
        }
        if (sign == Sign.POSITIVE) {
            return BigInteger(arithmetic.multiply(this.magnitude, other.magnitude), sign)
        } else {
            return BigInteger(arithmetic.multiply(this.magnitude, other.magnitude), sign)
        }
    }

    fun divide(other: BigInteger): BigInteger {
        if (other.isZero()) {
            throw ArithmeticException("Division by zero! $this / $other")
        }
        val sign = if (this.sign != other.sign) {
            Sign.NEGATIVE
        } else {
            Sign.POSITIVE
        }

        return BigInteger(arithmetic.divide(this.magnitude, other.magnitude).first, sign)
    }

    fun remainder(other: BigInteger): BigInteger {
        if (other.isZero()) {
            throw ArithmeticException("Division by zero! $this / $other")
        }
        val sign = if (this.sign != other.sign) {
            Sign.NEGATIVE
        } else {
            Sign.POSITIVE
        }

        return BigInteger(arithmetic.divide(this.magnitude, other.magnitude).second, sign)
    }

    fun divideAndRemainder(other: BigInteger): Pair<BigInteger, BigInteger> {
        if (other.isZero()) {
            throw ArithmeticException("Division by zero! $this / $other")
        }
        val sign = if (this.sign != other.sign) {
            Sign.NEGATIVE
        } else {
            Sign.POSITIVE
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
        if (sign != other.sign) return if (sign == Sign.POSITIVE) 1 else -1
        return arithmetic.compare(this.magnitude, other.magnitude)
    }

    fun isZero(): Boolean = this.sign == Sign.ZERO


    infix fun shl(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftLeft(this.magnitude, places), sign)
    }

    infix fun shr(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftRight(this.magnitude, places), sign)
    }


    operator fun plus(other: BigInteger): BigInteger {
        return add(other)
    }

    operator fun minus(other: BigInteger): BigInteger {
        return subtract(other)
    }

    operator fun times(other: BigInteger): BigInteger {
        return multiply(other)
    }

    operator fun div(other: BigInteger): BigInteger {
        return divide(other)
    }

    operator fun rem(other: BigInteger): BigInteger {
        return remainder(other)
    }

    infix fun divrem(other: BigInteger): QuotientAndRemainder {
        val result = divideAndRemainder(other)
        return QuotientAndRemainder(result.first, result.second)
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is BigInteger -> compare(other)
            is Long -> compare(BigInteger.fromLong(other))
            is Int -> compare(BigInteger.fromInt(other))
            is Short -> compare(BigInteger.fromShort(other))
            is Byte -> compare(BigInteger.fromByte(other))
            else -> throw RuntimeException("Invalid comparison type for BigInteger: ${other::class.simpleName}")
        }

    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigInteger -> compare(other)
            is Long -> compare(BigInteger.fromLong(other))
            is Int -> compare(BigInteger.fromInt(other))
            is Short -> compare(BigInteger.fromShort(other))
            is Byte -> compare(BigInteger.fromByte(other))
            else -> -1
        }
        return comparison == 0
    }



    override fun toString(): String {
        return toString(10)
    }

    fun toString(base: Int): String {
        val sign = if (sign == Sign.NEGATIVE) {
            "-"
        } else {
            ""
        }
        return sign + arithmetic.toString(this.magnitude, base)
    }

    data class QuotientAndRemainder(val quotient: BigInteger, val remainder: BigInteger)


}