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

package com.ionspin.kotlin.bignum


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Apr-2019
 */
interface BigNumber<BigType> {
    fun add(other: BigType): BigType
    fun subtract(other: BigType): BigType
    fun multiply(other: BigType): BigType
    fun divide(other: BigType): BigType
    fun remainder(other: BigType): BigType
    fun divideAndRemainder(other: BigType): Pair<BigType, BigType>
    fun compare(other: BigType): Int
    fun isZero(): Boolean
    fun negate(): BigType
    fun abs(): BigType
    fun pow(exponent: BigType) : BigType
    fun pow(exponent: Long): BigType
    fun pow(exponent: Int): BigType
    fun signum(): Int
    fun bitAt(position: Long): Boolean
    fun setBitAt(position: Long, bit : Boolean) : BigType
    fun numberOfDigits() : Long

    infix fun shl(places: Int): BigType

    infix fun shr(places: Int): BigType

    operator fun unaryMinus(): BigType

    operator fun plus(other: BigType): BigType

    operator fun minus(other: BigType): BigType

    operator fun times(other: BigType): BigType

    operator fun div(other: BigType): BigType

    operator fun rem(other: BigType): BigType

    infix fun and(other: BigType): BigType

    infix fun or(other: BigType): BigType

    infix fun xor(other: BigType): BigType
    /**
     * Inverts only up to chosen [arithmetic] [BigTypeArithmetic.bitLength] bits.
     * This is different from Java biginteger which returns inverse in two's complement.
     *
     * I.e.: If the number was "1100" binary, invPrecise returns "0011" => "11" => 4 decimal
     */
    fun invPrecise(): BigType

    fun compareTo(other: Any): Int
    override fun equals(other: Any?): Boolean

    override fun toString(): String
    fun toString(base: Int): String

    operator fun plus(int: Int): BigType

    operator fun plus(long: Long): BigType

    operator fun plus(short: Short): BigType

    operator fun plus(byte: Byte): BigType

    operator fun times(int: Int): BigType

    operator fun times(long: Long): BigType

    operator fun times(short: Short): BigType

    operator fun times(byte: Byte): BigType
    
    operator fun times(char: Char) : String

    operator fun minus(int: Int): BigType

    operator fun minus(long: Long): BigType

    operator fun minus(short: Short): BigType

    operator fun minus(byte: Byte): BigType

    operator fun div(int: Int): BigType

    operator fun div(long: Long): BigType

    operator fun div(short: Short): BigType

    operator fun div(byte: Byte): BigType

    operator fun rem(int: Int): BigType

    operator fun rem(long: Long): BigType

    operator fun rem(short: Short): BigType

    operator fun rem(byte: Byte): BigType

}