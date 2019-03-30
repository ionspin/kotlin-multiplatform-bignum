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
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

@ExperimentalUnsignedTypes
class BigDecimal private constructor(
    val significand: BigInteger,
    val exponent: BigInteger = BigInteger.ZERO,
    val decimalMode: DecimalMode = DecimalMode()
) : Comparable<Any> {

    val precision = significand.numberOfDigits()

    companion object {
        val ZERO = BigDecimal(BigInteger.ZERO)
        val ONE = BigDecimal(BigInteger.ONE)


        private fun roundOrDont(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            return if (decimalMode.precision != 0L && decimalMode.roundingMode != RoundingMode.NONE) {
                round(significand, exponent, decimalMode)
            } else {
                BigDecimal(significand, exponent)
            }
        }

        private fun roundDiscarded(
            significand: BigInteger,
            discarded: BigInteger,
            decimalMode: DecimalMode
        ): BigInteger {
            var result = significand
            val sign = if (significand == BigInteger.ZERO) {
                discarded.sign
            } else {
                significand.sign
            }
            val significantRemainderDigit = (discarded / (discarded.numberOfDigits())).abs()
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
                    throw ArithmeticException("Non-terminating result of division operation. Specify precision")
                }
            }
            return result
        }

        private fun round(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            if (significand == BigInteger.ZERO) {
                return BigDecimal(BigInteger.ZERO, exponent, decimalMode)
            }
            val significandDigits = significand.numberOfDigits()
            val desiredPrecision = decimalMode.precision
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

        fun fromLong(long: Long, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
        }

        fun fromInt(int: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
        }

        fun fromShort(short: Short, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
        }

        fun fromByte(byte: Byte, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, bigint.numberOfDigits().toBigInteger() - 1, decimalMode).round(decimalMode)
        }

        fun fromLongAsSignificand(long: Long, decimalMode: DecimalMode = DecimalMode()) =
            BigDecimal(BigInteger.fromLong(long), BigInteger.ZERO, decimalMode).round(decimalMode)

        fun fromIntAsSignificand(int: Int, decimalMode: DecimalMode = DecimalMode()) =
            BigDecimal(BigInteger.fromInt(int), BigInteger.ZERO, decimalMode).round(decimalMode)

        fun fromShortAsSignificand(short: Short, decimalMode: DecimalMode = DecimalMode()) =
            BigDecimal(BigInteger.fromShort(short), BigInteger.ZERO, decimalMode).round(decimalMode)

        fun fromByteAsSignificand(byte: Byte, decimalMode: DecimalMode = DecimalMode()) =
            BigDecimal(BigInteger.fromByte(byte), BigInteger.ZERO, decimalMode).round(decimalMode)

        fun fromFloat(float: Float, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val exponent = float.toBits() and 0x7F800000
            val significand = float.toBits() and 0x007fffff
            return BigDecimal(BigInteger.fromInt(significand), BigInteger.fromInt(exponent), decimalMode).round(
                decimalMode
            )
        }

        fun fromDouble(double: Double, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val rawbit = double.toRawBits()
            val bit = double.toBits()
            val exponent = (double.toBits() and 0x7FF0000000000000L shr 52) - 1023
            val significand = double.toBits() and 0x000FFFFFFFFFFFFFL
            return BigDecimal(BigInteger.fromLong(significand), BigInteger.fromLong(exponent), decimalMode).round(
                decimalMode
            )
        }

        fun fromBigIntegerWithExponent(
            bigInteger: BigInteger,
            exponent: BigInteger,
            decimalMode: DecimalMode = DecimalMode()
        ): BigDecimal {
            return BigDecimal(bigInteger, exponent, decimalMode).round(decimalMode)
        }

        fun fromLongWithExponent(
            long: Long,
            exponent: BigInteger,
            decimalMode: DecimalMode = DecimalMode()
        ): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }

        fun fromIntWithExponent(int: Int, exponent: BigInteger, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }

        fun fromShortWithExponent(
            short: Short,
            exponent: BigInteger,
            decimalMode: DecimalMode = DecimalMode()
        ): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }

        fun fromByteWithExponent(
            byte: Byte,
            exponent: BigInteger,
            decimalMode: DecimalMode = DecimalMode()
        ): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, exponent, decimalMode).round(decimalMode)
        }

        fun fromLongWithExponent(long: Long, exponent: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal =
            fromLongWithExponent(long, exponent.toBigInteger(), decimalMode)

        fun fromIntWithExponent(int: Int, exponent: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal =
            fromIntWithExponent(int, exponent.toBigInteger())

        fun fromShortWithExponent(short: Short, exponent: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal =
            fromShortWithExponent(short, exponent.toBigInteger(), decimalMode)

        fun fromByteWithExponent(byte: Byte, exponent: Int, decimalMode: DecimalMode = DecimalMode()): BigDecimal =
            fromByteWithExponent(byte, exponent.toBigInteger(), decimalMode)

        fun parseString(floatingPointString: String, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
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
                        if (exponentSplit[0] != '-' && exponentSplit[0] != '+') {
                            throw ArithmeticException("Invalid floating point format! $floatingPointString")
                        }
                        val exponentSign = if (exponentSplit[0] == '-') {
                            Sign.NEGATIVE
                        } else {
                            Sign.POSITIVE
                        }
                        val exponentString = exponentSplit.substring(startIndex = 1)
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
                    return BigDecimal(BigInteger.parseString(floatingPointString, 10), BigInteger.ZERO, decimalMode)
                }
            }


        }
    }

    val isExponentLong = exponent.numberOfWords == 0
    val longExponent = exponent.magnitude[0]


    fun plus(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val (first, second, exponent) = bringSignificandToSameExponent(this, other)
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

        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    fun minus(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val (first, second, exponent) = bringSignificandToSameExponent(this, other)

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
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }


    internal fun multiply(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newSignificand = this.significand * other.significand
        val newExponent = this.exponent + other.exponent
        return roundOrDont(newSignificand, newExponent, decimalMode)

    }


    fun div(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        var newExponent = this.exponent - other.exponent - 1

        val desiredPrecision = if (decimalMode.precision == 0L) {
            val precisionSum = this.precision + other.precision
            if (precisionSum < this.precision) {
                Long.MAX_VALUE
            } else {
                precisionSum
            }
        } else {
            decimalMode.precision
        }


        val thisPrepared = this.significand * 10.toBigInteger().pow(desiredPrecision - this.precision + other.precision)


        var divRem = thisPrepared divrem other.significand
        var result = divRem.quotient
        if (result == BigInteger.ZERO) {
            newExponent--
        }
        return BigDecimal(roundDiscarded(result, divRem.remainder, decimalMode), newExponent, decimalMode)

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
//            //Until rounding and precision is fully implemented
//            if (counter == 100) {
//                break
//            }
//            result = result * 10 + divRem.quotient
//        }
//        return roundOrDont(result, newExponent, decimalMode)
    }

    //TODO
    fun integerDiv(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand / other.significand
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    fun rem(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val (first, second) = bringSignificandToSameExponent(this, other)
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand % other.significand
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    //TODO
    fun divrem(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): Pair<BigDecimal, BigDecimal> {
        val newExponent = BigInteger.max(this.exponent, other.exponent)
        val newSignificand = this.significand / other.significand
        val newRemainderSignificand = this.significand % other.significand
        return Pair(
            roundOrDont(newSignificand, newExponent, decimalMode),
            roundOrDont(newRemainderSignificand, newExponent, decimalMode)
        )
    }

    operator fun plus(other: BigDecimal): BigDecimal {
        return this.plus(other, DecimalMode())
    }

    operator fun minus(other: BigDecimal): BigDecimal {
        return this.minus(other, DecimalMode())
    }

    operator fun times(other: BigDecimal): BigDecimal {
        return this.multiply(other, DecimalMode())
    }

    operator fun div(other: BigDecimal): BigDecimal {
        return this.div(other, DecimalMode())
    }

    operator fun rem(other: BigDecimal): BigDecimal {
        return this.rem(other, DecimalMode())
    }

    fun unaryMinus(): BigDecimal {
        return BigDecimal(significand.negate(), exponent)
    }

    fun inc(): BigDecimal {
        return this + 1
    }

    fun dec(): BigDecimal {
        return this - 1
    }

    fun abs(): BigDecimal {
        return BigDecimal(significand.abs(), exponent)
    }

    fun negate(): BigDecimal {
        return BigDecimal(significand.negate(), exponent)
    }

    fun pow(powerExponent: Long): BigDecimal {
        return BigDecimal(significand, exponent * powerExponent)
    }

    fun round(decimalMode: DecimalMode): BigDecimal {
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
        return this.multiply(BigDecimal.fromIntAsSignificand(int))
    }


    operator fun times(long: Long): BigDecimal {
        return this.multiply(BigDecimal.fromLongAsSignificand(long))
    }


    operator fun times(short: Short): BigDecimal {
        return this.multiply(BigDecimal.fromShortAsSignificand(short))
    }


    operator fun times(byte: Byte): BigDecimal {
        return this.multiply(BigDecimal.fromByteAsSignificand(byte))
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