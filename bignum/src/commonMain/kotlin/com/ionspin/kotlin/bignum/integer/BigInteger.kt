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

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.BitwiseCapable
import com.ionspin.kotlin.bignum.CommonBigNumberOperations
import com.ionspin.kotlin.bignum.NarrowingOperations
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import kotlin.math.ceil
import kotlin.math.log10


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

/**
 * Arbitrary precision integer arithmetic.
 *
 * Based on unsigned arrays, currently limited to [Int.MAX_VALUE] words.
 */
@ExperimentalUnsignedTypes
class BigInteger internal constructor(wordArray: WordArray, val sign: Sign) : BigNumber<BigInteger>,
    CommonBigNumberOperations<BigInteger>,
    NarrowingOperations<BigInteger>,
    BitwiseCapable<BigInteger>, Comparable<Any> {


    constructor(long: Long) : this(arithmetic.fromLong(long), determinSignFromNumber(long))
    constructor(int: Int) : this(arithmetic.fromInt(int), determinSignFromNumber(int))
    constructor(short: Short) : this(arithmetic.fromShort(short), determinSignFromNumber(short))
    constructor(byte: Byte) : this(arithmetic.fromByte(byte), determinSignFromNumber(byte))

    override fun getCreator(): BigNumber.Creator<BigInteger> {
        return BigInteger
    }

    override fun getInstance(): BigInteger {
        return this
    }

    @ExperimentalUnsignedTypes
    companion object : BigNumber.Creator<BigInteger>, BigNumber.Util<BigInteger> {
        private val arithmetic: BigIntegerArithmetic<WordArray, Word> = chosenArithmetic

        val ZERO = BigInteger(arithmetic.ZERO, Sign.ZERO)
        val ONE = BigInteger(arithmetic.ONE, Sign.POSITIVE)
        val TEN = BigInteger(arithmetic.TEN, Sign.POSITIVE)

        val LOG_10_OF_2 = log10(2.0)

        override fun parseString(string: String, base: Int): BigInteger {
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
        //BigIntegers are immutable so this is pointless, but the rest of creator implementations use this.
        override fun fromBigInteger(bigInteger: BigInteger): BigInteger { return bigInteger }
        override fun fromULong(uLong: ULong) = BigInteger(arithmetic.fromULong(uLong), Sign.POSITIVE)
        override fun fromUInt(uInt: UInt) = BigInteger(arithmetic.fromUInt(uInt), Sign.POSITIVE)
        override fun fromUShort(uShort: UShort) = BigInteger(arithmetic.fromUShort(uShort), Sign.POSITIVE)
        override fun fromUByte(uByte: UByte) = BigInteger(arithmetic.fromUByte(uByte), Sign.POSITIVE)
        override fun fromLong(long: Long) = BigInteger(long)
        override fun fromInt(int: Int) = BigInteger(int)
        override fun fromShort(short: Short) = BigInteger(short)
        override fun fromByte(byte: Byte) = BigInteger(byte)

        override fun max(first: BigInteger, second: BigInteger): BigInteger {
            return if (first > second) {
                first
            } else {
                second
            }
        }

        override fun min(first: BigInteger, second: BigInteger): BigInteger {
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

    var stringRepresentation: String? = null

    override fun add(other: BigInteger): BigInteger {
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

    override fun subtract(other: BigInteger): BigInteger {
        val comparison = arithmetic.compare(this.magnitude, other.magnitude)
        if (this == ZERO) {
            return other.negate()
        }
        if (other == ZERO) {
            return this
        }
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

    override fun multiply(other: BigInteger): BigInteger {
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

    override fun divide(other: BigInteger): BigInteger {
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

    /**
     * Returns the remainder of division operation. Uses truncating division, which means
     * that the sign of remainder will be same as sign of dividend
     */
    override fun remainder(other: BigInteger): BigInteger {
        if (other.isZero()) {
            throw ArithmeticException("Division by zero! $this / $other")
        }
        var sign = if (this.sign != other.sign) {
            Sign.NEGATIVE
        } else {
            Sign.POSITIVE
        }
        val result = arithmetic.divide(this.magnitude, other.magnitude).second
        if (result == arithmetic.ZERO) {
            sign = Sign.ZERO
        }

        return BigInteger(result, sign)
    }

    override fun divideAndRemainder(other: BigInteger): Pair<BigInteger, BigInteger> {
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
            BigInteger(result.second, this.sign)
        }
        return Pair(
            quotient,
            remainder
        )
    }


    /**
     * D1Balanced reciprocal
     */
    private fun d1reciprocalRecursive(): BigInteger {
        return BigInteger(arithmetic.reciprocal(this.magnitude).first, sign)


    }

    fun sqrt(): SqareRootAndRemainder {
        return naiveSqrtRem()
    }

    fun naiveSqrtRem() : SqareRootAndRemainder {
        TODO()
//        var l = floor((numberOfWords - 1).toDouble() / 4).toInt()
//        if (l == 0) return
    }

//    fun basecaseSqrt() : SqareRootAndRemainder {
//
//    }

    fun gcd(other: BigInteger) : BigInteger {
        return naiveGcd(other)
    }

    private fun naiveGcd(other : BigInteger) : BigInteger {
        var u = this
        var v = other
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
    }

    fun modInverse(modulo: BigInteger) : BigInteger {
        if (gcd(modulo) != ONE) {
            throw ArithmeticException("BigInteger is not invertible. This and modulus are not relatively prime (coprime)")
        }
        var u = ONE
        var w = ZERO
        var b = this
        var c = modulo
        while (c != ZERO) {
            val (q,r) = b divrem c
            b = c
            c = r
            val tmpU = u
            u = w
            w = tmpU - q*w
        }
        return u
    }

    /**
     * Returns an always positive remainder of division operation
     */
    infix fun mod(modulo : BigInteger) : BigInteger {
        val result = this % modulo
        return if (result < 0) {
            result + modulo
        } else {
            result
        }
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

    override fun isZero(): Boolean = this.sign == Sign.ZERO

    override fun negate(): BigInteger {
        return BigInteger(wordArray = this.magnitude.copyOf(), sign = sign.not())
    }

    override fun abs(): BigInteger {
        return BigInteger(wordArray = this.magnitude.copyOf(), sign = Sign.POSITIVE)
    }

    override fun pow(exponent: BigInteger): BigInteger {
        if (exponent <= Long.MAX_VALUE) {
            return pow(exponent.magnitude[0].toLong())
        }
        //TODO this is not efficient
        var counter = exponent
        var result = ONE
        while (counter > 0) {
            counter--
            result *= this
        }

        return result
    }

    override fun pow(exponent: Long): BigInteger {
        val sign = if (sign == Sign.NEGATIVE) {
            if (exponent % 2 == 0L) {
                Sign.POSITIVE
            } else {
                Sign.NEGATIVE
            }
        } else {
            Sign.POSITIVE
        }
        return BigInteger(arithmetic.pow(magnitude, exponent), sign)
    }

    override fun pow(exponent: Int): BigInteger {
        return pow(exponent.toLong())
    }

    override fun signum(): Int = when (sign) {
        Sign.POSITIVE -> 1
        Sign.NEGATIVE -> -1
        Sign.ZERO -> 0
    }

    override fun bitAt(position: Long): Boolean {
        return arithmetic.bitAt(magnitude, position)
    }

    override fun setBitAt(position: Long, bit: Boolean): BigInteger {
        return BigInteger(arithmetic.setBitAt(magnitude, position, bit), sign)
    }

    override fun numberOfDecimalDigits(): Long {
        val bitLenght = arithmetic.bitLength(magnitude)
        val minDigit = ceil((bitLenght - 1) * LOG_10_OF_2)
//        val maxDigit = floor(bitLenght * LOG_10_OF_2) + 1
//        val correct = this / 10.toBigInteger().pow(maxDigit.toInt())
//        return when {
//            correct == ZERO -> maxDigit.toInt() - 1
//            correct > 0 && correct < 10 -> maxDigit.toInt()
//            else -> -1
//        }

        var tmp = this / 10.toBigInteger().pow(minDigit.toInt())
        var counter = 0L
        while (tmp.compareTo(0) != 0) {
            tmp /= 10
            counter++
        }
        return counter + minDigit.toInt()


    }


    override infix fun shl(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftLeft(this.magnitude, places), sign)
    }

    override infix fun shr(places: Int): BigInteger {
        return BigInteger(arithmetic.shiftRight(this.magnitude, places), sign)
    }

    override operator fun unaryMinus(): BigInteger = negate()


    operator fun dec(): BigInteger {
        return this - ONE
    }

    operator fun inc(): BigInteger {
        return this + ONE
    }

    infix fun divrem(other: BigInteger): QuotientAndRemainder {
        val result = divideAndRemainder(other)
        return QuotientAndRemainder(result.first, result.second)
    }

    override infix fun and(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.and(this.magnitude, other.magnitude), sign)
    }

    override infix fun or(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.or(this.magnitude, other.magnitude), sign)
    }

    override infix fun xor(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.xor(this.magnitude, other.magnitude), sign)
    }

    /**
     * Inverts only up to chosen [arithmetic] [BigIntegerArithmetic.bitLength] bits.
     * This is different from Java biginteger which returns inverse in two's complement.
     *
     * I.e.: If the number was "1100" binary, not returns "0011" => "11" => 4 decimal
     */
    override fun not(): BigInteger {
        return BigInteger(arithmetic.not(this.magnitude), sign)
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is BigInteger -> compare(other)
            is Long -> compare(fromLong(other))
            is Int -> compare(fromInt(other))
            is Short -> compare(fromShort(other))
            is Byte -> compare(fromByte(other))
            is ULong -> compare(fromULong(other))
            is UInt -> compare(fromUInt(other))
            is UShort -> compare(fromUShort(other))
            is UByte -> compare(fromUByte(other))
            else -> throw RuntimeException("Invalid comparison type for BigInteger: ${other::class.simpleName}")
        }

    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigInteger -> compare(other)
            is Long -> compare(fromLong(other))
            is Int -> compare(fromInt(other))
            is Short -> compare(fromShort(other))
            is Byte -> compare(fromByte(other))
            is ULong -> compare(fromULong(other))
            is UInt -> compare(fromUInt(other))
            is UShort -> compare(fromUShort(other))
            is UByte -> compare(fromUByte(other))
            else -> -1
        }
        return comparison == 0
    }

    override fun hashCode(): Int {
        return magnitude.contentHashCode() + sign.hashCode()
    }

    override fun toString(): String {
        //TODO think about limiting the size of string, and offering a stream of characters instead of huge strings
//        if (stringRepresentation == null) {
//            stringRepresentation = toString(10)
//        }
//        return stringRepresentation!!

        //Linux build complains about mutating a frozen object, let's try without this representation caching
        return toString(10)
    }

    override fun toString(base: Int): String {
        val sign = if (sign == Sign.NEGATIVE) {
            "-"
        } else {
            ""
        }
        return sign + arithmetic.toString(this.magnitude, base)
    }

    data class QuotientAndRemainder(val quotient: BigInteger, val remainder: BigInteger)

    data class SqareRootAndRemainder(val squareRoot: BigInteger, val remainder: BigInteger)

    //TODO eh
    operator fun times(char: Char): String {
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

    fun toModularBigInteger(modulo : BigInteger) : ModularBigInteger {
        val creator = ModularBigInteger.creatorForModulo(modulo)
        return creator.fromBigInteger(this)
    }

    override fun intValue(exactRequired: Boolean): Int {
        if (exactRequired && this > Int.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to int and provide exact value")
        }
        return magnitude[0].toInt()
    }

    override fun longValue(exactRequired: Boolean): Long {
        if (exactRequired && (this > Long.MAX_VALUE.toUInt())) {
            throw ArithmeticException("Cannot convert to long and provide exact value")
        }
        return magnitude[0].toLong()
    }

    override fun byteValue(exactRequired: Boolean): Byte {
        if (exactRequired && this > Byte.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to byte and provide exact value")
        }
        return magnitude[0].toByte()
    }

    override fun shortValue(exactRequired: Boolean): Short {
        if (exactRequired && this > Short.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to short and provide exact value")
        }
        return magnitude[0].toShort()
    }

    override fun uintValue(exactRequired: Boolean): UInt {
        if (exactRequired && this > UInt.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned int and provide exact value")
        }
        return magnitude[0].toUInt()
    }

    override fun ulongValue(exactRequired: Boolean): ULong {
        if (exactRequired && (this > ULong.MAX_VALUE.toUInt())) {
            throw ArithmeticException("Cannot convert to unsigned long and provide exact value")
        }
        return magnitude[0]
    }

    override fun ubyteValue(exactRequired: Boolean): UByte {
        if (exactRequired && this > UByte.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned byte and provide exact value")
        }
        return magnitude[0].toUByte()
    }

    override fun ushortValue(exactRequired: Boolean): UShort {
        if (exactRequired && this > UShort.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned short and provide exact value")
        }
        return magnitude[0].toUShort()
    }
}