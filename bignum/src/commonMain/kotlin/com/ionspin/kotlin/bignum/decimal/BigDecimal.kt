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
import com.ionspin.kotlin.bignum.NarrowingOperations
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.ComparisonWorkaround
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.chosenArithmetic
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.ionspin.kotlin.bignum.serializer.BigDecimalSerializer
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Implementation of floating-point arbitrary precision arithmetic.
 *
 * Each object of this class represents an immutable large floating point number. The underlying implementation
 * uses [BigInteger] to represent sign and significand, and another [BigInteger] to represent the exponent.
 *
 * [DecimalMode] defines the precision and desired [RoundingMode] when using instances of this class in arithmetic operations,
 * but if [DecimalMode] is supplied to the operation it will override instances [DecimalMode]
 *
 * Scale, or the number of digits to the right of the decimal, can also be specified.  Default is no
 * scale, which puts no restriction on number of digits to the right of the decimal. When scale is
 * specified, a [RoundingMode] other than [RoundingMode.NONE] is also required.
 *
 * When arithmetic operations have both operands unlimited precision and no scaling, the result is
 * also unlimited precision and no scale. When an operation mixes an unlimited precision operand
 * and a scaled operand, the result is unlimited precision. WHen both operands have scale,
 * whether unlimited precision or limited precision, then these rules for scale of the result are used:
 * <ul>
 *     <li>add, subtract - max of the two scales</li>
 *     <li>multiply - sum of the two scales</li>
 *     <li>divide - min of the two scales</li>
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

