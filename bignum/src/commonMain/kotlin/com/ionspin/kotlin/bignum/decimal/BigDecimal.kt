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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigInteger.Companion.TEN
import com.ionspin.kotlin.bignum.integer.BigInteger.Companion.fromLong
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
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
    val exponent: BigInteger = BigInteger.ZERO,
    val decimalMode: DecimalMode? = null
) : Comparable<Any> {

    val precision = significand.numberOfDigits()

    companion object {
        val ZERO = BigDecimal(BigInteger.ZERO)
        val ONE = BigDecimal(BigInteger.ONE)


        private fun roundOrDont(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            return if (decimalMode.decimalPrecision != 0L && decimalMode.roundingMode != RoundingMode.NONE) {
                round(significand, exponent, decimalMode)
            } else {
                BigDecimal(significand, exponent)
            }
        }

        @Suppress("UNUSED_EXPRESSION")
        private fun roundDiscarded(
            significand: BigInteger,
            discarded: BigInteger,
            decimalMode: DecimalMode
        ): BigInteger {
            val toDiscard = significand.numberOfDigits() - decimalMode.decimalPrecision
            var result = if (toDiscard > 0) {
                (significand divrem TEN.pow(toDiscard)).quotient
            } else {
                significand
            }
            val sign = if (significand == BigInteger.ZERO) {
                discarded.sign
            } else {
                significand.sign
            }
            val significantRemainderDigit = if (toDiscard > 0) {
                (discarded / (discarded.numberOfDigits())).abs() + (significand divrem TEN.pow(toDiscard)).remainder * TEN.pow(toDiscard)
            } else {
                (discarded / (discarded.numberOfDigits())).abs()
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

        private fun round(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            if (significand == BigInteger.ZERO) {
                return BigDecimal(BigInteger.ZERO, exponent, decimalMode)
            }
            val significandDigits = significand.numberOfDigits()
            val desiredPrecision = decimalMode.decimalPrecision
            val newSignificand = when {
                desiredPrecision > significandDigits -> {
                    val extendedSignidicand = significand * TEN.pow(desiredPrecision - significandDigits)
                    return BigDecimal(extendedSignidicand, exponent, decimalMode)
                }
                desiredPrecision < significandDigits -> {
                    val divRem = significand divrem TEN.pow(significandDigits - desiredPrecision)
                    roundDiscarded(divRem.quotient, divRem.remainder, decimalMode)

                }
                else -> {
                    return BigDecimal(significand, exponent, decimalMode)
                }
            }
            val newExponent = exponent - exponent.signum()

            return BigDecimal(newSignificand, newExponent, decimalMode)


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
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
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
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
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
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
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
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
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
            BigDecimal(BigInteger.fromLong(long), BigInteger.ZERO, decimalMode).round(decimalMode)
        /**
         * Convert a Int into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param int Int value to conver
         * @return BigDecimal representing input
         */
        fun fromIntAsSignificand(int: Int, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromInt(int), BigInteger.ZERO, decimalMode).round(decimalMode)

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromShortAsSignificand(short: Short, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromShort(short), BigInteger.ZERO, decimalMode).round(decimalMode)

        /**
         * Convert a Short into a BigDecimal, but use supplied value directly as significant.
         *
         * i.e. 7111 -> 7.111E+0
         *
         * @param short Short value to conver
         * @return BigDecimal representing input
         */
        fun fromByteAsSignificand(byte: Byte, decimalMode: DecimalMode? = null) =
            BigDecimal(BigInteger.fromByte(byte), BigInteger.ZERO, decimalMode).round(decimalMode)

        /**
         * Convert a float into a BigDecimal
         *
         * i.e. 71.11 -> 7.111E+2
         *
         * @param float Float value to conver
         * @return BigDecimal representing input
         */
        fun fromFloat(float: Float, decimalMode: DecimalMode? = null): BigDecimal {
            return parseString(float.toString().dropLastWhile { it == '0' }, decimalMode)
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
            return parseString(double.toString().dropLastWhile { it == '0' }, decimalMode)
        }

        /**
         * Create BigDecimal from BigInteger significand and BigInteger exponent
         */
        fun fromBigIntegerWithExponent(
            bigInteger: BigInteger,
            exponent: BigInteger,
            decimalMode: DecimalMode = DecimalMode()
        ): BigDecimal {
            return BigDecimal(bigInteger, exponent, decimalMode).round(decimalMode)
        }
        /**
         * Create BigDecimal from Long significand and BigInteger exponent
         */
        fun fromLongWithExponent(
            long: Long,
            exponent: BigInteger,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }
        /**
         * Create BigDecimal from Int significand and BigInteger exponent
         */
        fun fromIntWithExponent(int: Int, exponent: BigInteger, decimalMode: DecimalMode? = null): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }
        /**
         * Create BigDecimal from Short significand and BigInteger exponent
         */
        fun fromShortWithExponent(
            short: Short,
            exponent: BigInteger,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }
        /**
         * Create BigDecimal from Short significand and BigInteger exponent
         */
        fun fromByteWithExponent(
            byte: Byte,
            exponent: BigInteger,
            decimalMode: DecimalMode? = null
        ): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }
        /**
         * Create BigDecimal from Long significand and Int exponent
         */
        fun fromLongWithExponent(long: Long, exponent: Int, decimalMode: DecimalMode? = null): BigDecimal =
            fromLongWithExponent(long, exponent.toBigInteger(), decimalMode).round(decimalMode)
        /**
         * Create BigDecimal from Int significand and Int exponent
         */
        fun fromIntWithExponent(int: Int, exponent: Int, decimalMode: DecimalMode? = null): BigDecimal =
            fromIntWithExponent(int, exponent.toBigInteger()).round(decimalMode)
        /**
         * Create BigDecimal from Short significand and Int exponent
         */
        fun fromShortWithExponent(short: Short, exponent: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal =
            fromShortWithExponent(short, exponent.toBigInteger(), decimalMode).round(decimalMode)
        /**
         * Create BigDecimal from Byte significand and Int exponent
         */
        fun fromByteWithExponent(byte: Byte, exponent: Int, decimalMode: DecimalMode? = null): BigDecimal =
            fromByteWithExponent(byte, exponent.toBigInteger(), decimalMode).round(decimalMode)

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
        fun parseString(floatingPointString: String, decimalMode: DecimalMode? = null): BigDecimal {
            if (floatingPointString.isEmpty()) {
                return ZERO
            }
            if (floatingPointString.contains('E') || floatingPointString.contains('e')) {
                //Sci notation
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
                            BigInteger.parseString(exponentString, 10)
                        } else {
                            BigInteger.parseString(exponentString, 10).negate()
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
                //Expanded notation
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
                                BigInteger.fromInt(leftTruncated.length - 1)
                            } else {
                                BigInteger.fromInt(rightTruncated.indexOfFirst { it != '0' } + 1).negate()
                            }

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
                    val significand = BigInteger.parseString(floatingPointString, 10)
                    return BigDecimal(significand, BigInteger.fromLong(significand.numberOfDigits() - 1), decimalMode)
                }
            }

        }

        private fun resolveDecimalMode(firstDecimalMode: DecimalMode?, secondDecimalMode : DecimalMode?, suppliedDecimalMode: DecimalMode?) : DecimalMode {
            return if (suppliedDecimalMode != null) {
                suppliedDecimalMode
            } else {
                if (firstDecimalMode == null && secondDecimalMode == null) { return DecimalMode() }
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

    val isExponentLong = exponent.numberOfWords == 0
    val longExponent = exponent.magnitude[0]

    /**
     * Add two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (addend)
     * @return BigDecimal containing result of the operation
     */
    fun addition(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second, _) = bringSignificandToSameExponent(this, other)
        //Temporary way to detect a carry happened, proper solution is to add
        //methods that return information about carry in arithmetic classes, this way it's going
        //to be rather slow
        val firstNumOfDigits = first.numberOfDigits()
        val secondNumOfDigits = second.numberOfDigits()
        val newSignificand = first + second
        val newSignificandNumOfDigit = newSignificand.numberOfDigits()
        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) {
            firstNumOfDigits
        } else {
            secondNumOfDigits
        }
        val carryDetected = newSignificandNumOfDigit - largerOperand
        val newExponent = BigInteger.max(this.exponent, other.exponent) + carryDetected

        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }
    /**
     * Subtract two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (subtrahend)
     * @return BigDecimal containing result of the operation
     */
    fun subtraction(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second, _) = bringSignificandToSameExponent(this, other)

        val firstNumOfDigits = first.numberOfDigits()
        val secondNumOfDigits = second.numberOfDigits()

        val newSignificand = first - second

        val newSignificandNumOfDigit = newSignificand.numberOfDigits()

        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) {
            firstNumOfDigits
        } else {
            secondNumOfDigits
        }
        val borrowDetected = newSignificandNumOfDigit - largerOperand

        val newExponent = BigInteger.max(this.exponent, other.exponent) + borrowDetected
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    /**
     * Multiply two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (multiplicand)
     * @return BigDecimal containing result of the operation
     */
    fun multiplication(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        //Temporary way to detect a carry happened, proper solution is to add
        //methods that return information about carry in arithmetic classes, this way it's going
        //to be rather slow
        val firstNumOfDigits = this.significand.numberOfDigits()
        val secondNumOfDigits = other.significand.numberOfDigits()

        val newSignificand = this.significand * other.significand

        val newSignificandNumOfDigit = newSignificand.numberOfDigits()
        val moveExponent = newSignificandNumOfDigit - (firstNumOfDigits + secondNumOfDigits)

        val newExponent = this.exponent + other.exponent + moveExponent + 1
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)

    }
    /**
     * Divide two BigDecimal and return result in a new instance of BigDecimal
     *
     * @param other BigDecimal (divisor)
     * @return BigDecimal containing result of the operation
     */
    fun div(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
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

        val thisPrepared = this.significand * 10.toBigInteger().pow(desiredPrecision - this.precision + other.precision)


        var divRem = thisPrepared divrem other.significand
        var result = divRem.quotient
        if (result == BigInteger.ZERO) {
            newExponent--
        }
        val exponentModifier = result.numberOfDigits() - resolvedDecimalMode.decimalPrecision
        if (divRem.remainder != BigInteger.ZERO && resolvedDecimalMode.decimalPrecision == 0L && resolvedDecimalMode.roundingMode == RoundingMode.NONE) {
            throw ArithmeticException("Non-terminating result of division operation. Specify decimalPrecision")
        }
        return BigDecimal(
            roundDiscarded(result, divRem.remainder, resolvedDecimalMode),
            newExponent + exponentModifier,
            resolvedDecimalMode
        )

//        var newExponent = this.exponent - other.exponent
//
//        var divRem = this.significand divrem other.significand
//        var result = divRem.quotient
//        if (result == BigInteger.ZERO) {
//            newExponent--
//        }
//        var counter = 0
//        while (divRem.remainder != BigInteger.ZERO) {
//            divRem = (divRem.remainder * 10) divrem other.significand
//            counter++
//            //Until rounding and decimalPrecision is fully implemented
//            if (counter == 100) {
//                break
//            }
//            result = result * 10 + divRem.quotient
//        }
//        return roundOrDont(result, newExponent, decimalMode)
    }



    //TODO
    private fun integerDiv(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand / other.significand
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    private fun rem(other: BigDecimal, decimalMode: DecimalMode? = null): BigDecimal {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand % other.significand
        return roundOrDont(newSignificand, newExponent, resolvedDecimalMode)
    }

    //TODO
    private fun divrem(other: BigDecimal, decimalMode: DecimalMode? = null): Pair<BigDecimal, BigDecimal> {
        val resolvedDecimalMode = resolveDecimalMode(this.decimalMode, other.decimalMode, decimalMode)
        val newExponent = BigInteger.max(this.exponent, other.exponent)
        val newSignificand = this.significand / other.significand
        val newRemainderSignificand = this.significand % other.significand
        return Pair(
            roundOrDont(newSignificand, newExponent, resolvedDecimalMode),
            roundOrDont(newRemainderSignificand, newExponent, resolvedDecimalMode)
        )
    }

    operator fun plus(other: BigDecimal): BigDecimal {
        return this.addition(other, DecimalMode())
    }

    operator fun minus(other: BigDecimal): BigDecimal {
        return this.subtraction(other, DecimalMode())
    }

    operator fun times(other: BigDecimal): BigDecimal {
        return this.multiplication(other, DecimalMode())
    }

    operator fun div(other: BigDecimal): BigDecimal {
        return this.div(other, DecimalMode())
    }

    operator fun rem(other: BigDecimal): BigDecimal {
        return this.rem(other, DecimalMode())
    }

    /**
     * Returns a new negated instance
     */
    fun unaryMinus(): BigDecimal {
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
    fun abs(): BigDecimal {
        return BigDecimal(significand.abs(), exponent)
    }

    /**
     * Negate this BigDecimal
     */
    fun negate(): BigDecimal {
        return BigDecimal(significand.negate(), exponent)
    }

    /**
     * Exponentiate this BigDecimal by some exponent
     */
    fun pow(powerExponent: Long): BigDecimal {
        return BigDecimal(significand, exponent * powerExponent)
    }

    /**
     * Signum function
     * @return Result of signum function for this BigDecimal (-1 negative, 0 zero, 1 positive)
     */
    fun signum() : Int = significand.signum()

    /**
     * Round using specific [DecimalMode] and return rounded instance
     */
    fun round(decimalMode: DecimalMode?): BigDecimal {
        if (decimalMode == null) {
            return this
        }
        if (decimalMode == DecimalMode()) {
            return this
        }
        return Companion.round(this.significand, this.exponent, decimalMode)
    }

    private fun getRidOfRadix(bigDecimal: BigDecimal): BigDecimal {
        val precision = bigDecimal.significand.numberOfDigits()
        val newExponent = bigDecimal.exponent - precision + 1
        return BigDecimal(bigDecimal.significand, newExponent)
    }

    private fun bringSignificandToSameExponent(
        first: BigDecimal,
        second: BigDecimal
    ): Triple<BigInteger, BigInteger, BigInteger> {
//        if (first.exponent == second.exponent) {
//            return Triple(first.significand, second.significand, first.exponent)
//        }
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
                    val movedSecond = secondPrepared.significand * 10.toBigInteger().pow(moveFirstBy.negate())
                    Triple(first.significand, movedSecond, firstPreparedExponent)
                }
            }
            first.exponent < second.exponent -> {
                val moveSecondBy = secondPreparedExponent - firstPreparedExponent
                return if (moveSecondBy >= 0) {
                    val movedSecond = secondPrepared.significand * 10.toBigInteger().pow(moveSecondBy)
                    Triple(first.significand, movedSecond, firstPreparedExponent)
                } else {
                    val movedFirst = firstPrepared.significand * 10.toBigInteger().pow(moveSecondBy.negate())
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
                        val movedSecond = second.significand * 10.toBigInteger().pow(delta.negate())
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


//
//
//        val firstDigits = first.significand.numberOfDigits()
//        val secondDigits = second.significand.numberOfDigits()
//        val firstExponent = first.exponent
//        val secondExponent = second.exponent
//
//        val firstMove = firstDigits  - 1
//        val secondMove = secondDigits - 1
//
//        val firstFreeOfRadix =
//
//
//        return when {
//            firstExponent > secondExponent -> {
//                val exponentDifference = firstExponent - secondExponent + secondMove - firstMove
//
//                val preparedFirst = first.significand * 10.toBigInteger().pow(exponentDifference)
//                return Triple(preparedFirst, second.significand, firstExponent)
//            }
//            firstExponent < secondExponent -> {
//                val exponentDifference = secondExponent - firstExponent + firstMove - secondMove
//                val preparedSecond = second.significand * 10.toBigInteger().pow(exponentDifference)
//                return Triple(first.significand, preparedSecond, secondExponent)
//            }
//            firstExponent == secondExponent -> {
//                return Triple(first.significand, second.significand, firstExponent)
//            }
//            else -> {
//                throw RuntimeException("Invalid comparison state BigInteger: $firstExponent, $secondExponent")
//            }
//        }

    }

    /**
     * Compare to ther BigDecimal
     */
    fun compare(other: BigDecimal): Int {
        if (exponent == other.exponent) {
            return significand.compare(other.significand)
        }
        val (preparedFirst, preparedSecond) = bringSignificandToSameExponent(this, other)
        return preparedFirst.compare(preparedSecond)
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(BigDecimal.fromLongAsSignificand(other))
            is Int -> compare(BigDecimal.fromIntAsSignificand(other))
            is Short -> compare(BigDecimal.fromShortAsSignificand(other))
            is Byte -> compare(BigDecimal.fromByteAsSignificand(other))
            else -> throw RuntimeException("Invalid comparison type for BigDecimal: ${other::class.simpleName}")
        }

    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(BigDecimal.fromLongAsSignificand(other))
            is Int -> compare(BigDecimal.fromIntAsSignificand(other))
            is Short -> compare(BigDecimal.fromShortAsSignificand(other))
            is Byte -> compare(BigDecimal.fromByteAsSignificand(other))
            else -> -1
        }
        return comparison == 0
    }

    /**
     * Return this BigDecimal in scientific notation
     * i.e. 1.23E+9
     */
    override fun toString(): String {
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
            exponent == BigInteger.ZERO -> {
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
        val digits = significand.numberOfDigits()
        if (exponent > Int.MAX_VALUE) {
            throw RuntimeException("Invalid toStringExpanded request (exponent > Int.MAX_VALUE)")
        }
        val significandString = significand.toString(10)


        return when {
            exponent > 0 -> {
                val diffBigInt = (exponent - digits + 1)
                val diffInt = diffBigInt.magnitude[0].toInt()

                if (diffBigInt > 0) {
                    val expandZeroes = diffBigInt * '0'
                    significandString + expandZeroes
                } else {
                    placeADotInString(significandString, significandString.length - exponent.magnitude[0].toInt() - 1)
                }

            }
            exponent < 0 -> {

                val diffInt = exponent.magnitude[0].toInt()

                if (diffInt > 0) {
                    val expandZeroes = exponent.abs() * '0'
                    placeADotInString(expandZeroes + significandString, diffInt + significandString.length - 1)
                } else {
                    placeADotInString(significandString, significandString.length - 1)
                }
            }
            exponent == BigInteger.ZERO -> "${placeADotInString(significandString, significandString.length - 1)}"

            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
    }

    private fun noExponentStringtoScientificNotation(input: String): String {
        return placeADotInString(input, input.length - 1) + "E+${input.length - 1}"
    }

    private fun placeADotInString(input: String, position: Int): String {

        val prefix = input.substring(0 until input.length - position)
        val suffix = input.substring(input.length - position until input.length)
        val prepared = prefix + '.' + suffix

        return prepared.dropLastWhile { it == '0' }

    }


//
//
// ----------------- Interop with basic types ----------------------
//
//


// ------------- Addition -----------


    operator fun plus(int: Int): BigDecimal {
        return this.plus(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun plus(long: Long): BigDecimal {
        return this.plus(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun plus(short: Short): BigDecimal {
        return this.plus(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun plus(byte: Byte): BigDecimal {
        return this.plus(BigDecimal.fromByteAsSignificand(byte))
    }

// ------------- Multiplication -----------


    operator fun times(int: Int): BigDecimal {
        return this.multiplication(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun times(long: Long): BigDecimal {
        return this.multiplication(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun times(short: Short): BigDecimal {
        return this.multiplication(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun times(byte: Byte): BigDecimal {
        return this.multiplication(BigDecimal.fromByteAsSignificand(byte))
    }


// ------------- Subtraction -----------


    operator fun minus(int: Int): BigDecimal {
        return this.minus(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun minus(long: Long): BigDecimal {
        return this.minus(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun minus(short: Short): BigDecimal {
        return this.minus(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun minus(byte: Byte): BigDecimal {
        return this.minus(BigDecimal.fromByteAsSignificand(byte))
    }

// ------------- Division -----------


    operator fun div(int: Int): BigDecimal {
        return this.div(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun div(long: Long): BigDecimal {
        return this.div(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun div(short: Short): BigDecimal {
        return this.div(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun div(byte: Byte): BigDecimal {
        return this.div(BigDecimal.fromByteAsSignificand(byte))
    }


    operator fun rem(int: Int): BigDecimal {
        return this.rem(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun rem(long: Long): BigDecimal {
        return this.rem(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun rem(short: Short): BigDecimal {
        return this.rem(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun rem(byte: Byte): BigDecimal {
        return this.rem(BigDecimal.fromByteAsSignificand(byte))
    }


}