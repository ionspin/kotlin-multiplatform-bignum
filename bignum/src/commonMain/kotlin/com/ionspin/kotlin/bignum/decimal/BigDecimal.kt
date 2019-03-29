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
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.math.absoluteValue

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
            return if (decimalMode.roundingMode != RoundingMode.NONE) {
                BigDecimal(significand, exponent)
                //round(significand, exponent, decimalMode)
            } else {
                BigDecimal(significand, exponent)
            }
        }

        private fun round(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            val significandDigits = significand.numberOfDigits()
            if (decimalMode.precision < significandDigits) {

            } else {

            }
            TODO()
        }

        fun fromLong(long: Long) = BigDecimal(BigInteger.fromLong(long), BigInteger.ZERO)
        fun fromInt(int: Int) = BigDecimal(BigInteger.fromInt(int), BigInteger.ZERO)
        fun fromShort(short: Short) = BigDecimal(BigInteger.fromShort(short), BigInteger.ZERO)
        fun fromByte(byte: Byte) = BigDecimal(BigInteger.fromByte(byte), BigInteger.ZERO)

        fun fromBigIntegerWithExponent(bigInteger: BigInteger , exponent: BigInteger) : BigDecimal {
            return BigDecimal(bigInteger, exponent)
        }

        fun fromLongWithExponent(long: Long, exponent: BigInteger): BigDecimal {
            val bigint = BigInteger.fromLong(long)
            return BigDecimal(bigint, exponent)
        }

        fun fromIntWithExponent(int: Int, exponent: BigInteger): BigDecimal {
            val bigint = BigInteger.fromInt(int)
            return BigDecimal(bigint, exponent)
        }

        fun fromShortWithExponent(short: Short, exponent: BigInteger): BigDecimal {
            val bigint = BigInteger.fromShort(short)
            return BigDecimal(bigint, exponent)
        }

        fun fromByteWithExponent(byte: Byte, exponent: BigInteger): BigDecimal {
            val bigint = BigInteger.fromByte(byte)
            return BigDecimal(bigint, exponent)
        }

        fun fromLongWithExponent(long: Long, exponent: Int): BigDecimal =
            fromLongWithExponent(long, exponent.toBigInteger())

        fun fromIntWithExponent(int: Int, exponent: Int): BigDecimal = fromIntWithExponent(int, exponent.toBigInteger())
        fun fromShortWithExponent(short: Short, exponent: Int): BigDecimal =
            fromShortWithExponent(short, exponent.toBigInteger())

        fun fromByteWithExponent(byte: Byte, exponent: Int): BigDecimal =
            fromByteWithExponent(byte, exponent.toBigInteger())
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
        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) { firstNumOfDigits } else { secondNumOfDigits }
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

        val largerOperand = if (firstNumOfDigits > secondNumOfDigits) { firstNumOfDigits } else { secondNumOfDigits }
        val borrowDetected = newSignificandNumOfDigit - largerOperand

        val newExponent = BigInteger.max(this.exponent, other.exponent) + borrowDetected
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }


    internal fun multiply(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
//        val (first, second) = bringSignificandToSameExponent(this, other)
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
        if (divRem.remainder != BigInteger.ZERO) {
            when (decimalMode.roundingMode) {
                RoundingMode.NONE -> { throw ArithmeticException("Non-terminating result of division operation. Specify precision")}
                RoundingMode.UP -> { if (result.sign == Sign.POSITIVE) { result++ } else {result -- } }
                RoundingMode.DOWN -> { if (result.sign == Sign.NEGATIVE) { result++ } else {result -- } }
                else -> {}
            }
        }
        return roundOrDont(result, newExponent, decimalMode)

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



    private fun getRidOfRadix(bigDecimal: BigDecimal) : BigDecimal {
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
                val moveFirstBy  = firstPreparedExponent - secondPreparedExponent
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
                return if (moveSecondBy >= 0 ) {
                    val movedSecond = secondPrepared.significand * 10.toBigInteger().pow(moveSecondBy)
                    Triple(first.significand, movedSecond, firstPreparedExponent)
                } else {
                    val movedFirst = firstPrepared.significand * 10.toBigInteger().pow(moveSecondBy.negate())
                    Triple(movedFirst, second.significand, firstPreparedExponent)
                }
            }
            first.exponent == second.exponent -> {
                val delta = firstPreparedExponent - secondPreparedExponent
                return when  {
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
            is Long -> compare(BigDecimal.fromLong(other))
            is Int -> compare(BigDecimal.fromInt(other))
            is Short -> compare(BigDecimal.fromShort(other))
            is Byte -> compare(BigDecimal.fromByte(other))
            else -> throw RuntimeException("Invalid comparison type for BigDecimal: ${other::class.simpleName}")
        }

    }

    override fun equals(other: Any?): Boolean {
        val comparison = when (other) {
            is BigDecimal -> compare(other)
            is Long -> compare(BigDecimal.fromLong(other))
            is Int -> compare(BigDecimal.fromInt(other))
            is Short -> compare(BigDecimal.fromShort(other))
            is Byte -> compare(BigDecimal.fromByte(other))
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
        val expand = if (significandString.length == 1) {
            "0"
        } else {
            ""
        }

        return when {
            exponent > 0 -> "${placeADotInString(significandString, significandString.length - modifier)}${expand}E+$exponent"
            exponent < 0 -> "${placeADotInString(significandString, significandString.length - modifier)}${expand}E$exponent"
            exponent == BigInteger.ZERO -> "${placeADotInString(significandString, significandString.length - modifier)}0"
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
        return this.plus(BigDecimal.fromInt(int))
    }


    operator fun plus(long: Long): BigDecimal {
        return this.plus(BigDecimal.fromLong(long))
    }


    operator fun plus(short: Short): BigDecimal {
        return this.plus(BigDecimal.fromShort(short))
    }


    operator fun plus(byte: Byte): BigDecimal {
        return this.plus(BigDecimal.fromByte(byte))
    }

    // ------------- Multiplication -----------


    operator fun times(int: Int): BigDecimal {
        return this.multiply(BigDecimal.fromInt(int))
    }


    operator fun times(long: Long): BigDecimal {
        return this.multiply(BigDecimal.fromLong(long))
    }


    operator fun times(short: Short): BigDecimal {
        return this.multiply(BigDecimal.fromShort(short))
    }


    operator fun times(byte: Byte): BigDecimal {
        return this.multiply(BigDecimal.fromByte(byte))
    }


    // ------------- Subtraction -----------


    operator fun minus(int: Int): BigDecimal {
        return this.minus(BigDecimal.fromInt(int))
    }


    operator fun minus(long: Long): BigDecimal {
        return this.minus(BigDecimal.fromLong(long))
    }


    operator fun minus(short: Short): BigDecimal {
        return this.minus(BigDecimal.fromShort(short))
    }


    operator fun minus(byte: Byte): BigDecimal {
        return this.minus(BigDecimal.fromByte(byte))
    }

    // ------------- Division -----------


    operator fun div(int: Int): BigDecimal {
        return this.div(BigDecimal.fromInt(int))
    }


    operator fun div(long: Long): BigDecimal {
        return this.div(BigDecimal.fromLong(long))
    }


    operator fun div(short: Short): BigDecimal {
        return this.div(BigDecimal.fromShort(short))
    }


    operator fun div(byte: Byte): BigDecimal {
        return this.div(BigDecimal.fromByte(byte))
    }


    operator fun rem(int: Int): BigDecimal {
        return this.rem(BigDecimal.fromInt(int))
    }


    operator fun rem(long: Long): BigDecimal {
        return this.rem(BigDecimal.fromLong(long))
    }


    operator fun rem(short: Short): BigDecimal {
        return this.rem(BigDecimal.fromShort(short))
    }


    operator fun rem(byte: Byte): BigDecimal {
        return this.rem(BigDecimal.fromByte(byte))
    }


}