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

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Apr-2019
 */


interface BigNumber<BigType> where BigType : BigNumber<BigType> {


    interface Creator<BigType> {
        val ZERO: BigType
        val ONE: BigType
        val TWO: BigType
        val TEN: BigType
        fun parseString(string: String, base: Int = 10): BigType
        fun fromULong(uLong: ULong): BigType
        fun fromUInt(uInt: UInt): BigType
        fun fromUShort(uShort: UShort): BigType
        fun fromUByte(uByte: UByte): BigType
        fun fromLong(long: Long): BigType
        fun fromInt(int: Int): BigType
        fun fromShort(short: Short): BigType
        fun fromByte(byte: Byte): BigType
        fun fromBigInteger(bigInteger: BigInteger): BigType
        fun tryFromFloat(float: Float, exactRequired: Boolean = false): BigType
        fun tryFromDouble(double: Double, exactRequired: Boolean = false): BigType
    }

    interface Util<BigType> {
        fun max(first: BigType, second: BigType): BigType
        fun min(first: BigType, second: BigType): BigType
    }

    fun getCreator(): Creator<BigType>

    fun add(other: BigType): BigType
    fun subtract(other: BigType): BigType
    fun multiply(other: BigType): BigType
    fun divide(other: BigType): BigType

    /**
     * Remainder of integer division operation. Remainder has same sign as dividend.
     */
    fun remainder(other: BigType): BigType

    /**
     * Perform integer division and return quotient and remainder
     */
    fun divideAndRemainder(other: BigType): Pair<BigType, BigType>

    fun isZero(): Boolean
    /**
     * Return additive inverse of this number
     *
     * i.e.
     * ```  val a = 5.toBigInteger()
     *      b = a.negate
     *      b == -5.toBigInteger()
     * ```
     */
    fun negate(): BigType

    /**
     * Return absolute value of this big integer
     *
     * i.e.
     * ```  val a = -5.toBigInteger()
     *      b = a.negate
     *      b == -5.toBigInteger()
     * ```
     */
    fun abs(): BigType
    // TODO Implement in 0.3.0 when BigDecimal has log, pow, sqrt and the rest of the company
//    /**
//     * Return result of exponentiation of this number by supplied exponent
//     * i.e.
//     * ```  val a = 10.toBigInteger()
//     *      b = a.exp(2.toBigInteger())
//     *      b == 100.toBigInteger()
//     * ```
//     */
//    fun pow(exponent: BigType) : BigType
    /**
     * Return result of exponentiation of this number by supplied long exponent
     * i.e.
     * ```  val a = 10.toBigInteger()
     *      b = a.exp(2L)
     *      b == 100.toBigInteger()
     * ```
     */
    fun pow(exponent: Long): BigType

    /**
     * Return result of exponentiation of this number by supplied integer exponent
     * i.e.
     * ```  val a = 10.toBigInteger()
     *      b = a.exp(2)
     *      b == 100.toBigInteger()
     * ```
     */
    fun pow(exponent: Int): BigType

    fun signum(): Int

    /**
     * Return the number of decimal digits representing this number
     */
    fun numberOfDecimalDigits(): Long

    fun compareTo(other: Any): Int
    override fun equals(other: Any?): Boolean

    override fun toString(): String
    fun toString(base: Int): String

    operator fun unaryMinus(): BigType
}

internal interface NarrowingOperations<BigType> where BigType : BigNumber<BigType> {
    fun intValue(exactRequired: Boolean = false): Int
    fun longValue(exactRequired: Boolean = false): Long
    fun byteValue(exactRequired: Boolean = false): Byte
    fun shortValue(exactRequired: Boolean = false): Short
    fun uintValue(exactRequired: Boolean = false): UInt
    fun ulongValue(exactRequired: Boolean = false): ULong
    fun ubyteValue(exactRequired: Boolean = false): UByte
    fun ushortValue(exactRequired: Boolean = false): UShort
    fun floatValue(exactRequired: Boolean = false): Float
    fun doubleValue(exactRequired: Boolean = false): Double
}


internal interface CommonBigNumberOperations<BigType> where BigType : BigNumber<BigType> {

    fun getCreator(): BigNumber.Creator<BigType>

    fun getInstance(): BigType

    operator fun plus(other: BigType): BigType = getInstance().add(other)

    operator fun minus(other: BigType): BigType = getInstance().subtract(other)

    operator fun times(other: BigType): BigType = getInstance().multiply(other)

    operator fun div(other: BigType): BigType = getInstance().divide(other)

