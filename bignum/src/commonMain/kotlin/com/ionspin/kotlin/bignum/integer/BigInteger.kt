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

package com.ionspin.kotlin.bignum.integer

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

enum class Sign {
    POSITIVE, NEGATIVE, ZERO;

    operator fun not(): Sign {
        return when (this) {
            POSITIVE -> NEGATIVE
            NEGATIVE -> POSITIVE
            ZERO -> ZERO
        }
    }

    fun toInt(): Int {
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
        private val arithmetic : BigIntegerArithmetic<WordArray, Word> = chosenArithmetic

        val ZERO = BigInteger(arithmetic.ZERO, Sign.ZERO)
        val ONE = BigInteger(arithmetic.ONE, Sign.POSITIVE)

        val LOG_10_OF_2 = log10(2.0)

        fun parseString(string: String, base: Int = 10): BigInteger {
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
                BigInteger(
                    arithmetic.parseForBase(string.substring(startIndex = 1, endIndex = string.length), base),
                    isNegative
                )
            } else {
                if (string.length == 1 && string[0] == '0') {
                    return ZERO
                }
                BigInteger(arithmetic.parseForBase(string, base), Sign.POSITIVE)
            }

        }

        internal fun fromWordArray(wordArray: WordArray, sign: Sign): BigInteger {
            return BigInteger(wordArray, sign)
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

        fun max(first: BigInteger, second: BigInteger): BigInteger {
            return if (first > second) {
                first
            } else {
                second
            }
        }

        fun min(first: BigInteger, second: BigInteger): BigInteger {
            return if (first < second) {
                first
            } else {
                second
            }
        }
    }

    internal val magnitude: WordArray = wordArray

    private fun isResultZero(resultMagnitude: WordArray): Boolean {
        return arithmetic.compare(resultMagnitude, arithmetic.ZERO) == 0
    }

