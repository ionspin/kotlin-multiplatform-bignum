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

import com.ionspin.kotlin.bignum.ByteArrayRepresentation
import com.ionspin.kotlin.bignum.Endianness

/**
 * Interface defining big integer operations
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

interface BigInteger32ArithmeticInterface {
    val _emitIntArray: IntArray
    val ZERO: UIntArray
    val ONE: UIntArray
    val TWO: UIntArray
    val TEN: UIntArray

    val basePowerOfTwo: Int

    /**
     * Returns the number of leading zeros in a word
     */
    fun numberOfLeadingZerosInAWord(value: UInt): Int

    /**
     * Number of bits needed to represent this number
     */
    fun bitLength(value: UIntArray): Int

    /**
     * Number of consecutive zeros count from the right in binary representation
     */
    fun trailingZeroBits(value: UIntArray): Int

    /**
     * Arithmetic shift left. Shifts the number to the left, by required places of bits, creating new words if necessary
     */
    fun shiftLeft(operand: UIntArray, places: Int): UIntArray

    /**
     * Arithmetic shift right. Shifts the number to the right, by required places of bits, removing words that no longer relevant
     */
    fun shiftRight(operand: UIntArray, places: Int): UIntArray

    /**
     * Compares two numbers
     *
     * @return -1 if first is bigger, 0 if equal, +1 if second is bigger
     */
    fun compare(first: UIntArray, second: UIntArray): Int

    /**
     * Adds two big integers
     * @return result of add
     */
    fun add(first: UIntArray, second: UIntArray): UIntArray

    /**
     * Subtracts two big integers
     * @return result of subtract
     */
    fun subtract(first: UIntArray, second: UIntArray): UIntArray

    /**
     * Multiplies two big integers
     * @return result of multiply
     */
    fun multiply(first: UIntArray, second: UIntArray): UIntArray

    /**
     * Divide two big integers
     * @return A pair representing quotient (first member of the pair) and remainder (second member of the pair)
     */
    fun divide(
        first: UIntArray,
        second: UIntArray
    ): Pair<UIntArray, UIntArray>

    /**
     * Returns a integer reciprocal of this number such that 0 <= base ^ word - operand * reciprocal <= operand,
     * and remainder such that 0 < reciprocal < operand
     */
    fun reciprocal(operand: UIntArray): Pair<UIntArray, UIntArray>

    /**
     * Exponentiation function
     * @return BigInteger result of exponentiation of number by exponent
     */
    fun pow(base: UIntArray, exponent: Long): UIntArray

    fun sqrt(operand: UIntArray): Pair<UIntArray, UIntArray>

    fun gcd(first: UIntArray, second: UIntArray): UIntArray

    /**
     * Parse a string in a specific base into a big integer
     */
    fun parseForBase(number: String, base: Int): UIntArray

    /**
     * return a string representation of big integer in a specific number base
     */
    fun toString(operand: UIntArray, base: Int): String

    fun numberOfDecimalDigits(operand: UIntArray): Long

    fun fromULong(uLong: ULong): UIntArray
    fun fromUInt(uInt: UInt): UIntArray
    fun fromUShort(uShort: UShort): UIntArray
    fun fromUByte(uByte: UByte): UIntArray
    fun fromLong(long: Long): UIntArray
    fun fromInt(int: Int): UIntArray
    fun fromShort(short: Short): UIntArray
    fun fromByte(byte: Byte): UIntArray

    fun or(operand: UIntArray, mask: UIntArray): UIntArray
    fun xor(operand: UIntArray, mask: UIntArray): UIntArray
    fun and(operand: UIntArray, mask: UIntArray): UIntArray
    fun not(operand: UIntArray): UIntArray

    fun bitAt(operand: UIntArray, position: Long): Boolean
    fun setBitAt(operand: UIntArray, position: Long, bit: Boolean): UIntArray

    fun oldToByteArray(operand: UIntArray, sign: Sign): Array<Byte>
    fun oldFromByteArray(byteArray: Array<Byte>): Pair<UIntArray, Sign>
    fun oldFromByteArray(byteArray: ByteArray): Pair<UIntArray, Sign>
    fun olfFromUByteArray(uByteArray: Array<UByte>, endianness: Endianness = Endianness.BIG): Pair<UIntArray, Sign>
    fun olfFromUByteArray(uByteArray: UByteArray, endianness: Endianness = Endianness.BIG): Pair<UIntArray, Sign>

    fun fromUByteArray(
        source: UByteArray,
        byteArrayRepresentation: ByteArrayRepresentation,
        endianness: Endianness,
        isTwosComplement: Boolean
    ): Pair<UIntArray, Sign>

    fun fromByteArray(
        source: ByteArray,
        byteArrayRepresentation: ByteArrayRepresentation,
        endianness: Endianness,
        isTwosComplement: Boolean
    ): Pair<UIntArray, Sign>

    fun toUByteArray(
        operand: UIntArray,
        byteArrayRepresentation: ByteArrayRepresentation,
        endianness: Endianness,
        isTwosComplement: Boolean
    ): UByteArray

    fun toByteArray(
        operand: UIntArray,
        byteArrayRepresentation: ByteArrayRepresentation,
        endianness: Endianness,
        isTwosComplement: Boolean
    ): ByteArray

    /**
     * Converts an UIntArray into a byte array representation, with consideration to requested endianness
     *
     * E.g.
     * Input UIntArray:
     * 0xAABBCCDDU, 0x11223344
     * Output UByteArray
     * Little endian:
     * 0xDD, 0xCC, 0xBB, 0xAA, 0x44, 0x33, 0x22, 0x11
     * Big endian:
     * 0xAA, 0xBB, 0xCC, 0xDD, 0x11, 0x22, 0x33, 0x44
     */
    fun toUIntArrayRepresentedAsTypedUByteArray(
        operand: UIntArray,
        endianness: Endianness = Endianness.BIG
    ): Array<UByte>

    /**
     * Converts an UIntArray into a byte array representation, with consideration to requested endianness
     *
     * E.g.
     * Input UIntArray:
     * 0xAABBCCDDU, 0x11223344
     * Output UByteArray
     * Little endian:
     * 0xDD, 0xCC, 0xBB, 0xAA, 0x44, 0x33, 0x22, 0x11
     * Big endian:
     * 0xAA, 0xBB, 0xCC, 0xDD, 0x11, 0x22, 0x33, 0x44
     */
    fun toUIntArrayRepresentedAsUByteArray(operand: UIntArray, endianness: Endianness = Endianness.BIG): UByteArray
}