@Serializable(BigDecimalSerializer::class)
class BigDecimal private constructor(
    _significand: BigInteger,
    _exponent: Long = 0L,
    _decimalMode: DecimalMode? = null
) : BigNumber<BigDecimal>,
    CommonBigNumberOperations<BigDecimal>,
    NarrowingOperations<BigDecimal>,
    Comparable<Any> {

    val precision: Long

    val significand: BigInteger
    val exponent: Long
    val decimalMode: DecimalMode?

    init {
        if (_decimalMode != null && _decimalMode.usingScale) {
            val wrk = applyScale(_significand, _exponent, _decimalMode)
            if (wrk.isZero().not()) {
                significand = wrk.significand
                exponent = wrk.exponent
                val newPrecision = significand.numberOfDecimalDigits()
                precision = newPrecision
                decimalMode = _decimalMode.copy(decimalPrecision = newPrecision)
            } else {
                significand = wrk.significand
                exponent = wrk.exponent.times(_decimalMode.decimalPrecision + _decimalMode.scale)
                precision = _decimalMode.decimalPrecision + _decimalMode.scale
                decimalMode = _decimalMode.copy(decimalPrecision = precision)
            }
        } else {
            significand = _significand
            precision = _significand.numberOfDecimalDigits()
            exponent = _exponent
            decimalMode = _decimalMode
        }
    }

    /**
     * [precisionLimit] is zero if precision unlimited. Otherwise returns [DecimalMode.decimalPrecision].
     */
    val precisionLimit = decimalMode?.decimalPrecision ?: 0
    val roundingMode = decimalMode?.roundingMode ?: RoundingMode.NONE

    companion object : BigNumber.Creator<BigDecimal> {
        override val ZERO = BigDecimal(BigInteger.ZERO)
        override val ONE = BigDecimal(BigInteger.ONE)
        override val TWO = BigDecimal(BigInteger.TWO)
        override val TEN = BigDecimal(BigInteger.TEN, _exponent = 1)

        var useToStringExpanded: Boolean = false

        /**
         * Powers of 10 which can be represented exactly in `double`. From java BigDecimal, hopefully
         * significantly more efficient for most conversions than toStrings.
         */
        private val double10pow = doubleArrayOf(
            1.0e0, 1.0e1, 1.0e2, 1.0e3, 1.0e4, 1.0e5,
            1.0e6, 1.0e7, 1.0e8, 1.0e9, 1.0e10, 1.0e11,
            1.0e12, 1.0e13, 1.0e14, 1.0e15, 1.0e16, 1.0e17,
            1.0e18, 1.0e19, 1.0e20, 1.0e21, 1.0e22
        )
        private val maximumDouble = fromDouble(Double.MAX_VALUE)
        private val leastSignificantDouble = fromDouble(Double.MIN_VALUE)

        /**
         * Powers of 10 which can be represented exactly in {@code
         * float}.
         */
        private val float10pow = floatArrayOf(
            1.0e0f, 1.0e1f, 1.0e2f, 1.0e3f, 1.0e4f, 1.0e5f,
            1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f
        )
        private val maximumFloat = fromFloat(Float.MAX_VALUE)
        private val leastSignificantFloat = fromFloat(Float.MIN_VALUE)

        private fun roundOrDont(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            return if (decimalMode.isPrecisionUnlimited) {
                BigDecimal(significand, exponent)
            } else {
                roundSignificand(significand, exponent, decimalMode)
            }
        }

        private enum class SignificantDecider {
            FIVE, LESS_THAN_FIVE, MORE_THAN_FIVE
        }

        private fun determineDecider(discarded: BigInteger): SignificantDecider {
            val scale = (BigInteger.TEN.pow(discarded.numberOfDecimalDigits() - 1))
            val divrem = discarded.divrem(scale)
            val significant = divrem.quotient.abs().intValue(true)
            val rest = divrem.remainder.abs()
            return when {
                significant == 5 -> {
                    if (rest == BigInteger.ZERO) {
                        SignificantDecider.FIVE
                    } else {
                        SignificantDecider.MORE_THAN_FIVE
                    }
                }
                significant > 5 -> SignificantDecider.MORE_THAN_FIVE
                significant < 5 -> SignificantDecider.LESS_THAN_FIVE
                else -> throw RuntimeException("Couldn't determine decider")
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
            var (result, remainder) = if (toDiscard > 0) {
                val additionallyDiscarded = (significand divrem BigInteger.TEN.pow(toDiscard))
                Pair(additionallyDiscarded.quotient, additionallyDiscarded.remainder)
            } else {
                Pair(significand, discarded)
            }

            val sign = if (significand == BigInteger.ZERO) {
                discarded.sign
            } else {
                significand.sign
            }

            if (remainder.isZero()) {
                return result
            }
            val decider = determineDecider(remainder)
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
                            if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (decider != SignificantDecider.LESS_THAN_FIVE) {
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
                            if (decider == SignificantDecider.MORE_THAN_FIVE) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (decider == SignificantDecider.MORE_THAN_FIVE) {
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
                            if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (decider == SignificantDecider.MORE_THAN_FIVE) {
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
                            if (decider == SignificantDecider.MORE_THAN_FIVE) {
                                result++
                            }
                        }
                        Sign.NEGATIVE -> {
                            if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                result--
                            }
                        }
                        Sign.ZERO -> {
                        }
                    }
                }
                RoundingMode.ROUND_HALF_TO_EVEN -> {
                    when {
                        decider == SignificantDecider.FIVE -> {
                            if (significand % 2 == BigInteger.ONE) {
                                // RoundingMode.HALF_CEILING if the digit to the left of the discarded fraction is odd
                                when (sign) {
                                    Sign.POSITIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result++
                                        }
                                    }
                                    Sign.NEGATIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result--
                                        }
                                    }
                                    Sign.ZERO -> {
                                    }
                                }
                            } else {
                                when (sign) {
                                    Sign.POSITIVE -> {
                                        if (decider == SignificantDecider.MORE_THAN_FIVE) {
                                            result++
                                        }
                                    }
                                    Sign.NEGATIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result--
                                        }
                                    }
                                    Sign.ZERO -> {
                                    }
                                }
                            }
                        }
                        decider == SignificantDecider.MORE_THAN_FIVE -> {
                            if (sign == Sign.POSITIVE) {
                                result++
                            }
                            if (sign == Sign.NEGATIVE) {
                                result--
                            }
                        }
                    }
                }
                RoundingMode.ROUND_HALF_TO_ODD -> {
                    when {
                        decider == SignificantDecider.FIVE -> {
                            if (significand % 2 == BigInteger.ZERO) {
                                // RoundingMode.HALF_CEILING if the digit to the left of the discarded fraction is even
                                when (sign) {
                                    Sign.POSITIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result++
                                        }
                                    }
                                    Sign.NEGATIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result--
                                        }
                                    }
                                    Sign.ZERO -> {
                                    }
                                }
                            } else {
                                when (sign) {
                                    Sign.POSITIVE -> {
                                        if (decider == SignificantDecider.MORE_THAN_FIVE) {
                                            result++
                                        }
                                    }
                                    Sign.NEGATIVE -> {
                                        if (decider != SignificantDecider.LESS_THAN_FIVE) {
                                            result--
                                        }
                                    }
                                    Sign.ZERO -> {
                                    }
                                }
                            }
                        }
                        decider == SignificantDecider.MORE_THAN_FIVE -> {
                            if (sign == Sign.POSITIVE) {
                                result++
                            }
                            if (sign == Sign.NEGATIVE) {
                                result--
                            }
                        }
                    }
                }

                RoundingMode.NONE -> {
                    throw ArithmeticException("Non-terminating result of division operation. Specify decimalPrecision")
                }
            }
            return result
        }

        fun handleZeroRounding(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            return when {
                significand.sign == Sign.POSITIVE -> {
                    when (decimalMode.roundingMode) {
                        RoundingMode.CEILING, RoundingMode.AWAY_FROM_ZERO -> {
                            val increasedSignificand = significand.inc()
                            val exponentModifier = increasedSignificand.numberOfDecimalDigits() - significand.numberOfDecimalDigits()
                            BigDecimal(increasedSignificand, exponent + exponentModifier, decimalMode)
                        }
                        else -> BigDecimal(significand, exponent, decimalMode)
                    }
                }
                significand.sign == Sign.NEGATIVE -> {
                    when (decimalMode.roundingMode) {
                        RoundingMode.FLOOR, RoundingMode.AWAY_FROM_ZERO -> {
                            val increasedSignificand = significand.dec()
                            val exponentModifier = increasedSignificand.numberOfDecimalDigits() - significand.numberOfDecimalDigits()
                            BigDecimal(increasedSignificand, exponent + exponentModifier, decimalMode)
                        }
                        else -> BigDecimal(significand, exponent, decimalMode)
                    }
                }
                else -> BigDecimal(significand, exponent, decimalMode)
            }
        }

        private fun roundSignificand(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            if (significand == BigInteger.ZERO) {
                return BigDecimal(BigInteger.ZERO, exponent, decimalMode)
            }
            val significandDigits = significand.numberOfDecimalDigits()
            val desiredPrecision = if (decimalMode.usingScale) {
                decimalMode.decimalPrecision + decimalMode.scale
            } else {
                decimalMode.decimalPrecision
            }
            return when {
                desiredPrecision > significandDigits -> {
                    val extendedSignificand = significand * BigInteger.TEN.pow(desiredPrecision - significandDigits)
                    BigDecimal(extendedSignificand, exponent, decimalMode)
                }
                desiredPrecision < significandDigits -> {
                    val divRem = significand divrem BigInteger.TEN.pow(significandDigits - desiredPrecision)
                    val resolvedRemainder = divRem.remainder
                    if (divRem.remainder == BigInteger.ZERO) {
                        return BigDecimal(divRem.quotient, exponent, decimalMode)
                    }
                    // Check if remainder was .0XXX if so handle it
                    if (significand.numberOfDecimalDigits() == divRem.quotient.numberOfDecimalDigits() + divRem.remainder.numberOfDecimalDigits()) {
                        val newSignificand = roundDiscarded(divRem.quotient, resolvedRemainder, decimalMode)
                        val exponentModifier =
                            newSignificand.numberOfDecimalDigits() - divRem.quotient.numberOfDecimalDigits()
                        BigDecimal(newSignificand, exponent + exponentModifier, decimalMode)
                    } else {
                        handleZeroRounding(divRem.quotient, exponent, decimalMode)
                    }
                }
                else -> {
                    BigDecimal(significand, exponent, decimalMode)
                }
            }
        }

        /**
         * Round the BigDecimal to a specific length AFTER the decimal point (scale).
         * If position is set to 0 a integer value is returned
         *
         * I.e.
         *
         * 1234.5678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 1234.568
         * 123.456 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 123.456
         * 0.0012345678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 0.001
         * 0.0012345678 digitPosition 5, rounding mode HALF_TOWARDS_ZERO will produce 0.00123
         */
        private fun applyScale(significand: BigInteger, exponent: Long, decimalMode: DecimalMode): BigDecimal {
            if (!decimalMode.usingScale) {
                return BigDecimal(significand, exponent, decimalMode)
            }
            val workMode = when {
                exponent >= 0 -> DecimalMode(
                    exponent + decimalMode.scale + 1,
                    decimalMode.roundingMode
                )
                exponent < 0 -> DecimalMode(
                    decimalMode.scale + 1,
                    decimalMode.roundingMode
                )
                else -> throw RuntimeException("Unexpected state")
            }
            return if (exponent >= 0) {
                roundSignificand(significand, exponent, workMode)
            } else {
                val temp = BigDecimal(significand, exponent) + significand.signum()
                roundSignificand(temp.significand, temp.exponent, workMode) - significand.signum()
            }
        }

        /**
         * New BigDecimal from existing, with different DecimalMode.
         *
         * @param bigDecimal Long value to conver
         * @return BigDecimal representing input
         */
        fun fromBigDecimal(bigDecimal: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
            return BigDecimal(bigDecimal.significand, bigDecimal.exponent, decimalMode)
                .roundSignificand(decimalMode)
        }

        /**
         * Convert a Long into a BigDecimal.
         *
         * i.e. 7111 -> 7.111E+3
         *
         * @param bigInteger Long value to conver
         * @return BigDecimal representing input
         */
        fun fromBigInteger(bigInteger: BigInteger, decimalMode: DecimalMode? = null): BigDecimal {
            return BigDecimal(bigInteger, bigInteger.numberOfDecimalDigits() - 1, decimalMode)
                .roundSignificand(decimalMode)
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
         * @param uLong Long value to conver
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
         * @param uInt Int value to conver
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
         * @param uShort Short value to conver
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
         * @param uByte Byte value to conver
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
         * @param byte Short value to convert
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
            return if (floatString.contains('.') && !floatString.contains('E', true)) {
                parseStringWithMode(floatString.dropLastWhile { it == '0' }, decimalMode).roundSignificand(decimalMode)
            } else {
                parseStringWithMode(floatString, decimalMode).roundSignificand(decimalMode)
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
            return if (doubleString.contains('.') && !doubleString.contains('E', true)) {
                parseStringWithMode(doubleString.dropLastWhile { it == '0' }, decimalMode).roundSignificand(decimalMode)
            } else {
                parseStringWithMode(doubleString, decimalMode).roundSignificand(decimalMode).roundSignificand(decimalMode)
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
         * @param bigInteger Long value to conver
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
         * @param uLong Long value to conver
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
         * @param uInt Int value to conver
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
         * @param uShort Short value to conver
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
         * @param uByte Short value to conver
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
         * @param byte Short value to conver
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

        fun parseString(string: String): BigDecimal {
            return parseStringWithMode(string)
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
            if (floatingPointString.contains('E', true)) {
                // Sci notation
                val split = if (floatingPointString.contains('.').not()) {
                    // As is case with JS Double.MIN_VALUE
                    val splitAroundE = floatingPointString.split('E', 'e')
                    listOf(splitAroundE[0], "0E" + splitAroundE[1])
                } else {
                    floatingPointString.split('.')
                }
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
                        // here we need to cover mixed scientific and expanded notationtions, such as 0.00375E-20 = 3.75E-22
                        val exponentModifiedByFloatingPointPosition = if (leftTruncated != "0") {
                            // i.e. 375E-37 = 3.75*10^2*10^-37 =4.75 * 10^-35
                            exponent + leftTruncated.length - 1
                        } else {
                            // i.e. 0.375E-20 = 3.75 * 10^E-23
                            exponent - (rightTruncated.length - significand.numberOfDecimalDigits()) - 1
                        }
                        return BigDecimal(significand, exponentModifiedByFloatingPointPosition, decimalMode)
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
                            val exponent = if (leftTruncated.isNotEmpty() && leftTruncated[0] != '0') {
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

    val scale = decimalMode?.scale ?: -1
    val usingScale = scale >= 0

    /**
     * @param scale a value between 0 and if precision is limited, precision - 1.
     * @return a new BigDecimal with same precision and RoundingMode, but new scale applied. If current
     * DecimalMode is null and scale is not null, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO is default
     */
    fun scale(scale: Long): BigDecimal {
        if (scale < 0)
            throw ArithmeticException("Negative Scale is unsupported.")
        val mode = if (decimalMode == null) {
            if (scale == -1L) {
                DecimalMode.DEFAULT
            } else {
                DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale)
            }
        } else {
            DecimalMode(decimalMode.decimalPrecision - decimalMode.scale, decimalMode.roundingMode, scale)
        }
        return BigDecimal(significand, exponent, mode)
    }

    fun removeScale(): BigDecimal {
        return BigDecimal(
            significand, exponent, DecimalMode(
                decimalMode?.decimalPrecision ?: 0,
                decimalMode?.roundingMode ?: RoundingMode.NONE,
                -1
            )
        )
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
        return add(other, computeMode(other, ScaleOps.Max))
    }

    /**
     * Add two BigDecimal and return result in a new instance of BigDecimal.
     *
     * @param other BigDecimal (addend)
     * @return BigDecimal containing result of the operation
     */
    fun add(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        if (this == ZERO) {
            return roundOrDont(other.significand, other.exponent, resolvedDecimalMode)
        }
        if (other == ZERO) {
            return roundOrDont(this.significand, this.exponent, resolvedDecimalMode)
        }
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

        return if (resolvedDecimalMode.usingScale) {
            roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode.copy(decimalPrecision = newSignificandNumOfDigit)
            )
        } else {
            roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode
            )
        }
    }

    /**
     * Subtract two BigDecimal and return result in a new instance of BigDecimal.
     * Default decimal mode will be used
     *
     * @param other BigDecimal (subtrahend)
     * @return BigDecimal containing result of the operation
     */
    override fun subtract(other: BigDecimal): BigDecimal {
        return subtract(other, computeMode(other, ScaleOps.Max))
    }

    /**
     * Subtract two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (subtrahend)
     * @return BigDecimal containing result of the operation
     */
    fun subtract(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)

        if (this == ZERO) {
            return roundOrDont(other.significand.negate(), other.exponent, resolvedDecimalMode)
        }
        if (other == ZERO) {
            return roundOrDont(this.significand, this.exponent, resolvedDecimalMode)
        }

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
        if (usingScale) {
            return roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode.copy(decimalPrecision = newSignificandNumOfDigit)
            )
        } else {
            return roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode
            )
        }
    }

    /**
     * Multiply two BigDecimal and return result in a new instance of BigDecimal
     * Default decimal mode will be used
     * @param other BigDecimal (multiplicand)
     * @return BigDecimal containing result of the operation
     */
    override fun multiply(other: BigDecimal): BigDecimal {
        return multiply(other, computeMode(other, ScaleOps.Max))
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
        return if (resolvedDecimalMode.usingScale) {
            roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode.copy(decimalPrecision = newSignificandNumOfDigit)
            )
        } else {
            roundOrDont(
                newSignificand,
                newExponent,
                resolvedDecimalMode
            )
        }
    }

    override fun divide(other: BigDecimal): BigDecimal {
        return divide(other, computeMode(other, ScaleOps.Max))
    }

    /**
     * Divide two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (divisor)
     * @return BigDecimal containing result of the operation
     */
    fun divide(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        if (resolvedDecimalMode.isPrecisionUnlimited) {
            val newExponent = this.exponent - other.exponent
            val power = (other.precision * 2 + 6)
            val thisPrepared = this.significand * BigInteger.TEN.pow(power)
            val divRem = thisPrepared divrem other.significand
            val result = divRem.quotient
            val expectedDiff = other.precision - 1
            val exponentModifier =
                expectedDiff + (result.numberOfDecimalDigits() - thisPrepared.numberOfDecimalDigits())

            if (divRem.remainder != BigInteger.ZERO) {
                throw ArithmeticException(
                    "Non-terminating result of division operation " +
                            "(i.e. 1/3 = 0.3333... library needs to know when to stop and how to round up at that point). " +
                            "Specify decimalPrecision inside your decimal mode."
                )
            }
            return BigDecimal(
                result,
                newExponent + exponentModifier,
                resolvedDecimalMode
            )
        } else {
            var newExponent = this.exponent - other.exponent - 1

            val desiredPrecision = resolvedDecimalMode.decimalPrecision

            val power = desiredPrecision - this.precision + other.precision
            val thisPrepared = when {
                power > 0 -> this.significand * 10.toBigInteger().pow(power)
                power < 0 -> this.significand / 10.toBigInteger().pow(power.absoluteValue)
                else -> this.significand
            }

            val divRem = thisPrepared divrem other.significand
            val result = divRem.quotient
            if (result == BigInteger.ZERO) {
                newExponent--
            }
            val exponentModifier = result.numberOfDecimalDigits() - resolvedDecimalMode.decimalPrecision

            return if (usingScale) {
                BigDecimal(
                    roundDiscarded(result, divRem.remainder, resolvedDecimalMode),
                    newExponent + exponentModifier,
                    resolvedDecimalMode.copy(decimalPrecision = result.numberOfDecimalDigits())
                )
            } else {
                BigDecimal(
                    roundDiscarded(result, divRem.remainder, resolvedDecimalMode),
                    newExponent + exponentModifier,
                    resolvedDecimalMode
                )
            }
        }
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
        if (places == 0) {
            return this
        }
        return copy(exponent = exponent + places)
    }

    /**
     * Moves the decimal point by creating a new instance with a different exponent. Positive values move
     * the decimal point to the right, negative values move the decimal point to the left.
     */
    fun moveDecimalPoint(places: Long): BigDecimal {
        if (places == 0L) {
            return this
        }
        return copy(exponent = exponent + places)
    }

    // TODO in 0.3.x
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

    // TODO in 0.3.x
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
            exponent in 1L until precision -> precision
            exponent > 0 && exponent > precision -> exponent + 1 // Significand is already 10^1 when exponent is > 0
            exponent > 0 && exponent == precision -> precision + 1 // Same as above
            exponent < 0 -> exponent.absoluteValue + precision
            exponent == 0L -> removeTrailingZeroes(this).precision
            else -> throw RuntimeException("Invalid case when getting number of decimal digits")
        }
        return numberOfDigits
    }

    /**
     * Used to squeeze significands into minimal precision i.e. 12340000 to 1234 because both numbers still represent
     * the same number i.e. 1.234
     */
    private fun removeTrailingZeroes(bigDecimal: BigDecimal): BigDecimal {
        if (bigDecimal == ZERO) return this
        var significand = bigDecimal.significand
        var divisionResult = BigInteger.QuotientAndRemainder(bigDecimal.significand, BigInteger.ZERO)
        do {
            divisionResult = divisionResult.quotient.divrem(BigInteger.TEN)
            if (divisionResult.remainder == BigInteger.ZERO) {
                significand = divisionResult.quotient
            }
        } while (divisionResult.remainder == BigInteger.ZERO)
        return BigDecimal(significand, bigDecimal.exponent)
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

    private enum class ScaleOps {
        Max, Min, Add
    }

    private fun computeMode(other: BigDecimal, op: ScaleOps): DecimalMode {
        return if (decimalMode == null ||
            decimalMode.isPrecisionUnlimited ||
            other.decimalMode == null ||
            other.decimalMode.isPrecisionUnlimited
        )
            DecimalMode.DEFAULT
        else {
            DecimalMode(
                max(decimalMode.decimalPrecision, other.decimalMode.decimalPrecision),
                decimalMode.roundingMode,
                if (decimalMode.usingScale && other.decimalMode.usingScale)
                    when (op) {
                        ScaleOps.Max -> max(decimalMode.scale, other.decimalMode.scale)
                        ScaleOps.Min -> min(decimalMode.scale, other.decimalMode.scale)
                        ScaleOps.Add -> decimalMode.scale + other.decimalMode.scale
                    } else
                    -1
            )
        }
    }

    override operator fun plus(other: BigDecimal): BigDecimal {
        return this.add(other, computeMode(other, ScaleOps.Max))
    }

    override operator fun minus(other: BigDecimal): BigDecimal {
        return this.subtract(other, computeMode(other, ScaleOps.Max))
    }

    override operator fun times(other: BigDecimal): BigDecimal {
        return this.multiply(other, computeMode(other, ScaleOps.Max))
    }

    override operator fun div(other: BigDecimal): BigDecimal {
        return this.divide(other, computeMode(other, ScaleOps.Max))
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

    override fun secureOverwrite() {
        significand.secureOverwrite()
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
        return BigDecimal(significand.abs(), exponent, decimalMode)
    }

    /**
     * Negate this BigDecimal
     */
    override fun negate(): BigDecimal {
        return BigDecimal(significand.negate(), exponent, decimalMode)
    }

    /**
     * Exponentiate this BigDecimal by some exponent
     */
    override fun pow(exponent: Long): BigDecimal {
        var result = this
        return when {
            exponent > 0 -> {
                for (i in 0 until exponent - 1) {
                    result *= this
                }
                result
            }
            exponent < 0 -> {
                for (i in 0..exponent.absoluteValue) {
                    result /= this
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
     * The next group of functions are implementations of the NarrowingOperations interface
     */
    /**
     * Convert the current value to  Int.
     * @param exactRequired True causes an ArithmeticException to be thrown if there is any loss of
     * precision during the conversion, either side of the decimal. False truncates any precision to
     * the right of the decimal.
     */
    override fun intValue(exactRequired: Boolean): Int {
        checkWholeness(exactRequired)
        return toBigInteger().intValue(exactRequired)
    }

    override fun longValue(exactRequired: Boolean): Long {
        checkWholeness(exactRequired)
        return toBigInteger().longValue(exactRequired)
    }

    override fun byteValue(exactRequired: Boolean): Byte {
        checkWholeness(exactRequired)
        return toBigInteger().byteValue(exactRequired)
    }

    override fun shortValue(exactRequired: Boolean): Short {
        checkWholeness(exactRequired)
        return toBigInteger().shortValue(exactRequired)
    }

    override fun uintValue(exactRequired: Boolean): UInt {
        checkWholeness(exactRequired)
        return toBigInteger().uintValue(exactRequired)
    }

    override fun ulongValue(exactRequired: Boolean): ULong {
        checkWholeness(exactRequired)
        return toBigInteger().ulongValue(exactRequired)
    }

    override fun ubyteValue(exactRequired: Boolean): UByte {
        checkWholeness(exactRequired)
        return toBigInteger().ubyteValue(exactRequired)
    }

    override fun ushortValue(exactRequired: Boolean): UShort {
        checkWholeness(exactRequired)
        return toBigInteger().ushortValue(exactRequired)
    }

    /**
     * @return true if "this" is a whole number, false if not
     */
    fun isWholeNumber(): Boolean {
        return abs().divrem(ONE).second.isZero()
    }

    /**
     * @param exactRequired if not a whole number, throw an exception
     * @return true if this is a whole number, false if not
     * @throws ArithmeticException if any nonzero value to the right of the decimal
     */
    private fun checkWholeness(exactRequired: Boolean) {
        if (exactRequired && !isWholeNumber())
            throw ArithmeticException("Cannot convert to int and provide exact value")
    }

    /**
     * Narrow to a float.
     * @param exactRequired if false, precision can be lost. If true, the current absolute value
     * must be between Float.MAX_VALUE and Float.MIN_VALUE, with 7 or less digits of precision.
     * @throws ArithmeticException if exactRequired is true and any of the above conditions not met
     */
    override fun floatValue(exactRequired: Boolean): Float {
        if (exactRequired) {
            var exactPossible = true
            // IEEE 754 float
            // Exponent can be between -126 and 127 in base 2 or -38 and 38 in base 10 (2^-126 ~ 10^-38), but subnormal
            // can go to -45, and since we check the significand part in a base 2 algorithm we should be covering subnormal
            // values correctly as well
            if (exponent < -45L || exponent > 38L) {
                exactPossible = false
            }
            // For significand we can have a maximum of 24 bits (23 + 1 implicit)
            // If there is no decimal point at all we can count the bits after modifying for exponent,
            // but if there is we need to convert the fractional part to binary32 representation first.
            // Bit count:
            val totalBits = if (precision - exponent - 1 > 0) {
                // First find out where the decimal point will be
                val integerPart = if (exponent >= 0) {
                    significand / BigInteger.TEN.pow(precision - exponent - 1)
                } else {
                    BigInteger.ZERO
                }
                val integerPartBitLength = chosenArithmetic.bitLength(integerPart.magnitude)

                val fractionPart = (significand divrem BigInteger.TEN.pow(precision - exponent - 1)).remainder
                var fractionConvertTemp = BigDecimal(fractionPart, -1) // this will represent the integer xxxx as 0.xxxx
                val bitList = mutableListOf<Int>()
                var counter = 0
                while (fractionConvertTemp != ZERO && counter <= 24) {
                    val multiplied = fractionConvertTemp * 2
                    val bit = if (multiplied >= ONE) {
                        1
                    } else {
                        0
                    }
                    bitList.add(bit)
                    fractionConvertTemp = if (bit == 1) {
                        (multiplied divrem TEN).second
                    } else {
                        multiplied
                    }
                    counter++
                }
                val bitSum = integerPartBitLength + bitList.size
                bitSum
            } else {
                // There is no fractional part so we need check if the distance between first non zero bit and
                // last non zero bit is is less than or equal to 24
                val trailingZeroBits = chosenArithmetic.trailingZeroBits(significand.magnitude)
                val bitSum = chosenArithmetic.bitLength(significand.magnitude)
                bitSum - trailingZeroBits
            }

            if (totalBits > 24) {
                exactPossible = false
            }

            if (!exactPossible) {
                throw ArithmeticException("Value cannot be narrowed to float")
            }
        }

        /*
         * For large exponent values, use fallback string parse
         */
        val divExponent = precision - 1 - exponent
        val f = this.significand.longValue(exactRequired)
        return if (divExponent >= 0 && divExponent < float10pow.size) {
            f / float10pow[divExponent.toInt()]
        } else {
            this.toString().toFloat()
        }
    }

    /**
     * Narrow to a double.
     * @param exactRequired if false, precision can be lost. If true, the current absolute value
     * must be between Double.MAX_VALUE and Double.MIN_VALUE, with 16 or less digits of precision.
     * @throws ArithmeticException if exactRequired is true and any of the above conditions not met
     */
    override fun doubleValue(exactRequired: Boolean): Double {
        if (exactRequired) {
            var exactPossible = true
            // IEEE 754 double precision
            // Exponent can be between -1022 and 1023 in base 2 or -308 and 308 in base 10, but subnormal numbers
            // can go to -324, and since we check the significand part in a base 2 algorithm we should be covering subnormal
            // values correctly as well
            if (exponent < -324 || exponent > 308L) {
                exactPossible = false
            }
            // For significand we can have a maximum of 53 (52 + 1 implicit)
            // If there is no decimal point at all we can directly count the bits in significand and use them,
            // but if there is we need to convert the fractional part to binary32 representation first.
            // Bit count:
            val totalBits = if (precision - exponent - 1 > 0) {
                // First find out where the decimal point will be
                val integerPart = if (exponent >= 0) {
                    significand / BigInteger.TEN.pow(precision - exponent - 1)
                } else {
                    BigInteger.ZERO
                }
                val integerPartBitLength = chosenArithmetic.bitLength(integerPart.magnitude)

                val fractionPart = (significand divrem BigInteger.TEN.pow(precision - exponent - 1)).remainder
                var fractionConvertTemp = BigDecimal(fractionPart, -1) // this will represent the integer xxxx as 0.xxxx
                val bitList = mutableListOf<Int>()
                var counter = 0
                while (fractionConvertTemp != ZERO && counter <= 53) {
                    val multiplied = fractionConvertTemp * 2
                    val bit = if (multiplied >= ONE) {
                        1
                    } else {
                        0
                    }
                    bitList.add(bit)
                    fractionConvertTemp = if (bit == 1) {
                        (multiplied divrem TEN).second
                    } else {
                        multiplied
                    }
                    counter++
                }
                val bitSum = integerPartBitLength + bitList.size
                bitSum
            } else {
                // There is no fractional part so we need check if the distance between first non zero bit and
                // last non zero bit is is less than or equal to 24
                // We can reuse the trailing zero bits to count number of zeroes from the right, because we know
                // that in big integer first non-zero is always the leftmost bit
                val trailingZeroBits = chosenArithmetic.trailingZeroBits(significand.magnitude)
                val bitSum = chosenArithmetic.bitLength(significand.magnitude)
                bitSum - trailingZeroBits
            }

            if (totalBits > 53) {
                exactPossible = false
            }

            if (!exactPossible) {
                throw ArithmeticException("Value cannot be narrowed to float")
            }
        }

        /*
         *  For large exponent values, use fallback string parse
         */
        val divExponent = precision - 1 - exponent
        val l = this.significand.longValue(exactRequired)
        return if (l.toDouble().toLong() == l && divExponent >= 0 && divExponent < double10pow.size) {
            (l / double10pow[divExponent.toInt()]) * signum()
        } else {
            toString().toDouble()
        }
    }

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
     * Use this for rounding when not using scale.
     * I.e.
     *
     * 1234.5678 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 1230
     * 123.456 digitPosition 3, rounding mode HALF_TOWARDS_ZERO will produce 123
     * 0.0012345678 digitPosition 4, rounding mode HALF_TOWARDS_ZERO will produce 0.001
     * 0.0012345678 digitPosition 6, rounding mode HALF_TOWARDS_ZERO will produce 0.00123
     */
    fun roundToDigitPosition(digitPosition: Long, roundingMode: RoundingMode = this.roundingMode): BigDecimal {
        if (digitPosition == 0L) {
            throw ArithmeticException("Rounding to 0 position is not supported")
        }
        val rounded = if (this.exponent >= 0) {
            roundSignificand(DecimalMode(digitPosition, roundingMode))
        } else {
            (this + this.signum()).roundSignificand(DecimalMode(digitPosition, roundingMode)) - this.signum()
        }

        return if (decimalMode == null) {
            BigDecimal(rounded.significand, rounded.exponent)
        } else {
            BigDecimal(rounded.significand, rounded.exponent, decimalMode)
        }
    }

    /**
     * Round the BigDecimal to a specific length AFTER the decimal point. If position is set to 0 a integer value is returned
     * Use this only if not using scale.
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
        val rounded = when {
            exponent >= 0 -> roundToDigitPosition(exponent + digitPosition + 1, roundingMode)
            exponent < 0 -> roundToDigitPosition(digitPosition + 1, roundingMode)
            else -> throw RuntimeException("Unexpected state")
        }
        return if (decimalMode == null) {
            BigDecimal(rounded.significand, rounded.exponent)
        } else {
            BigDecimal(rounded.significand, rounded.exponent, decimalMode)
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
        return if (this.exponent == other.exponent && this.precision == other.precision) {
            significand.compare(other.significand)
        } else {
            val (preparedFirst, preparedSecond) = bringSignificandToSameExponent(this, other)
            preparedFirst.compare(preparedSecond)
        }
    }

    override fun compareTo(other: Any): Int {
        if (other is Number) {
            if (ComparisonWorkaround.isSpecialHandlingForFloatNeeded(other)) {
                return javascriptNumberComparison(other)
            }
        }
        return when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(fromLong(other))
            is Int -> compare(fromInt(other))
            is Short -> compare(fromShort(other))
            is Byte -> compare(fromByte(other))
            is Double -> compare(fromDouble(other))
            is Float -> compare(fromFloat(other))
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
            float % 1 == 0f -> compare(fromLong(number.toLong()))
            else -> compare(number.toFloat().toBigDecimal())
        }
    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(fromLong(other))
            is Int -> compare(fromInt(other))
            is Short -> compare(fromShort(other))
            is Byte -> compare(fromByte(other))
            is Double -> compare(fromDouble(other))
            is Float -> compare(fromFloat(other))
            else -> -1
        }
        return comparison == 0
    }

    override fun hashCode(): Int {
        if (this == ZERO) {
            return 0
        }
        return removeTrailingZeroes(this).significand.hashCode() + exponent.hashCode()
    }

    /**
     * Return this BigDecimal in scientific notation
     * i.e. 1.23E+9
     */
    override fun toString(): String {
        if (useToStringExpanded) {
            return toStringExpanded()
        }
        val significandString = significand.toString(10)
        val modifier = if (significand < 0) {
            2
        } else {
            1
        }
        val expand = if (significand.toString().dropLastWhile { it == '0' }.length <= 1) {
            "0"
        } else {
            ""
        }

        return when {
            exponent > 0 -> {
                "${
                    placeADotInString(
                        significandString,
                        significandString.length - modifier
                    )
                }${expand}E+$exponent"
            }
            exponent < 0 -> {

                "${
                    placeADotInString(
                        significandString,
                        significandString.length - modifier
                    )
                }${expand}E$exponent"
            }
            exponent == 0L -> {
                "${
                    placeADotInString(
                        significandString,
                        significandString.length - modifier
                    )
                }$expand"
            }
            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
    }

    /**
     * Convenience method matching Java BigDecimal, same functionality as toStringExpanded
     */
    fun toPlainString(): String {
        return toStringExpanded()
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
        val suffix = input.substring(input.length - position until input.length).dropLastWhile { it == '0' }

        return if (suffix.isNotEmpty()) {
            ("$prefix.$suffix")
        } else {
            prefix
        }
    }

    private fun placeADotInString(input: String, position: Int): String {

        val prefix = input.substring(0 until input.length - position)
        val suffix = input.substring(input.length - position until input.length)
        val prepared = "$prefix.$suffix"

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