    /**
     * Remainder of integer division operation. Returns the *least absolute remainder* (remainder has same sign as dividend)
     */
    operator fun rem(other: BigType): BigType = getInstance().remainder(other)

    operator fun plus(int: Int): BigType = getInstance().add(getCreator().fromInt(int))

    operator fun plus(long: Long): BigType = getInstance().add(getCreator().fromLong(long))

    operator fun plus(short: Short): BigType = getInstance().add(getCreator().fromShort(short))

    operator fun plus(byte: Byte): BigType = getInstance().add(getCreator().fromByte(byte))

    operator fun times(int: Int): BigType = getInstance().multiply(getCreator().fromInt(int))

    operator fun times(long: Long): BigType = getInstance().multiply(getCreator().fromLong(long))

    operator fun times(short: Short): BigType = getInstance().multiply(getCreator().fromShort(short))

    operator fun times(byte: Byte): BigType = getInstance().multiply(getCreator().fromByte(byte))

    operator fun minus(int: Int): BigType = getInstance().subtract(getCreator().fromInt(int))

    operator fun minus(long: Long): BigType = getInstance().subtract(getCreator().fromLong(long))

    operator fun minus(short: Short): BigType = getInstance().subtract(getCreator().fromShort(short))

    operator fun minus(byte: Byte): BigType = getInstance().subtract(getCreator().fromByte(byte))

    operator fun div(int: Int): BigType = getInstance().divide(getCreator().fromInt(int))

    operator fun div(long: Long): BigType = getInstance().divide(getCreator().fromLong(long))

    operator fun div(short: Short): BigType = getInstance().divide(getCreator().fromShort(short))

    operator fun div(byte: Byte): BigType = getInstance().divide(getCreator().fromByte(byte))

    /**
     * Remainder of integer division operation. Remainder has same sign as dividend
     */
    operator fun rem(int: Int): BigType = getInstance().remainder(getCreator().fromInt(int))

    /**
     * Remainder of integer division operation. Remainder has same sign as dividend
     */
    operator fun rem(long: Long): BigType = getInstance().remainder(getCreator().fromLong(long))

    /**
     * Remainder of integer division operation. Remainder has same sign as dividend
     */
    operator fun rem(short: Short): BigType = getInstance().remainder(getCreator().fromShort(short))

    /**
     * Remainder of integer division operation. Remainder has same sign as dividend.
     */
    operator fun rem(byte: Byte): BigType = getInstance().remainder(getCreator().fromByte(byte))
}

interface BitwiseCapable<BigType> {

    infix fun shl(places: Int): BigType

    infix fun shr(places: Int): BigType

    infix fun and(other: BigType): BigType

    infix fun or(other: BigType): BigType

    infix fun xor(other: BigType): BigType

    fun bitAt(position: Long): Boolean

    fun setBitAt(position: Long, bit: Boolean): BigType

    /**
     * Inverts only up to chosen [arithmetic] [BigTypeArithmetic.bitLength] bits.
     * This is different from Java biginteger which returns inverse in two's complement.
     *
     * I.e.: If the number was "1100" binary, not returns "0011" => "11" => 4 decimal
     */
    fun not(): BigType
}


interface ByteArraySerializable {

    fun oldToTypedByteArray(): Array<Byte>
    fun oldToByteArray(): ByteArray
    fun oldToTypedUByteArray(endianness: Endianness = Endianness.BIG): Array<UByte>
    fun oldToUByteArray(endianness: Endianness = Endianness.BIG): UByteArray

    fun toUByteArray(byteArrayRepresentation: ByteArrayRepresentation, endianness: Endianness, twosComplement: Boolean) : UByteArray
    fun toByteArray(byteArrayRepresentation: ByteArrayRepresentation, endianness: Endianness, twosComplement: Boolean) : ByteArray
}


interface ByteArrayDeserializable<BigType : BigNumber<BigType>> {
    fun oldFromByteArray(byteArray: Array<Byte>): BigType
    fun oldFromByteArray(byteArray: ByteArray): BigType
    fun oldFromUByteArray(uByteArray: Array<UByte>, endianness: Endianness = Endianness.BIG): BigType
    fun oldFromUByteArray(uByteArray: UByteArray, endianness: Endianness = Endianness.BIG): BigType

    fun fromUByteArray(source: UByteArray, byteArrayRepresentation: ByteArrayRepresentation, endianness: Endianness, twosComplement: Boolean) : BigType
    fun fromByteArray(source: ByteArray, byteArrayRepresentation: ByteArrayRepresentation, endianness: Endianness, twosComplement: Boolean) : BigType
}
