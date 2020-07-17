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

/**
 * Interface defining big integer operations
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

interface BigIntegerList63Arithmetic {
    val ZERO: List<ULong>
    val ONE: List<ULong>
    val TWO: List<ULong>
    val TEN: List<ULong>

    val basePowerOfTwo: Int
    /**
     * Returns the number of leading zeros in a word
     */
    fun numberOfLeadingZerosInAWord(value: ULong): Int

    /**
     * Number of bits needed to represent this number
     */
    fun bitLength(value: List<ULong>): Int

    /**
     * Number of consecutive zeros count from the right in binary representation
     */
    fun trailingZeroBits(value: List<ULong>): Int

    /**
     * Arithmetic shift left. Shifts the number to the left, by required places of bits, creating new words if necessary
     */
    fun shiftLeft(operand: List<ULong>, places: Int): List<ULong>

    /**
     * Arithmetic shift right. Shifts the number to the right, by required places of bits, removing words that no longer relevant
     */
    fun shiftRight(operand: List<ULong>, places: Int): List<ULong>

    /**
     * Compares two numbers
     *
     * @return -1 if first is bigger, 0 if equal, +1 if second is bigger
     */
    fun compare(first: List<ULong>, second: List<ULong>): Int

    /**
     * Adds two big integers
     * @return result of add
     */
    fun add(first: List<ULong>, second: List<ULong>): List<ULong>

    /**
     * Subtracts two big integers
     * @return result of subtract
     */
    fun subtract(first: List<ULong>, second: List<ULong>): List<ULong>

    /**
     * Multiplies two big integers
     * @return result of multiply
     */
    fun multiply(first: List<ULong>, second: List<ULong>): List<ULong>

    /**
     * Divide two big integers
     * @return A pair representing quotient (first member of the pair) and remainder (second member of the pair)
     */
    fun divide(
        first: List<ULong>,
        second: List<ULong>
    ): Pair<List<ULong>, List<ULong>>

    /**
     * Returns a integer reciprocal of this number such that 0 <= base ^ word - operand * reciprocal <= operand,
     * and remainder such that 0 < reciprocal < operand
     */
    fun reciprocal(operand: List<ULong>): Pair<List<ULong>, List<ULong>>

    /**
     * Exponentiation function
     * @return BigInteger result of exponentiation of number by exponent
     */
    fun pow(base: List<ULong>, exponent: Long): List<ULong>

    fun sqrt(operand: List<ULong>): Pair<List<ULong>, List<ULong>>

    fun gcd(first: List<ULong>, second: List<ULong>): List<ULong>

    /**
     * Parse a string in a specific base into a big integer
     */
    fun parseForBase(number: String, base: Int): List<ULong>

    /**
     * return a string representation of big integer in a specific number base
     */
    fun toString(operand: List<ULong>, base: Int): String

    fun numberOfDecimalDigits(operand: List<ULong>): Long

    fun fromULong(uLong: ULong): List<ULong>
    fun fromUInt(uInt: UInt): List<ULong>
    fun fromUShort(uShort: UShort): List<ULong>
    fun fromUByte(uByte: UByte): List<ULong>
    fun fromLong(long: Long): List<ULong>
    fun fromInt(int: Int): List<ULong>
    fun fromShort(short: Short): List<ULong>
    fun fromByte(byte: Byte): List<ULong>

    fun or(operand: List<ULong>, mask: List<ULong>): List<ULong>
    fun xor(operand: List<ULong>, mask: List<ULong>): List<ULong>
    fun and(operand: List<ULong>, mask: List<ULong>): List<ULong>
    fun not(operand: List<ULong>): List<ULong>

    fun bitAt(operand: List<ULong>, position: Long): Boolean
    fun setBitAt(operand: List<ULong>, position: Long, bit: Boolean): List<ULong>

    fun fromUByteArray(source: UByteArray): List<ULong>
    fun fromByteArray(source: ByteArray): List<ULong>

    fun toUByteArray(operand: List<ULong>): UByteArray
    fun toByteArray(operand: List<ULong>): ByteArray
}
