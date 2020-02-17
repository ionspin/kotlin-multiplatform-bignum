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

package com.ionspin.kotlin.bignum.decimal

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.CommonBigNumberOperations
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.ComparisonWorkaround
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.math.absoluteValue
import kotlin.math.max

/**
 * Implementation of floating-point arbitrary precision arithmetic.
 *
 * Each object of this class represents an immutable large floating point number. The underlying implementation
 * uses [BigInteger] to represent sign and significand, and another [BigInteger] to represent the exponent.
 *
 * [DecimalMode] defines the precision and desired [RoundingMode] when using instances of this class in arithmetic operations,
 * but if [DecimalMode] is supplied to the operation it will override instances [DecimalMode]
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

@ExperimentalUnsignedTypes
class BigDecimal private constructor(
    val significand: BigInteger,
    val exponent: Long = 0L,
    val decimalMode: DecimalMode? = null
) : BigNumber<BigDecimal>,
    CommonBigNumberOperations<BigDecimal>,
    Comparable<Any> {

    val precision = significand.numberOfDecimalDigits()

    companion object : BigNumber.Creator<BigDecimal> {
        override val ZERO = BigDecimal(BigInteger.ZERO)
        override val ONE = BigDecimal(BigInteger.ONE)
        override val TWO = BigDecimal(BigInteger.TWO)
        override val TEN = BigDecimal(BigInteger.TEN)

        var useToStringExpanded: Boolean = false

        private fun roundOrDont(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            return if (decimalMode.decimalPrecision != 0L && decimalMode.roundingMode != RoundingMode.NONE) {
                roundSignificand(significand, exponent, decimalMode)
            } else {
                BigDecimal(significand, exponent)
            }
        }

        /**
         * Use this rounding when part of number needs to be discarded because
         * the precision is narrowing or extended because precision is increasing. Discarded parameter
         * influences the least significant digit of the result
         */
        @Suppress("UNUSED_EXPRESSION")
        private fun roundDiscarded(
            significand: BigInteger,
            discarded: BigInteger,
            decimalMode: DecimalMode
        ): BigInteger {

            val toDiscard = significand.numberOfDecimalDigits() - decimalMode.decimalPrecision
            var result = if (toDiscard > 0) {
                (significand divrem BigInteger.TEN.pow(toDiscard)).quotient
            } else {
                significand
            }
            val sign = if (significand == BigInteger.ZERO) {
                discarded.sign
            } else {
                significand.sign
            }
            val significantRemainderDigit = if (toDiscard > 0) {
                if (discarded == BigInteger.ZERO) {
                    BigInteger.ZERO
                } else {
                    (discarded / (discarded.numberOfDecimalDigits())).abs() +
                        (significand divrem BigInteger.TEN.pow(toDiscard)).remainder * BigInteger.TEN.pow(
                        toDiscard
                    )
                }
            } else {
                if (discarded == BigInteger.ZERO) {
                    BigInteger.ZERO
                } else {
                    (discarded / (BigInteger.TEN.pow(discarded.numberOfDecimalDigits() - 1))).abs()
                }
            }
            when (decimalMode.roundingMode) {
                RoundingMode.AWAY_FROM_ZERO -> {
                    if (sign == Sign.POSITIVE) {
                        result++
                    } else {
                        result--
                    }
                }
                RoundingMode.TOWARDS_ZERO -> {
                    result
                }
                RoundingMode.CEILING -> {
                    if (sign == Sign.POSITIVE) {
                        result++
                    } else {
                        result
                    }
                }
                RoundingMode.FLOOR -> {
                    if (sign == Sign.POSITIVE) {
                        result
                    } else {
                        result--
                    }
                }
                RoundingMode.ROUND_HALF_AWAY_FROM_ZERO -> {
                    when (sign) {
                        Sign.POSITIVE -> {
                            if (significantRemainderDigit >= 5) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (significantRemainderDigit >= 5) {
                                result--
                            }
                        }
                        Sign.ZERO -> {
                        }
                    }
                }
                RoundingMode.ROUND_HALF_TOWARDS_ZERO -> {
                    when (sign) {
                        Sign.POSITIVE -> {
                            if (significantRemainderDigit > 5) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (significantRemainderDigit > 5) {
                                result--
                            }
                        }
                        Sign.ZERO -> {
                        }
                    }
                }
                RoundingMode.ROUND_HALF_CEILING -> {
                    when (sign) {
                        Sign.POSITIVE -> {
                            if (significantRemainderDigit >= 5) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (significantRemainderDigit > 5) {
                                result--
                            }
                        }
                        Sign.ZERO -> {
                        }
                    }
                }
                RoundingMode.ROUND_HALF_FLOOR -> {
                    when (sign) {
                        Sign.POSITIVE -> {
                            if (significantRemainderDigit > 5) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (significantRemainderDigit >= 5) {
                                result--
                            }
                        }
                        Sign.ZERO -> {
                        }
                    }
                }
//                        RoundingMode.ROUND_HALF_TO_EVEN -> {
//                        }
//                        RoundingMode.ROUND_HALF_TO_ODD -> {
//                        }

                RoundingMode.NONE -> {
                    throw ArithmeticException("Non-terminating result of division operation. Specify decimalPrecision")
                }
            }
            return result
        }

        private fun roundSignificand(bigDecimal: BigDecimal, decimalMode: DecimalMode): BigDecimal {
            return roundSignificand(bigDecimal.significand, bigDecimal.exponent, decimalMode)
        }

        private fun roundSignificand(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            if (significand == BigInteger.ZERO) {
                return BigDecimal(BigInteger.ZERO, exponent, decimalMode)
            }
            val significandDigits = significand.numberOfDecimalDigits()
            val desiredPrecision = decimalMode.decimalPrecision
            return when {
                desiredPrecision > significandDigits -> {
                    val extendedSignificand = significand * BigInteger.TEN.pow(desiredPrecision - significandDigits)
                    BigDecimal(extendedSignificand, exponent, decimalMode)
                }
                desiredPrecision < significandDigits -> {
                    val divRem = significand divrem BigInteger.TEN.pow(significandDigits - desiredPrecision)
                    val resolvedRemainder = if (divRem.remainder.numberOfDecimalDigits() < significandDigits - desiredPrecision) {
                        BigInteger.ZERO
                    } else {
                        divRem.remainder
                    }
                    val newSignificand = roundDiscarded(divRem.quotient, resolvedRemainder, decimalMode)
                    BigDecimal(newSignificand, exponent, decimalMode)
                }
                else -> {
                    BigDecimal(significand, exponent, decimalMode)
                }
            }
        }

        /**
         * Convert a Long into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        fun fromBigInteger(bigInteger: BigInteger, decimalMode: DecimalMode? = null): BigDecimal {
            return BigDecimal(bigInteger, bigInteger.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(
                decimalMode
            )
        }

        /**
         * Convert a Long into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        fun fromLong(long: Long, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Long into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        fun fromULong(uLong: ULong, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromULong(uLong)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Int into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        fun fromInt(int: Int, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Int into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        fun fromUInt(uInt: UInt, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromUInt(uInt)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Short into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromUShort(uShort: UShort, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromUShort(uShort)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Short into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromShort(short: Short, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Byte into a BigDecimal.
         *
         * i.e. 11 -> 1.1E+2
         *
         * @param byte Byte value to conver
         * @return BigDecimal representing input
         */
        fun fromUByte(uByte: UByte, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromUByte(uByte)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Byte into a BigDecimal.
         *
         * i.e. 11 -> 1.1E+2
         *
         * @param byte Byte value to conver
         * @return BigDecimal representing input
         */
        fun fromByte(byte: Byte, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, bigint.numberOfDecimalDigits() - 1, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a Long into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        fun fromLongAsSignificand(long: Long, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromLong(long), 0L, decimalMode).roundSignificand(decimalMode)

        /**
         * Convert a Int into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        fun fromIntAsSignificand(int: Int, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromInt(int), 0L, decimalMode).roundSignificand(decimalMode)

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromShortAsSignificand(short: Short, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromShort(short), 0L, decimalMode).roundSignificand(decimalMode)

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromByteAsSignificand(byte: Byte, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromByte(byte), 0L, decimalMode).roundSignificand(decimalMode)

        /**
         * Convert a float into a BigDecimal
         *
         * i.e. 71.11 -> 7.111E+2
         *
         * @param float Float value to conver
         * @return BigDecimal representing input
         */
        fun fromFloat(float: Float, decimalMode: DecimalMode? = null): BigDecimal {
            val floatString = float.toString()
            return if (floatString.contains('.')) {
                parseStringWithMode(floatString.dropLastWhile { it == '0' }, decimalMode)
            } else {
                parseStringWithMode(floatString, decimalMode)
            }
        }

        /**
         * Convert a Double into a BigDecimal
         *
         * i.e. 71.11 -> 7.111E+2
         *
         * @param double Double value to conver
         * @return BigDecimal representing input
         */
        fun fromDouble(double: Double, decimalMode: DecimalMode? = null): BigDecimal {
            val doubleString = double.toString()
            return if (doubleString.contains('.')) {
                parseStringWithMode(doubleString.dropLastWhile { it == '0' }, decimalMode)
            } else {
                parseStringWithMode(doubleString, decimalMode)
            }
        }

        /**
         * Create BigDecimal from BigInteger significand and  exponent
         */
        fun fromBigIntegerWithExponent(
            bigInteger: BigInteger,
            exponent: Long,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            return BigDecimal(bigInteger, exponent, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Create BigDecimal from Long significand and BigInteger exponent
         */
        fun fromLongWithExponent(
            long: Long,
            exponent: Long,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, exponent, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Create BigDecimal from Int significand and BigInteger exponent
         */
        fun fromIntWithExponent(int: Int, exponent: Long, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, exponent, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Create BigDecimal from Short significand and BigInteger exponent
         */
        fun fromShortWithExponent(
            short: Short,
            exponent: Long,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, exponent, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Create BigDecimal from Short significand and BigInteger exponent
         */
        fun fromByteWithExponent(
            byte: Byte,
            exponent: Long,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, exponent, decimalMode).roundSignificand(decimalMode)
        }

        /**
         * Convert a BigInteger into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        override fun fromBigInteger(bigInteger: BigInteger): BigDecimal {
            return fromBigInteger(bigInteger, null)
        }

        /**
         * Convert a Long into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        override fun fromULong(uLong: ULong): BigDecimal {
            return fromULong(uLong, null)
        }

        /**
         * Convert a Int into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        override fun fromUInt(uInt: UInt): BigDecimal {
            return fromUInt(uInt, null)
        }

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        override fun fromUShort(uShort: UShort): BigDecimal {
            return fromUShort(uShort, null)
        }

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        override fun fromUByte(uByte: UByte): BigDecimal {
            return fromUByte(uByte, null)
        }

        /**
         * Convert a Long into a BigDecimal
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param long Long value to conver
         * @return BigDecimal representing input
         */
        override fun fromLong(long: Long): BigDecimal {
            return fromLong(long, null)
        }

        /**
         * Convert a Int into a BigDecimal
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        override fun fromInt(int: Int): BigDecimal {
            return fromInt(int, null)
        }

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        override fun fromShort(short: Short): BigDecimal {
            return fromShort(short, null)
        }

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        override fun fromByte(byte: Byte): BigDecimal {
            return fromByte(byte, null)
        }

        override fun tryFromFloat(float: Float, exactRequired: Boolean): BigDecimal {
            return fromFloat(float, null)
        }

        override fun tryFromDouble(double: Double, exactRequired: Boolean): BigDecimal {
            return fromDouble(double, null)
        }

        override fun parseString(string: String, base: Int): BigDecimal {
            return parseStringWithMode(string, null)
        }

        /**
         * Parse BigDecimal from a supplied string. The string can be in either of two different formats:
         *
         * * **Scientific** i.e. 1.234E-9
         *
         * or
         *
         * * **Expanded** ie 0.000000001234
         *
         * @return Parsed string
         * @throws ArithmeticException if parsing fails
         */
        fun parseStringWithMode(floatingPointString: String, decimalMode: DecimalMode? = null): BigDecimal {
            if (floatingPointString.isEmpty()) {
                return ZERO
            }
            if (floatingPointString.contains('E') || floatingPointString.contains('e')) {
                // Sci notation
                val split = floatingPointString.split('.')
                when (split.size) {
                    2 -> {
                        val signPresent = (floatingPointString[0] == '-' || floatingPointString[0] == '+')
                        val leftStart = if (signPresent) {
                            1
                        } else {
                            0
                        }
                        var sign = if (signPresent) {
                            if (floatingPointString[0] == '-') {
                                Sign.NEGATIVE
                            } else {
                                Sign.POSITIVE
                            }
                        } else {
                            Sign.POSITIVE
                        }
                        val left = split[0].substring(startIndex = leftStart)
                        val rightSplit = split[1].split('E', 'e')
                        val right = rightSplit[0]
                        val exponentSplit = rightSplit[1]
                        val exponentSignPresent = (exponentSplit[0] == '-' || exponentSplit[0] == '+')
                        val exponentSign = if (exponentSplit[0] == '-') {
                            Sign.NEGATIVE
                        } else {
                            Sign.POSITIVE
                        }
                        val skipSignIfPresent = if (exponentSignPresent) {
                            1
                        } else {
                            0
                        }
                        val exponentString = exponentSplit.substring(startIndex = skipSignIfPresent)
                        val exponent = if (exponentSign == Sign.POSITIVE) {
                            exponentString.toLong(10)
                        } else {
                            exponentString.toLong(10) * -1
                        }

                        var leftFirstNonZero = left.indexOfFirst { it != '0' }

                        if (leftFirstNonZero == -1) {
                            leftFirstNonZero = 0
                        }

                        var rightLastNonZero = right.indexOfLast { it != '0' }

                        if (rightLastNonZero == -1) {
                            rightLastNonZero = right.length - 1
                        }
                        val leftTruncated = left.substring(leftFirstNonZero, left.length)
                        val rightTruncated = right.substring(0, rightLastNonZero + 1)
                        var significand = BigInteger.parseString(leftTruncated + rightTruncated, 10)

                        if (significand == BigInteger.ZERO) {
                            sign = Sign.ZERO
                        }
                        if (sign == Sign.NEGATIVE) {
                            significand = significand.negate()
                        }
                        return BigDecimal(significand, exponent, decimalMode)
                    }
                    else -> throw ArithmeticException("Invalid (or unsupported) floating point number format: $floatingPointString")
                }
            } else {
                // Expanded notation
                if (floatingPointString.contains('.')) {
                    val split = floatingPointString.split('.')
                    when (split.size) {
                        2 -> {
                            val signPresent = (floatingPointString[0] == '-' || floatingPointString[0] == '+')
                            val leftStart = if (signPresent) {
                                1
                            } else {
                                0
                            }
                            var sign = if (signPresent) {
                                if (floatingPointString[0] == '-') {
                                    Sign.NEGATIVE
                                } else {
                                    Sign.POSITIVE
                                }
                            } else {
                                Sign.POSITIVE
                            }
                            val left = split[0].substring(startIndex = leftStart)
                            val right = split[1]
                            var leftFirstNonZero = left.indexOfFirst { it != '0' }

                            if (leftFirstNonZero == -1) {
                                leftFirstNonZero = 0
                            }

                            var rightLastNonZero = right.indexOfLast { it != '0' }

                            if (rightLastNonZero == -1) {
                                rightLastNonZero = right.length - 1
                            }
                            val leftTruncated = left.substring(leftFirstNonZero, left.length)
                            val rightTruncated = right.substring(0, rightLastNonZero + 1)
                            var significand = BigInteger.parseString(leftTruncated + rightTruncated, 10)
                            var exponent = if (leftTruncated.length >= 1 && leftTruncated[0] != '0') {
                                leftTruncated.length - 1
                            } else {
                                (rightTruncated.indexOfFirst { it != '0' } + 1) * -1
                            }

                            if (significand == BigInteger.ZERO) {
                                sign = Sign.ZERO
                            }
                            if (sign == Sign.NEGATIVE) {
                                significand = significand.negate()
                            }
                            return BigDecimal(significand, exponent.toLong(), decimalMode)
                        }
                        else -> throw ArithmeticException("Invalid (or unsupported) floating point number format: $floatingPointString")
                    }
                } else {
                    val significand = BigInteger.parseString(floatingPointString, 10)
                    return BigDecimal(
                        significand,
                        significand.numberOfDecimalDigits() - 1,
                        decimalMode
                    )
                }
            }
        }

        private fun resolveDecimalMode(
            firstDecimalMode: DecimalMode?,
            secondDecimalMode: DecimalMode?,
            suppliedDecimalMode: DecimalMode?
        ): DecimalMode {
            return if (suppliedDecimalMode != null) {
                suppliedDecimalMode
            } else {
                if (firstDecimalMode == null && secondDecimalMode == null) {
                    return DecimalMode()
                }
                if (firstDecimalMode == null && secondDecimalMode != null) {
                    return secondDecimalMode
                }
                if (secondDecimalMode == null && firstDecimalMode != null) {
                    return firstDecimalMode
                }
                if (firstDecimalMode!!.roundingMode != secondDecimalMode!!.roundingMode) {
                    throw ArithmeticException("Different rounding modes! This: ${firstDecimalMode.roundingMode} Other: ${secondDecimalMode.roundingMode}")
                }
                val unifiedDecimalMode = if (firstDecimalMode.decimalPrecision >= secondDecimalMode.decimalPrecision) {
                    firstDecimalMode
                } else {
                    secondDecimalMode
                }
                unifiedDecimalMode
            }
        }
    }

    override fun getCreator(): BigNumber.Creator<BigDecimal> = BigDecimal
    override fun getInstance(): BigDecimal = this

    /**
     * Add two BigDecimal and return result in a new instance of BigDecimal. Default decimal mode will be used.
     *
     * @param other BigDecimal (addend)
     * @return BigDecimal containing result of the operation
     */
    override fun add(other: BigDecimal): BigDecimal {
        return add(other, null)
    }

    /**
     * Add two BigDecimal and return result in a new instance of BigDecimal.
     *
     * @param other BigDecimal (addend)
     * @return BigDecimal containing result of the operation
     */
    fun add(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second, _) = bringSignificandToSameExponent(this, other)
        // Temporary way to detect a carry happened, proper solution is to add
        // methods that return information about carry in arithmetic classes, this way it's going
        // to be rather slow
        val firstNumOfDigits = first.numberOfDecimalDigits()
        val secondNumOfDigits = second.numberOfDecimalDigits()
        val newSignificand = first + second
        val newSignificandNumOfDigit = newSignificand.numberOfDecimalDigits()
        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) {
            firstNumOfDigits
        } else {
            secondNumOfDigits
        }
        val carryDetected = newSignificandNumOfDigit - largerOperand
        val newExponent = max(this.exponent, other.exponent) + carryDetected

        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    /**
     * Subtract two BigDecimal and return result in a new instance of BigDecimal.
     * Default decimal mode will be used
     *
     * @param other BigDecimal (subtrahend)
     * @return BigDecimal containing result of the operation
     */
    override fun subtract(other: BigDecimal): BigDecimal {
        return subtract(other, null)
    }

    /**
     * Subtract two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (subtrahend)
     * @return BigDecimal containing result of the operation
     */
    fun subtract(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second, _) = bringSignificandToSameExponent(this, other)

        val firstNumOfDigits = first.numberOfDecimalDigits()
        val secondNumOfDigits = second.numberOfDecimalDigits()

        val newSignificand = first - second

        val newSignificandNumOfDigit = newSignificand.numberOfDecimalDigits()

        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) {
            firstNumOfDigits
        } else {
            secondNumOfDigits
        }
        val borrowDetected = newSignificandNumOfDigit - largerOperand

        val newExponent = max(this.exponent, other.exponent) + borrowDetected
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    /**
     * Multiply two BigDecimal and return result in a new instance of BigDecimal
     * Default decimal mode will be used
     * @param other BigDecimal (multiplicand)
     * @return BigDecimal containing result of the operation
     */
    override fun multiply(other: BigDecimal): BigDecimal {
        return multiply(other, null)
    }

    /**
     * Multiply two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (multiplicand)
     * @return BigDecimal containing result of the operation
     */
    fun multiply(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        // Temporary way to detect a carry happened, proper solution is to add
        // methods that return information about carry in arithmetic classes, this way it's going
        // to be rather slow
        val firstNumOfDigits = this.significand.numberOfDecimalDigits()
        val secondNumOfDigits = other.significand.numberOfDecimalDigits()

        val newSignificand = this.significand * other.significand

        val newSignificandNumOfDigit = newSignificand.numberOfDecimalDigits()
        val moveExponent = newSignificandNumOfDigit - (firstNumOfDigits + secondNumOfDigits)

        val newExponent = this.exponent + other.exponent + moveExponent + 1
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    override fun divide(other: BigDecimal): BigDecimal {
        return divide(other, null)
    }

    /**
     * Divide two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (divisor)
     * @return BigDecimal containing result of the operation
     */
    fun divide(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        var newExponent = this.exponent - other.exponent - 1

        val desiredPrecision = if (resolvedDecimalMode.decimalPrecision == 0L) {
            val precisionSum = max(6, this.precision + other.precision)
            if (precisionSum < this.precision) {
                Long.MAX_VALUE
            } else {
                precisionSum
            }
        } else {
            resolvedDecimalMode.decimalPrecision
        }
        val power = desiredPrecision - this.precision + other.precision
        val thisPrepared = when {
            power > 0 -> this.significand * 10.toBigInteger().pow(power)
            power < 0 -> this.significand / 10.toBigInteger().pow(power.absoluteValue)
            else -> this.significand
        }

        var divRem = thisPrepared divrem other.significand
        var result = divRem.quotient
        if (result == BigInteger.ZERO) {
            newExponent--
        }
        val exponentModifier = result.numberOfDecimalDigits() - resolvedDecimalMode.decimalPrecision
        if (divRem.remainder != BigInteger.ZERO && resolvedDecimalMode.decimalPrecision == 0L && resolvedDecimalMode.roundingMode == RoundingMode.NONE) {
            throw ArithmeticException("Non-terminating result of division operation. Specify decimalPrecision")
        }
        return BigDecimal(
            roundDiscarded(result, divRem.remainder, resolvedDecimalMode),
            newExponent + exponentModifier,
            resolvedDecimalMode
        )
    }

    /**
     * Remainder of **integer** division on this bigDecimal. If there is no rounding mode defined in decimal mode
     * #RoundingMode.FLOOR will be used
     */
    override fun remainder(other: BigDecimal): BigDecimal {
        return divideAndRemainder(other).second
    }

    /**
     * Quotient and remainder of **integer** division on this bigDecimal. If there is no rounding mode defined in decimal mode
     * #RoundingMode.FLOOR will be used
     */
    override fun divideAndRemainder(other: BigDecimal): Pair<BigDecimal, BigDecimal> {
        val resolvedRoundingMode = this.decimalMode ?: DecimalMode(exponent + 1, RoundingMode.FLOOR)
        val quotient = divide(other, resolvedRoundingMode)
        val quotientInfinitePrecision = quotient.copy(decimalMode = DecimalMode.DEFAULT)
        val remainder = this - (quotientInfinitePrecision * other)
        return Pair(quotient, remainder)
    }

    override fun isZero(): Boolean {
        return significand.isZero()
    }

    /**
     * Creates a copy of this BigDecimal with some different elements (significand/exponent/decimalMode)
     */
    fun copy(
        significand: BigInteger = this.significand,
        exponent: Long = this.exponent,
        decimalMode: DecimalMode? = this.decimalMode
    ): BigDecimal {
        return BigDecimal(significand, exponent, decimalMode)
    }

    /**
     * Moves the decimal point by creating a new instance with a different exponent. Positive values move
     * the decimal point to the right, negative values move the decimal point to the left.
     */
    fun moveDecimalPoint(places: Int): BigDecimal {
        return copy(exponent = exponent + places)
    }

    // TODO in 0.3.0
//    override fun pow(exponent: BigDecimal): BigDecimal {
//        if (exponent.signum() < 0) {
//            throw RuntimeException("BigDecimal exponentiation with negative numbers is not supported. Exponent: ${exponent}")
//        }
//        //TODO Naive implementation
//        var result = ONE
//        for (i in 1.toBigInteger() .. exponent.toBigInteger()) {
//            result = result * this
//        }
//        val remainder = exponent.subtract(exponent.floor(), DecimalMode.DEFAULT)
//        return result
//
//    }
    /**
     * Exponentiate this BigDecimal by some exponent
     */
    override fun pow(exponent: Int): BigDecimal {
        return pow(exponent.toLong())
    }

    // TODO in 0.3.0
//    /**
//     * Natural logarithm of this BigDecimal
//     */
//    fun log() {
//
//    }

//    /**
//     * Arithmetic-geometric mean
//     */
//    fun agm(other : BigDecimal, decimalMode: DecimalMode? = null) : BigDecimal {
//        var a = this
//        var g = other
//        var nextA : BigDecimal
//        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
//        if (resolvedDecimalMode.decimalPrecision == 0L) {
//            throw ArithmeticException("Agm not available with infinite precision")
//        }
//        while (true) {
//            nextA = (a + g) / TWO
//            if ((a - g).abs().precision <= resolvedDecimalMode.decimalPrecision) {
//                return nextA
//            } else {
//                g = (a * g).sqrt()
//                a = nextA
//            }
//
//        }
//    }
//
//    fun sqrt() : BigDecimal {
//        val root = significand.sqrt()
//        val exponent =
//    }
    /**
     * Return the this truncated by using floor rounding
     */
    fun floor(): BigDecimal {
        return roundSignificand(DecimalMode(exponent + 1, RoundingMode.FLOOR))
    }

    /**
     * Return the this truncated by using ceil rounding
     */
    fun ceil(): BigDecimal {
        return roundSignificand(DecimalMode(exponent + 1, RoundingMode.CEILING))
    }

    /**
     * Convert to big integer by truncating
     */
    fun toBigInteger(): BigInteger {
        if (exponent < 0) {
            return BigInteger.ZERO
        }
        val precisionExponentDiff = exponent - precision
        return when {
            precisionExponentDiff > 0 -> {
                significand * 10.toBigInteger().pow(precisionExponentDiff + 1)
            }
            precisionExponentDiff < 0 -> {
                significand / 10.toBigInteger().pow(precisionExponentDiff.absoluteValue - 1)
            }
            else -> {
                significand
            }
        }
    }

    /**
     * Returns the number of digits representing this number
     * i.e.
     * 12.345 will return 5
     * 0.001 will return 4
     * 123000 will return 6
     */
    override fun numberOfDecimalDigits(): Long {
        val numberOfDigits = when {
            exponent > 0 && exponent < precision -> precision
            exponent > 0 && exponent > precision -> exponent + 1 // Significand is already 10^1 when exponent is > 0
            exponent > 0 && exponent == precision -> precision + 1 // Same as above
            exponent < 0 -> exponent.absoluteValue + precision
            exponent == 0L -> significand.toString().dropLastWhile { it == '0' }.length.toLong() // I.e. precision is 3, exponent is 0,
            // but significand is 100, which is equivalent to 1, so the result should be 1
            // TODO find a better way than this lazy conversion to string
            else -> throw RuntimeException("Invalid case when getting number of decimal digits")
        }
        return numberOfDigits
    }

    override fun toString(base: Int): String {
        if (base != 10) {
            throw RuntimeException("BigDecimal in base other than 10 is not supported yet")
        }
        return toString()
    }

    // TODO
    private fun integerDiv(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
//        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand / other.significand
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    private fun rem(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
//        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand % other.significand
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    // TODO
    private fun divrem(other: BigDecimal, decimalMode: DecimalMode? = null): Pair<BigDecimal, BigDecimal> {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val newExponent = max(this.exponent, other.exponent)
        val newSignificand = this.significand / other.significand
        val newRemainderSignificand = this.significand % other.significand
        return Pair(
            roundOrDont(newSignificand, newExponent, resolvedDecimalMode),
            roundOrDont(newRemainderSignificand, newExponent, resolvedDecimalMode)
        )
    }

    infix fun divrem(other: BigDecimal): Pair<BigDecimal, BigDecimal> {
        return divideAndRemainder(other)
    }

    override operator fun plus(other: BigDecimal): BigDecimal {
        return this.add(other, null)
    }

    override operator fun minus(other: BigDecimal): BigDecimal {
        return this.subtract(other, null)
    }

    override operator fun times(other: BigDecimal): BigDecimal {
        return this.multiply(other, null)
    }

    override operator fun div(other: BigDecimal): BigDecimal {
        return this.divide(other, null)
    }

    override operator fun rem(other: BigDecimal): BigDecimal {
        return this.rem(other, null)
    }

    /**
     * Returns a new negated instance
     */
    override fun unaryMinus(): BigDecimal {
        return BigDecimal(significand.negate(), exponent)
    }

    /**
     * Incerement by one
     */
    fun inc(): BigDecimal {
        return this + 1
    }

    /**
     * Decrement by one
     */
    fun dec(): BigDecimal {
        return this - 1
    }

    /**
     * Return Absolute value
     */
    override fun abs(): BigDecimal {
        return BigDecimal(significand.abs(), exponent)
    }

    /**
     * Negate this BigDecimal
     */
    override fun negate(): BigDecimal {
        return BigDecimal(significand.negate(), exponent)
    }

    /**
     * Exponentiate this BigDecimal by some exponent
     */
    override fun pow(exponent: Long): BigDecimal {
        var result = this
        return when {
            exponent > 0 -> {
                for (i in 0 until exponent - 1) {
                    result = result * this
                }
                result
            }
            exponent < 0 -> {
                for (i in 0..exponent.absoluteValue) {
                    result = result / this
                }
                result
            }
            else -> {
                ONE
            }
        }
    }

    /**
     * Signum function
     * @return Result of signum function for this BigDecimal (-1 negative, 0 zero, 1 positive)
     */
    override fun signum(): Int = significand.signum()

    /**
     * Round using specific [DecimalMode] and return rounded instance. This is applied only to significand.
     *
     * I.e.:
     * 1.234E3 == 1234, rounded with DecimalMode(2, RoundingMode.CEILING) will return 1.3E3 == 1300
     *
     * 1.234E-3 == 0.001234, , rounded with DecimalMode(2, RoundingMode.CEILING) will return 1.3E-3 == 0.0013
     *
     */
    fun roundSignificand(decimalMode: DecimalMode?): BigDecimal {
        if (decimalMode == null) {
            return this
        }
        return Companion.roundSignificand(this.significand, this.exponent, decimalMode)
    }

    /**
     * Round the BigDecimal to a specific length. If position is set to 0, a ArithmeticException is thrown.
     *
     * I.e.
     *
     * 1234.5678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 1230
     * 123.456 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 123
     * 0.0012345678 digitPosition 4, rounding mode HALF_TOWARDS_ZERO will produce 0.001
     * 0.0012345678 digitPosition 6, rounding mode HALF_TOWARDS_ZERO will produce 0.00123
     */
    fun roundToDigitPosition(digitPosition: Long, roundingMode: RoundingMode): BigDecimal {
        if (digitPosition == 0L) {
            throw ArithmeticException("Rounding to 0 position is not supported")
        }
        return if (this.exponent >= 0) {
            roundSignificand(DecimalMode(digitPosition, roundingMode))
        } else {
            (this + this.signum()).roundSignificand(DecimalMode(digitPosition, roundingMode)) - this.signum()
        }
    }

    /**
     * Round the BigDecimal to a specific length AFTER the decimal point. If position is set to 0 a integer value is returned
     *
     * I.e.
     *
     * 1234.5678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 1234.568
     * 123.456 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 123.456
     * 0.0012345678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 0.001
     * 0.0012345678 digitPosition 5, rounding mode HALF_TOWARDS_ZERO will produce 0.00123
     */
    fun roundToDigitPositionAfterDecimalPoint(digitPosition: Long, roundingMode: RoundingMode): BigDecimal {
        if (digitPosition < 0) {
            throw ArithmeticException("This method doesn't support negative digit position")
        }
        // if (exponent > 0) {
        //     when {
        //         precision <= exponent -> return this
        //         precision > exponent ->
        //             return this.roundSignificand(DecimalMode(exponent + digitPosition, roundingMode))
        //         else -> return this
        //     }
        // }
        return when {
            exponent >= 0 -> roundToDigitPosition(exponent + digitPosition + 1, roundingMode)
            exponent < 0 -> roundToDigitPosition(digitPosition + 1, roundingMode)
            else -> throw RuntimeException("Unexpected state")
        }
    }

    private fun getRidOfRadix(bigDecimal: BigDecimal): BigDecimal {
        val precision = bigDecimal.significand.numberOfDecimalDigits()
        val newExponent = bigDecimal.exponent - precision + 1
        return BigDecimal(bigDecimal.significand, newExponent)
    }

    /**
     * Brings the two big decimals to same exponents, which makes it easier to apply other operations
     */
    private fun bringSignificandToSameExponent(
        first: BigDecimal,
        second: BigDecimal
    ): Triple<BigInteger, BigInteger, Long> {
        val firstPrepared = getRidOfRadix(first)
        val secondPrepared = getRidOfRadix(second)

        val firstPreparedExponent = firstPrepared.exponent
        val secondPreparedExponent = secondPrepared.exponent

        return when {
            first.exponent > second.exponent -> {
                val moveFirstBy = firstPreparedExponent - secondPreparedExponent
                if (moveFirstBy >= 0) {
                    val movedFirst = firstPrepared.significand * 10.toBigInteger().pow(moveFirstBy)
                    return Triple(movedFirst, second.significand, secondPreparedExponent)
                } else {
                    val movedSecond = secondPrepared.significand * 10.toBigInteger().pow(moveFirstBy * -1)
                    Triple(first.significand, movedSecond, firstPreparedExponent)
                }
            }
            first.exponent < second.exponent -> {
                val moveSecondBy = secondPreparedExponent - firstPreparedExponent
                return if (moveSecondBy >= 0) {
                    val movedSecond = secondPrepared.significand * 10.toBigInteger().pow(moveSecondBy)
                    Triple(first.significand, movedSecond, firstPreparedExponent)
                } else {
                    val movedFirst = firstPrepared.significand * 10.toBigInteger().pow(moveSecondBy * -1)
                    Triple(movedFirst, second.significand, firstPreparedExponent)
                }
            }
            first.exponent == second.exponent -> {
                val delta = firstPreparedExponent - secondPreparedExponent
                return when {
                    delta > 0 -> {
                        val movedFirst = first.significand * 10.toBigInteger().pow(delta)
                        Triple(movedFirst, second.significand, firstPreparedExponent)
                    }
                    delta < 0 -> {
                        val movedSecond = second.significand * 10.toBigInteger().pow(delta * -1)
                        Triple(first.significand, movedSecond, firstPreparedExponent)
                    }
                    delta.compareTo(0) == 0 -> {
                        Triple(first.significand, second.significand, firstPreparedExponent)
                    }
                    else -> throw RuntimeException("Invalid delta: $delta")
                }
            }
            else -> {
                throw RuntimeException("Invalid comparison state BigInteger: ${first.exponent}, ${second.exponent}")
            }
        }
    }

    /**
     * Compare to other BigDecimal
     */
    fun compare(other: BigDecimal): Int {
        if (exponent == other.exponent && precision == other.precision) {
            return significand.compare(other.significand)
        }
        val (preparedFirst, preparedSecond) = bringSignificandToSameExponent(this, other)
        return preparedFirst.compare(preparedSecond)
    }

    override fun compareTo(other: Any): Int {
        if (other is Number) {
            if (ComparisonWorkaround.isSpecialHandlingForFloatNeeded(other)) {
                return javascriptNumberComparison(other)
            }
        }
        return when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(BigDecimal.fromLong(other))
            is Int -> compare(BigDecimal.fromInt(other))
            is Short -> compare(BigDecimal.fromShort(other))
            is Byte -> compare(BigDecimal.fromByte(other))
            is Double -> compare(BigDecimal.fromDouble(other))
            is Float -> compare(BigDecimal.fromFloat(other))
            else -> throw RuntimeException("Invalid comparison type for BigDecimal: ${other::class.simpleName}")
        }
    }

    /**
     * Javascrpt doesn't have different types for float, integer, long, it's all just "number", so we need
     * to check if it's a decimal or integer number before comparing.
     */
    private fun javascriptNumberComparison(number: Number): Int {
        val float = number.toFloat()
        return when {
            float % 1 == 0f -> compare(BigDecimal.fromLong(number.toLong()))
            else -> compare(number.toFloat().toBigDecimal())
        }
    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(BigDecimal.fromLong(other))
            is Int -> compare(BigDecimal.fromInt(other))
            is Short -> compare(BigDecimal.fromShort(other))
            is Byte -> compare(BigDecimal.fromByte(other))
            is Double -> compare(BigDecimal.fromDouble(other))
            is Float -> compare(BigDecimal.fromFloat(other))
            else -> -1
        }
        return comparison == 0
    }

    override fun hashCode(): Int {
        return significand.hashCode() + exponent.hashCode() + decimalMode.hashCode()
    }

    /**
     * Return this BigDecimal in scientific notation
     * i.e. 1.23E+9
     */
    override fun toString(): String {
        if (BigDecimal.useToStringExpanded) {
            return toStringExpanded()
        }
        val significandString = significand.toString(10)
        val modifier = if (significand < 0) {
            2
        } else {
            1
        }
        val expand = if (significand.toString().dropLastWhile { it == '0' }.length == 1) {
            "0"
        } else {
            ""
        }

        return when {
            exponent > 0 -> {
                "${placeADotInString(
                    significandString,
                    significandString.length - modifier
                )}${expand}E+$exponent"
            }
            exponent < 0 -> {

                "${placeADotInString(
                    significandString,
                    significandString.length - modifier
                )}${expand}E$exponent"
            }
            exponent == 0L -> {
                "${placeADotInString(
                    significandString,
                    significandString.length - modifier
                )}$expand"
            }
            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
    }

    /**
     * Return this big decimal in expanded notation.
     * i.e. 123000000 for 1.23E+9 or 0.00000000123 for 1.23E-9
     */
    fun toStringExpanded(): String {
        if (this == ZERO) {
            return "0"
        }
        val digits = significand.numberOfDecimalDigits()
        if (exponent > Int.MAX_VALUE) {
            throw RuntimeException("Invalid toStringExpanded request (exponent > Int.MAX_VALUE)")
        }
        val significandString = significand.toStringWithoutSign(10)
        val sign = if (significand.sign == Sign.NEGATIVE) {
            "-"
        } else {
            ""
        }

        val adjusted = when {
            exponent > 0 -> {
                val diffBigInt = (exponent - digits + 1)

                if (diffBigInt > 0) {
                    val expandZeros = diffBigInt * '0'
                    significandString + expandZeros
                } else {
                    placeADotInStringExpanded(significandString, significandString.length - exponent.toInt() - 1)
                }
            }
            exponent < 0 -> {

                val diffInt = exponent.toInt().absoluteValue

                if (diffInt > 0) {
                    val expandZeros = exponent.absoluteValue * '0'
                    placeADotInStringExpanded(expandZeros + significandString, diffInt + significandString.length - 1)
                } else {
                    placeADotInStringExpanded(significandString, significandString.length - 1)
                }
            }
            exponent == 0L -> {
                if (digits == 1L) {
                    return sign + significandString
                }
                placeADotInStringExpanded(significandString, significandString.length - 1)
            }

            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
        return sign + adjusted
    }

    private fun noExponentStringtoScientificNotation(input: String): String {
        return placeADotInString(input, input.length - 1) + "E+${input.length - 1}"
    }

    private fun placeADotInStringExpanded(input: String, position: Int): String {

        val prefix = input.substring(0 until input.length - position)
        val suffix = input.substring(input.length - position until input.length)
        val prepared = if (suffix.isNotEmpty()) {
            (prefix + '.' + suffix).dropLastWhile { it == '0' }
        } else {
            prefix
        }

        return prepared
    }

    private fun placeADotInString(input: String, position: Int): String {

        val prefix = input.substring(0 until input.length - position)
        val suffix = input.substring(input.length - position until input.length)
        val prepared = prefix + '.' + suffix

        return prepared.dropLastWhile { it == '0' }
    }

    operator fun Long.times(char: Char): String {
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
}