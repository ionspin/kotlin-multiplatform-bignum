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

import com.ionspin.kotlin.bignum.Endianness

/**
 * Interface defining big integer operations
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */
@ExperimentalUnsignedTypes
interface BigIntegerArithmetic {
    val _emitLongArray: LongArray
    val ZERO: ULongArray
    val ONE: ULongArray
    val TWO: ULongArray
    val TEN: ULongArray

    val basePowerOfTwo: Int
    /**
     * Returns the number of leading zeros in a word
     */
    fun numberOfLeadingZerosInAWord(value: ULong): Int

    /**
     * Number of bits needed to represent this number
     */
    fun bitLength(value: ULongArray): Int

    /**
     * Number of consecutive zeros count from the right in binary representation
     */
    fun trailingZeroBits(value: ULongArray): Int

    /**
     * Arithmetic shift left. Shifts the number to the left, by required places of bits, creating new words if necessary
     */
    fun shiftLeft(operand: ULongArray, places: Int): ULongArray

    /**
     * Arithmetic shift right. Shifts the number to the right, by required places of bits, removing words that no longer relevant
     */
    fun shiftRight(operand: ULongArray, places: Int): ULongArray

    /**
     * Compares two numbers
     *
     * @return -1 if first is bigger, 0 if equal, +1 if second is bigger
     */
    fun compare(first: ULongArray, second: ULongArray): Int

    /**
     * Adds two big integers
     * @return result of add
     */
    fun add(first: ULongArray, second: ULongArray): ULongArray

    /**
     * Subtracts two big integers
     * @return result of subtract
     */
    fun subtract(first: ULongArray, second: ULongArray): ULongArray

    /**
     * Multiplies two big integers
     * @return result of multiply
     */
    fun multiply(first: ULongArray, second: ULongArray): ULongArray

    /**
     * Divide two big integers
     * @return A pair representing quotient (first member of the pair) and remainder (second member of the pair)
     */
    fun divide(
        first: ULongArray,
        second: ULongArray
    ): Pair<ULongArray, ULongArray>

    /**
     * Returns a integer reciprocal of this number such that 0 <= base ^ word - operand * reciprocal <= operand,
     * and remainder such that 0 < reciprocal < operand
     */
    fun reciprocal(operand: ULongArray): Pair<ULongArray, ULongArray>

    /**
     * Exponentiation function
     * @return BigInteger result of exponentiation of number by exponent
     */
    fun pow(base: ULongArray, exponent: Long): ULongArray

    fun sqrt(operand: ULongArray): Pair<ULongArray, ULongArray>

    fun gcd(first: ULongArray, second: ULongArray): ULongArray

    /**
     * Parse a string in a specific base into a big integer
     */
    fun parseForBase(number: String, base: Int): ULongArray

    /**
     * return a string representation of big integer in a specific number base
     */
    fun toString(operand: ULongArray, base: Int): String

    fun numberOfDecimalDigits(operand: ULongArray): Long

    fun fromULong(uLong: ULong): ULongArray
    fun fromUInt(uInt: UInt): ULongArray
    fun fromUShort(uShort: UShort): ULongArray
    fun fromUByte(uByte: UByte): ULongArray
    fun fromLong(long: Long): ULongArray
    fun fromInt(int: Int): ULongArray
    fun fromShort(short: Short): ULongArray
    fun fromByte(byte: Byte): ULongArray

    fun or(operand: ULongArray, mask: ULongArray): ULongArray
    fun xor(operand: ULongArray, mask: ULongArray): ULongArray
    fun and(operand: ULongArray, mask: ULongArray): ULongArray
    fun not(operand: ULongArray): ULongArray

    fun bitAt(operand: ULongArray, position: Long): Boolean
    fun setBitAt(operand: ULongArray, position: Long, bit: Boolean): ULongArray

    fun toByteArray(operand: ULongArray, sign: Sign): Array<Byte>
    fun fromByteArray(byteArray: Array<Byte>): Pair<ULongArray, Sign>
    fun fromByteArray(byteArray: ByteArray): Pair<ULongArray, Sign>
    fun fromUByteArray(uByteArray: Array<UByte>, endianness: Endianness = Endianness.BIG): Pair<ULongArray, Sign>
    fun fromUByteArray(uByteArray: UByteArray, endianness: Endianness = Endianness.BIG): Pair<ULongArray, Sign>
    fun toTypedUByteArray(operand: ULongArray, endianness: Endianness = Endianness.BIG): Array<UByte>
    fun toUByteArray(operand: ULongArray, endianness: Endianness = Endianness.BIG): UByteArray
}

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
