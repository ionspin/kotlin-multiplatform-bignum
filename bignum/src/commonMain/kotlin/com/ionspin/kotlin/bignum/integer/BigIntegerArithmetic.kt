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
interface BigIntegerArithmetic<BackingCollectionType, BackingWordType> {
    val ZERO: BackingCollectionType
    val ONE: BackingCollectionType
    val TWO: BackingCollectionType
    val TEN: BackingCollectionType

    val basePowerOfTwo: Int
    /**
     * Returns the number of leading zeroes in highest word
     */
    fun numberOfLeadingZeroesInAWord(value: BackingWordType): Int

    /**
     * Number of bits needed to represent this number
     */
    fun bitLength(value: BackingCollectionType): Int

    /**
     * Number of consecutive zeroes count from the right in binary representation
     */
    fun trailingZeroBits(value: BackingCollectionType): Int

    /**
     * Arithmetic shift left. Shifts the number to the left, by required places of bits, creating new words if necessary
     */
    fun shiftLeft(operand: BackingCollectionType, places: Int): BackingCollectionType

    /**
     * Arithmetic shift right. Shifts the number to the right, by required places of bits, removing words that no longer relevant
     */
    fun shiftRight(operand: BackingCollectionType, places: Int): BackingCollectionType

    /**
     * Compares two numbers
     *
     * @return -1 if first is bigger, 0 if equal, +1 if second is bigger
     */
    fun compare(first: BackingCollectionType, second: BackingCollectionType): Int

    /**
     * Adds two big integers
     * @return result of add
     */
    fun add(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Subtracts two big integers
     * @return result of subtract
     */
    fun subtract(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Multiplies two big integers
     * @return result of multiply
     */
    fun multiply(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Divide two big integers
     * @return A pair representing quotient (first member of the pair) and remainder (second member of the pair)
     */
    fun divide(
        first: BackingCollectionType,
        second: BackingCollectionType
    ): Pair<BackingCollectionType, BackingCollectionType>

    /**
     * Returns a integer reciprocal of this number such that 0 <= base ^ word - operand * reciprocal <= operand,
     * and remainder such that 0 < reciprocal < operand
     */
    fun reciprocal(operand: BackingCollectionType): Pair<BackingCollectionType, BackingCollectionType>

    /**
     * Exponentiation function
     * @return BigInteger result of exponentiation of number by exponent
     */
    fun pow(base: BackingCollectionType, exponent: Long): BackingCollectionType

    fun sqrt(operand: BackingCollectionType): Pair<BackingCollectionType, BackingCollectionType>

    fun gcd(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Parse a string in a specific base into a big integer
     */
    fun parseForBase(number: String, base: Int): BackingCollectionType

    /**
     * return a string representation of big integer in a specific number base
     */
    fun toString(operand: BackingCollectionType, base: Int): String

    fun numberOfDecimalDigits(operand: BackingCollectionType): Long

    fun fromULong(uLong: ULong): BackingCollectionType
    fun fromUInt(uInt: UInt): BackingCollectionType
    fun fromUShort(uShort: UShort): BackingCollectionType
    fun fromUByte(uByte: UByte): BackingCollectionType
    fun fromLong(long: Long): BackingCollectionType
    fun fromInt(int: Int): BackingCollectionType
    fun fromShort(short: Short): BackingCollectionType
    fun fromByte(byte: Byte): BackingCollectionType

    fun or(operand: BackingCollectionType, mask: BackingCollectionType): BackingCollectionType
    fun xor(operand: BackingCollectionType, mask: BackingCollectionType): BackingCollectionType
    fun and(operand: BackingCollectionType, mask: BackingCollectionType): BackingCollectionType
    fun not(operand: BackingCollectionType): BackingCollectionType

    fun bitAt(operand: BackingCollectionType, position: Long): Boolean
    fun setBitAt(operand: BackingCollectionType, position: Long, bit: Boolean): BackingCollectionType

    fun toByteArray(operand: BackingCollectionType, sign: Sign): Array<Byte>
    fun fromByteArray(byteArray: Array<Byte>): Pair<BackingCollectionType, Sign>
    fun fromUByteArray(uByteArray: Array<UByte>, endianness: Endianness = Endianness.BIG): Pair<BackingCollectionType, Sign>
    fun toUByteArray(operand: BackingCollectionType, endianness: Endianness = Endianness.BIG): Array<UByte>
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
