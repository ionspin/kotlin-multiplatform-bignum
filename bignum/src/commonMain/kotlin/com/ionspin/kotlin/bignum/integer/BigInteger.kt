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
import com.ionspin.kotlin.bignum.ByteArrayDeserializable
import com.ionspin.kotlin.bignum.ByteArraySerializable
import com.ionspin.kotlin.bignum.CommonBigNumberOperations
import com.ionspin.kotlin.bignum.NarrowingOperations
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10

/**
 * Arbitrary precision integer arithmetic.
 *
 * Based on unsigned arrays, currently limited to [Int.MAX_VALUE] words.
 */

class BigInteger internal constructor(wordArray: WordArray, requestedSign: Sign) : BigNumber<BigInteger>,
    CommonBigNumberOperations<BigInteger>,
    NarrowingOperations<BigInteger>,
    BitwiseCapable<BigInteger>, Comparable<Any>,
    ByteArraySerializable {

    constructor(long: Long) : this(arithmetic.fromLong(long), determinSignFromNumber(long))
    constructor(int: Int) : this(arithmetic.fromInt(int), determinSignFromNumber(int))
    constructor(short: Short) : this(arithmetic.fromShort(short), determinSignFromNumber(short))
    constructor(byte: Byte) : this(arithmetic.fromByte(byte), determinSignFromNumber(byte))

    init {
        if (requestedSign == Sign.ZERO) {
            require(isResultZero(wordArray)) {
                "sign should be Sign.ZERO iff magnitude has a value of 0"
            }
        }
    }

    override fun getCreator(): BigNumber.Creator<BigInteger> {
        return BigInteger
    }

    override fun getInstance(): BigInteger {
        return this
    }

    fun getBackingArrayCopy(): WordArray {
        return magnitude.copyOf()
    }

    fun getSign(): Sign {
        return sign
    }

    companion object : BigNumber.Creator<BigInteger>, BigNumber.Util<BigInteger>, ByteArrayDeserializable<BigInteger> {
        private val arithmetic: BigIntegerArithmetic = chosenArithmetic

        override val ZERO = BigInteger(arithmetic.ZERO, Sign.ZERO)
        override val ONE = BigInteger(arithmetic.ONE, Sign.POSITIVE)
        override val TWO = BigInteger(arithmetic.TWO, Sign.POSITIVE)
        override val TEN = BigInteger(arithmetic.TEN, Sign.POSITIVE)

        val LOG_10_OF_2 = log10(2.0)

        fun createFromWordArray(wordArray: WordArray, requestedSign: Sign): BigInteger {
            return BigInteger(wordArray, requestedSign)
        }

        override fun parseString(string: String, base: Int): BigInteger {
            if (base < 2 || base > 36) {
                throw NumberFormatException("Unsupported base: $base. Supported base range is from 2 to 36")
            }
            val decimal = string.contains('.')
            if (decimal) {
                val bigDecimal = BigDecimal.parseString(string)
                val isActuallyDecimal = (bigDecimal - bigDecimal.floor()) > 0
                if (isActuallyDecimal) {
                    throw NumberFormatException("Supplied string is decimal, which cannot be converted to BigInteger without precision loss.")
                }
                return bigDecimal.toBigInteger()
            }
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
                else -> throw RuntimeException("Unsupported type ${T::class}")
            }
        }

        // BigIntegers are immutable so this is pointless, but the rest of creator implementations use this.
        override fun fromBigInteger(bigInteger: BigInteger): BigInteger {
            return bigInteger
        }

        override fun fromULong(uLong: ULong) = BigInteger(arithmetic.fromULong(uLong), Sign.POSITIVE)
        override fun fromUInt(uInt: UInt) = BigInteger(arithmetic.fromUInt(uInt), Sign.POSITIVE)
        override fun fromUShort(uShort: UShort) = BigInteger(arithmetic.fromUShort(uShort), Sign.POSITIVE)
        override fun fromUByte(uByte: UByte) = BigInteger(arithmetic.fromUByte(uByte), Sign.POSITIVE)
        override fun fromLong(long: Long) = BigInteger(long)
        override fun fromInt(int: Int) = BigInteger(int)
        override fun fromShort(short: Short) = BigInteger(short)
        override fun fromByte(byte: Byte) = BigInteger(byte)

        override fun tryFromFloat(float: Float, exactRequired: Boolean): BigInteger {
            val floatDecimalPart = float - floor(float)
            val bigDecimal = BigDecimal.fromFloat(floor(float), null)

            if (exactRequired) {
                if (floatDecimalPart > 0) {
                    throw ArithmeticException("Cant create BigInteger without precision loss, and exact  value was required")
                }
            }
            return bigDecimal.toBigInteger()
        }

        override fun tryFromDouble(double: Double, exactRequired: Boolean): BigInteger {
            val doubleDecimalPart = double - floor(double)
            val bigDecimal = BigDecimal.fromDouble(floor(double), null)

            if (exactRequired) {
                if (doubleDecimalPart > 0) {
                    throw ArithmeticException("Cant create BigInteger without precision loss, and exact  value was required")
                }
            }
            return bigDecimal.toBigInteger()
        }

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

        override fun fromUByteArray(
            source: UByteArray,
            sign: Sign
        ): BigInteger {
            val result = arithmetic.fromUByteArray(source)
            return BigInteger(result, sign)
        }

        override fun fromByteArray(
            source: ByteArray,
            sign: Sign
        ): BigInteger {
            val result = arithmetic.fromByteArray(source)
            return BigInteger(result, sign)
        }
    }

    internal val magnitude: WordArray = BigInteger63Arithmetic.removeLeadingZeros(wordArray)

    internal val sign: Sign = if (isResultZero(magnitude)) {
        Sign.ZERO
    } else {
        requestedSign
    }

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
                    BigInteger(arithmetic.subtract(this.magnitude, other.magnitude), sign)
                }
                comparison < 0 -> {
                    BigInteger(arithmetic.subtract(other.magnitude, this.magnitude), other.sign)
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
                    BigInteger(arithmetic.subtract(this.magnitude, other.magnitude), sign)
                }
                comparison < 0 -> {
                    BigInteger(arithmetic.subtract(other.magnitude, this.magnitude), !sign)
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
        if (other == ONE) {
            return this
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

    fun sqrt(): BigInteger {
        return BigInteger(arithmetic.sqrt(magnitude).first, this.sign)
    }

    fun sqrtAndRemainder(): SqareRootAndRemainder {
        return SqareRootAndRemainder(
            BigInteger(arithmetic.sqrt(magnitude).first, this.sign),
            BigInteger(arithmetic.sqrt(magnitude).second, this.sign)
        )
    }

    fun gcd(other: BigInteger): BigInteger {
        return BigInteger(arithmetic.gcd(this.magnitude, other.magnitude), Sign.POSITIVE)
    }

    private fun naiveGcd(other: BigInteger): BigInteger {
        var u = this
        var v = other
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
    }

    fun modInverse(modulo: BigInteger): BigInteger {
        if (gcd(modulo) != ONE) {
            throw ArithmeticException("BigInteger is not invertible. This and modulus are not relatively prime (coprime)")
        }
        var u = ONE
        var w = ZERO
        var b = this
        var c = modulo
        while (c != ZERO) {
            val (q, r) = b divrem c
            b = c
            c = r
            val tmpU = u
            u = w
            w = tmpU - q * w
        }
        return u
    }

    /**
     * Returns an always positive remainder of division operation
     */
    infix fun mod(modulo: BigInteger): BigInteger {
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
        val result = arithmetic.compare(this.magnitude, other.magnitude)
        return if (this.sign == Sign.NEGATIVE && other.sign == Sign.NEGATIVE) {
            result * -1
        } else {
            result
        }
    }

    override fun isZero(): Boolean {
        return this.sign == Sign.ZERO ||
            chosenArithmetic.compare(this.magnitude, chosenArithmetic.ZERO) == 0
    }

    override fun negate(): BigInteger {
        return BigInteger(wordArray = this.magnitude, requestedSign = sign.not())
    }

    override fun abs(): BigInteger {
        return BigInteger(wordArray = this.magnitude, requestedSign = Sign.POSITIVE)
    }

    fun factorial(): BigInteger {
        var result = ONE
        var element = ONE
        val abs = this.abs()
        while (element <= abs) {
            result *= element
            element = element.inc()
        }
        return if (this.isNegative) {
            -result
        } else {
            result
        }
    }

    fun pow(exponent: BigInteger): BigInteger {
        if (exponent < ZERO)
            throw ArithmeticException("Negative exponent not supported with BigInteger")

        if (exponent <= Long.MAX_VALUE) {
            return pow(exponent.magnitude[0].toLong())
        }

        return exponentiationBySquaring(ONE, this, exponent)
    }

    private tailrec fun exponentiationBySquaring(y: BigInteger, x: BigInteger, n: BigInteger): BigInteger {
        return when {
            n == ZERO -> y
            n == ONE -> x * y
            n.mod(TWO) == ZERO -> exponentiationBySquaring(y, x * x, n / 2)
            else -> exponentiationBySquaring(x * y, x * x, (n - 1) / 2)
        }
    }

    override fun pow(exponent: Long): BigInteger {
        if (exponent < 0) {
            throw ArithmeticException("Negative exponent not supported with BigInteger")
        }
        return when (this) {
            ZERO -> ZERO
            ONE -> ONE
            else -> {
                val sign = if (sign == Sign.NEGATIVE) {
                    if (exponent % 2 == 0L) {
                        Sign.POSITIVE
                    } else {
                        Sign.NEGATIVE
                    }
                } else {
                    Sign.POSITIVE
                }
                BigInteger(arithmetic.pow(magnitude, exponent), sign)
            }
        }
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
        if (isZero()) {
            return 1
        }
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
        val result = BigInteger(arithmetic.shiftRight(this.magnitude, places), sign)
        if (result.magnitude == arithmetic.ZERO) {
            return ZERO
        }
        return result
    }

    override operator fun unaryMinus(): BigInteger = negate()

    override fun secureOverwrite() {
        for (i in 0 until magnitude.size) {
            magnitude[i] = 0U
        }
    }

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
        val resultMagnitude = arithmetic.xor(this.magnitude, other.magnitude)
        val resultSign = when {
            this.isNegative xor other.isNegative -> Sign.NEGATIVE
            isResultZero(resultMagnitude) -> Sign.ZERO
            else -> Sign.POSITIVE
        }
        return BigInteger(resultMagnitude, resultSign)
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
        if (other is Number) {
            if (RuntimePlatform.currentPlatform() == Platform.JS) {
                return javascriptNumberComparison(other)
            }
        }
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
            is Float -> compareFloatAndBigInt(other) { compare(it) }
            is Double -> compareDoubleAndBigInt(other) { compare(it) }
            else -> throw RuntimeException("Invalid comparison type for BigInteger: ${other::class}")
        }
    }

    /**
     * Javascrpt doesn't have different types for float, integer, long, it's all just "number", so we need
     * to check if it's a decimal or integer number before comparing.
     */
    private fun javascriptNumberComparison(number: Number): Int {
        val double = number.toDouble()
        return when {
            double > Long.MAX_VALUE -> { compare(parseString(double.toString())) } // This whole block can be removed after 1.6.20 and https://github.com/JetBrains/kotlin/pull/4364
            double % 1 == 0.0 -> compare(fromLong(number.toLong()))
            else -> compareFloatAndBigInt(number.toFloat()) { compare(it) }
        }
    }

    fun compareFloatAndBigInt(float: Float, comparisonBlock: (BigInteger) -> Int): Int {
        val withoutDecimalPart = floor(float)
        val hasDecimalPart = (float % 1 != 0f)
        return if (hasDecimalPart) {
            val comparisonResult = comparisonBlock.invoke(tryFromFloat(withoutDecimalPart + 1))
            if (comparisonResult == 0) {
                // They were equal with float incremented by one (because of decimal part) so the BigInt was larger
                1
            } else {
                comparisonResult
            }
        } else {
            comparisonBlock.invoke(tryFromFloat(withoutDecimalPart))
        }
    }

    fun compareDoubleAndBigInt(double: Double, comparisonBlock: (BigInteger) -> Int): Int {
        val withoutDecimalPart = floor(double)
        val hasDecimalPart = (double % 1 != 0.0)
        return if (hasDecimalPart) {
            val comparisonResult = comparisonBlock.invoke(tryFromDouble(withoutDecimalPart + 1))
            if (comparisonResult == 0) {
                // They were equal with double incremented by one (because of decimal part) so the BigInt was larger
                1
            } else {
                comparisonResult
            }
        } else {
            comparisonBlock.invoke(tryFromDouble(withoutDecimalPart))
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
        return magnitude.fold(0) { acc, uLong -> acc + uLong.hashCode() } + sign.hashCode()
    }

    override fun toString(): String {
        // TODO think about limiting the size of string, and offering a stream of characters instead of huge strings
//        if (stringRepresentation == null) {
//            stringRepresentation = toString(10)
//        }
//        return stringRepresentation!!

        // Linux build complains about mutating a frozen object, let's try without this representation caching
        return toString(10)
    }

    override fun toString(base: Int): String {
        val sign = if (sign == Sign.NEGATIVE) {
            "-"
        } else {
            ""
        }
        return sign + toStringWithoutSign(base)
    }

    internal fun toStringWithoutSign(base: Int): String {
        return arithmetic.toString(this.magnitude, base)
    }

    data class QuotientAndRemainder(val quotient: BigInteger, val remainder: BigInteger)

    data class SqareRootAndRemainder(val squareRoot: BigInteger, val remainder: BigInteger)

    // TODO eh
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

    fun toModularBigInteger(modulo: BigInteger): ModularBigInteger {
        val creator = ModularBigInteger.creatorForModulo(modulo)
        return creator.fromBigInteger(this)
    }

    override fun intValue(exactRequired: Boolean): Int {
        if (exactRequired && (this > Int.MAX_VALUE || this < Int.MIN_VALUE)) {
            throw ArithmeticException("Cannot convert to int and provide exact value")
        }
        return magnitude[0].toInt() * signum()
    }

    override fun longValue(exactRequired: Boolean): Long {
        if (exactRequired && (this > Long.MAX_VALUE || this < Long.MIN_VALUE)) {
            throw ArithmeticException("Cannot convert to long and provide exact value")
        }
        return if (magnitude.size > 1) {
            val firstBit = magnitude[1] shl 63
            (magnitude[0].toLong() or firstBit.toLong()) * signum()
        } else {
            return magnitude[0].toLong() * signum()
        }
    }

    override fun byteValue(exactRequired: Boolean): Byte {
        if (exactRequired && (this > Byte.MAX_VALUE || this < Byte.MIN_VALUE)) {
            throw ArithmeticException("Cannot convert to byte and provide exact value")
        }
        return (magnitude[0].toByte() * signum()).toByte()
    }

    override fun shortValue(exactRequired: Boolean): Short {
        if (exactRequired && (this > Short.MAX_VALUE || this < Short.MIN_VALUE)) {
            throw ArithmeticException("Cannot convert to short and provide exact value")
        }
        return (magnitude[0].toShort() * signum()).toShort()
    }

    override fun uintValue(exactRequired: Boolean): UInt {
        if (exactRequired && (this > UInt.MAX_VALUE || isNegative)) {
            throw ArithmeticException("Cannot convert to unsigned int and provide exact value")
        }
        return magnitude[0].toUInt()
    }

    override fun ulongValue(exactRequired: Boolean): ULong {
        if (exactRequired && (this > ULong.MAX_VALUE || isNegative)) {
            throw ArithmeticException("Cannot convert to unsigned long and provide exact value")
        }
        return if (magnitude.size > 1) {
            val firstBit = magnitude[1] shl 63
            magnitude[0] or firstBit
        } else {
            return magnitude[0]
        }
    }

    override fun ubyteValue(exactRequired: Boolean): UByte {
        if (exactRequired && (this > UByte.MAX_VALUE.toUInt() || isNegative)) {
            throw ArithmeticException("Cannot convert to unsigned byte and provide exact value")
        }
        return magnitude[0].toUByte()
    }

    override fun ushortValue(exactRequired: Boolean): UShort {
        if (exactRequired && this > UShort.MAX_VALUE.toUInt() || isNegative) {
            throw ArithmeticException("Cannot convert to unsigned short and provide exact value")
        }
        return magnitude[0].toUShort()
    }

    override fun floatValue(exactRequired: Boolean): Float {
        if (exactRequired && this.abs() > Float.MAX_VALUE) {
            throw ArithmeticException("Cannot convert to float and provide exact value")
        }
        return this.toString().toFloat()
    }

    override fun doubleValue(exactRequired: Boolean): Double {
        if (exactRequired && this.abs() > Double.MAX_VALUE) {
            println(this.abs())
            println(Double.MAX_VALUE)
            if (this.abs() > Double.MAX_VALUE) {
                println("huh")
            }
            throw ArithmeticException("Cannot convert to double and provide exact value")
        }
        return this.toString().toDouble()
    }

    override fun toUByteArray(): UByteArray {
        return arithmetic.toUByteArray(magnitude)
    }

    override fun toByteArray(): ByteArray {
        return arithmetic.toByteArray(magnitude)
    }

    operator fun rangeTo(other: BigInteger) = BigIntegerRange(this, other)

    class BigIntegerRange(override val start: BigInteger, override val endInclusive: BigInteger) :
        ClosedRange<BigInteger>, Iterable<BigInteger> {

        override fun iterator(): Iterator<BigInteger> {
            return BigIntegerIterator(start, endInclusive)
        }
    }

    class BigIntegerIterator(start: BigInteger, private val endInclusive: BigInteger) : Iterator<BigInteger> {

        private var current = start

        override fun hasNext(): Boolean {
            return current <= endInclusive
        }

        override fun next(): BigInteger {
            return current++
        }
    }
}
