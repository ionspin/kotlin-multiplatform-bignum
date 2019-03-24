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
import kotlin.math.absoluteValue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-3/23/19
 */

@ExperimentalUnsignedTypes
class BigDecimal(val significand: BigInteger, val exponent: BigInteger) {

    companion object {

        private fun roundOrDont(significand: BigInteger, exponent: BigInteger, decimalMode: DecimalMode): BigDecimal {
            return if (decimalMode.roundingMode != RoundingMode.NONE) {
                BigDecimal(significand, exponent).round(decimalMode)
            } else {
                BigDecimal(significand, exponent)
            }
        }

        fun fromLong(long: Long) = BigDecimal(BigInteger.fromLong(long), BigInteger.ZERO)
        fun fromInt(int: Int) = BigDecimal(BigInteger.fromInt(int), BigInteger.ZERO)
        fun fromShort(short: Short) = BigDecimal(BigInteger.fromShort(short), BigInteger.ZERO)
        fun fromByte(byte: Byte) = BigDecimal(BigInteger.fromByte(byte), BigInteger.ZERO)

        fun fromLongWithExponent(long: Long, exponent: BigInteger) = BigDecimal(BigInteger.fromLong(long), exponent)
        fun fromIntWithExponent(int: Int, exponent: BigInteger) = BigDecimal(BigInteger.fromInt(int), exponent)
        fun fromShortWithExponent(short: Short, exponent: BigInteger) =
            BigDecimal(BigInteger.fromShort(short), exponent)

        fun fromByteWithExponent(byte: Byte, exponent: BigInteger) = BigDecimal(BigInteger.fromByte(byte), exponent)
    }

    val isExponentLong = exponent.numberOfWords == 0
    val longExponent = exponent.magnitude[0]


    fun plus(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = BigInteger.max(this.exponent, other.exponent)
        val newSignificand = this.significand + other.significand
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    fun minus(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = BigInteger.max(this.exponent, other.exponent)
        val newSignificand = this.significand - other.significand
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }


    internal fun multiply(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = exponent * other.exponent
        val newSignificand = this.significand * other.significand
        return if (decimalMode.roundingMode != RoundingMode.NONE) {
            BigDecimal(newSignificand, newExponent).round(decimalMode)
        } else {
            BigDecimal(newSignificand, newExponent)
        }

    }


    fun div(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand / other.significand //TODO
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    //TODO
    fun integerDiv(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = this.exponent - other.exponent
        val newSignificand = this.significand / other.significand
        return roundOrDont(newSignificand, newExponent, decimalMode)
    }

    fun rem(other: BigDecimal, decimalMode: DecimalMode = DecimalMode()): BigDecimal {
        val newExponent = this.exponent - other.exponent //TODO
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
//        return BigDecimal(significand.pow(powerExponent), exponent)
        return BigDecimal(significand, exponent * powerExponent)
    }

    private fun round(decimalMode: DecimalMode): BigDecimal {

        TODO()
    }

    override fun toString(): String {
        val significandString = significand.toString(10)
        return when {
            exponent > 0 -> significandString + "E$exponent"
            exponent < 0 -> "0.${significandString}E$exponent"
            exponent == BigInteger.ZERO -> significandString
            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
    }

    fun toStringExpanded(): String {
        if (exponent > Int.MAX_VALUE) {
            throw RuntimeException("Invalid toStringExpanded request (expoenent > Int.MAX_VALUE)")
        }
        val significandString = significand.toString(10)
        return when {
            exponent > 0 -> significandString + (exponent * '0')
            exponent < 0 -> placeADotInString(significandString, exponent.magnitude[0].toInt().absoluteValue)
            exponent == BigInteger.ZERO -> significandString
            else -> throw RuntimeException("Invalid state, please report a bug (Integer compareTo invalid)")
        }
    }

    private fun placeADotInString(input: String, position: Int): String {
        val prepared = if (input.length < position) {
            val builder = buildString(input.length + (position - input.length)) {
                for (i in 0 until position - input.length) {
                    this.append('0')
                }
                append(input)
            }
            "0.${builder}"
        } else {
            val prefix = input.substring(0 until input.length - position)
            val suffix = input.substring(input.length - position until input.length)
            prefix + '.' + suffix
        }
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