    val numberOfWords = magnitude.size

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
        if (this == ZERO) { return other.negate()}
        if (other == ZERO) { return this }
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
        return if (sign == Sign.POSITIVE) {
            BigInteger(arithmetic.multiply(this.magnitude, other.magnitude), sign)
        } else {
            BigInteger(arithmetic.multiply(this.magnitude, other.magnitude), sign)
        }
    }

    fun divide(other: BigInteger): BigInteger {
        if (other.isZero()) {
            throw ArithmeticException("Division by zero! $this / $other")
        }

        val result = arithmetic.divide(this.magnitude, other.magnitude).first
        return if (result == arithmetic.ZERO) {
            ZERO
        } else {
            val sign = if (this.sign != other.sign) {
                Sign.NEGATIVE
            } else {
                Sign.POSITIVE
            }
            BigInteger(result, sign)
        }


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
        val result = arithmetic.divide(this.magnitude, other.magnitude)
        val quotient = if (result.first == arithmetic.ZERO) {
            ZERO
        } else {
            BigInteger(result.first, sign)
        }
        val remainder = if (result.second == arithmetic.ZERO) {
            ZERO
        } else {
            BigInteger(result.second, sign)
        }
        return Pair(
            quotient,
            remainder
        )
    }

    fun compare(other: BigInteger): Int {
        if (isZero() && other.isZero()) return 0
        if (other.isZero() && this.sign == Sign.POSITIVE) return 1
        if (other.isZero() && this.sign == Sign.NEGATIVE) return -1
        if (this.isZero() && other.sign == Sign.POSITIVE) return -1
        if (this.isZero() && other.sign == Sign.NEGATIVE) return 1
        if (sign != other.sign) return if (sign == Sign.POSITIVE) 1 else -1
        return arithmetic.compare(this.magnitude, other.magnitude)
    }

    fun isZero(): Boolean = this.sign == Sign.ZERO

    fun negate(): BigInteger {
        return BigInteger(wordArray = this.magnitude.copyOf(), sign = sign.not())
    }

    fun abs(): BigInteger {
        return BigInteger(wordArray = this.magnitude.copyOf(), sign = Sign.POSITIVE)
    }

    fun pow(exponent: BigInteger) : BigInteger {
        if (exponent <= Long.MAX_VALUE) {
            return pow(exponent.magnitude[0].toLong())
        }
        //TODO this is not efficient
        var counter = exponent
        var result = ONE
        while(counter > 0) {
            counter--
            result *= this
        }

        return result
    }

    fun pow(exponent: Long): BigInteger {
        return (0 until exponent).fold(ONE) { acc, _ ->
            acc * this
        }
    }

    fun pow(exponent: Int): BigInteger {
        return pow(exponent.toLong())
    }

    fun signum(): Int = when (sign) {
        Sign.POSITIVE -> 1
        Sign.NEGATIVE -> -1
        Sign.ZERO -> 0
    }

    fun bitAt(position: Long): Boolean {
        if (position / 63 > Int.MAX_VALUE) {
            throw ArithmeticException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= magnitude.size) {
            return false
        }
        val bitPosition = position % 63
        val word = magnitude[wordPosition.toInt()]
        return (word and (1UL shl bitPosition.toInt()) == 1UL)
    }

    fun numberOfDigits() : Long {
//        val bitLenght = arithmetic.bitLength(magnitude)
////        val minDigit = ceil((bitLenght - 1) * LOG_10_OF_2)
//        val maxDigit = floor(bitLenght * LOG_10_OF_2) + 1
//        val correct = this / 10.toBigInteger().pow(maxDigit.toInt())
//        return when {
//            correct == ZERO -> maxDigit.toInt() - 1
//            correct > 0 && correct < 10 -> maxDigit.toInt()
//            else -> -1
//        }
        var tmp = this
        var counter = 0L
        while (tmp.compareTo(0) != 0) {
            tmp /= 10
            counter ++
        }
        return counter


    }




    infix fun shl(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftLeft(this.magnitude, places), sign)
    }

    infix fun shr(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftRight(this.magnitude, places), sign)
    }

    operator fun unaryMinus(): BigInteger = negate()

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

    operator fun dec() : BigInteger {
        return this - 1
    }

    operator fun inc() : BigInteger {
        return this + 1
    }

    infix fun divrem(other: BigInteger): QuotientAndRemainder {
        val result = divideAndRemainder(other)
        return QuotientAndRemainder(result.first, result.second)
    }

    infix fun and(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.and(this.magnitude, other.magnitude), sign)
    }

    infix fun or(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.or(this.magnitude, other.magnitude), sign)
    }

    infix fun xor(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.xor(this.magnitude, other.magnitude), sign)
    }

    /**
     * Inverts only up to chosen [arithmetic] [BigIntegerArithmetic.bitLength] bits.
     * This is different from Java biginteger which returns inverse in two's complement.
     *
     * I.e.: If the number was "1100" binary, invPrecise returns "0011" => "11" => 4 decimal
     */
    fun invPrecise(): BigInteger {
        return BigInteger(arithmetic.inv(this.magnitude), sign)
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

    //
    //
    // ----------------- Interop with basic types ----------------------
    //
    //



    // ------------- Addition -----------


    operator fun plus(int: Int): BigInteger {
        return this.plus(BigInteger.fromInt(int))
    }


    operator fun plus(long: Long): BigInteger {
        return this.plus(BigInteger.fromLong(long))
    }


    operator fun plus(short: Short): BigInteger {
        return this.plus(BigInteger.fromShort(short))
    }


    operator fun plus(byte: Byte): BigInteger {
        return this.plus(BigInteger.fromByte(byte))
    }

    // ------------- Multiplication -----------


    operator fun times(int: Int): BigInteger {
        return this.multiply(BigInteger.fromInt(int))
    }


    operator fun times(long: Long): BigInteger {
        return this.multiply(BigInteger.fromLong(long))
    }


    operator fun times(short: Short): BigInteger {
        return this.multiply(BigInteger.fromShort(short))
    }


    operator fun times(byte: Byte): BigInteger {
        return this.multiply(BigInteger.fromByte(byte))
    }


    //TODO eh
    internal operator fun times(char: Char) : String {
        if (this < 0) {
            throw RuntimeException("Char cannot be multiplied with negative number")
        }
        var counter = this
        val stringBuilder = StringBuilder()
        while (counter > 0) {
            stringBuilder.append(char)
            counter--
        }
        return stringBuilder.toString()
    }

    // ------------- Subtraction -----------


    operator fun minus(int: Int): BigInteger {
        return this.minus(BigInteger.fromInt(int))
    }


    operator fun minus(long: Long): BigInteger {
        return this.minus(BigInteger.fromLong(long))
    }


    operator fun minus(short: Short): BigInteger {
        return this.minus(BigInteger.fromShort(short))
    }


    operator fun minus(byte: Byte): BigInteger {
        return this.minus(BigInteger.fromByte(byte))
    }

    // ------------- Division -----------


    operator fun div(int: Int): BigInteger {
        return this.div(BigInteger.fromInt(int))
    }


    operator fun div(long: Long): BigInteger {
        return this.div(BigInteger.fromLong(long))
    }


    operator fun div(short: Short): BigInteger {
        return this.div(BigInteger.fromShort(short))
    }


    operator fun div(byte: Byte): BigInteger {
        return this.div(BigInteger.fromByte(byte))
    }


    operator fun rem(int: Int): BigInteger {
        return this.rem(BigInteger.fromInt(int))
    }


    operator fun rem(long: Long): BigInteger {
        return this.rem(BigInteger.fromLong(long))
    }


    operator fun rem(short: Short): BigInteger {
        return this.rem(BigInteger.fromShort(short))
    }


    operator fun rem(byte: Byte): BigInteger {
        return this.rem(BigInteger.fromByte(byte))
    }

}