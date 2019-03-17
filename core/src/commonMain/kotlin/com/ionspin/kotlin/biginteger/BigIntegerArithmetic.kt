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

package com.ionspin.kotlin.biginteger

/**
 * Interface defining big integer operations
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
interface BigIntegerArithmetic<BackingCollectionType, BackingWordType> {
    val ZERO : BackingCollectionType
    val ONE : BackingCollectionType
    val basePowerOfTwo: Int
    /**
     * Returns the number of leading zeroes in highest word
     */
    fun numberOfLeadingZeroes(value: BackingWordType): Int

    /**
     * Number of bits needed to represent this number
     */
    fun bitLength(value: BackingCollectionType): Int

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
     * @return result of addition
     */
    fun add(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Substracts two big integers
     * @return result of subtraction
     */
    fun substract(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Multiplies two big integers
     * @return result of multiplication
     */
    fun multiply(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType

    /**
     * Divide two big integers
     * @return A pair representing quotient (first member of the pair) and remainder (second member of the pair)
     */
    fun divide(first: BackingCollectionType, second: BackingCollectionType): Pair<BackingCollectionType, BackingCollectionType>

    /**
     * Parse a string in a specific base into a big integer
     */
    fun parseForBase(number : String, base : Int) : BackingCollectionType

    /**
     * return a string representation of big integer in a specific number base
     */
    fun toString(operand: BackingCollectionType, base : Int) : String


    fun fromLong(long : Long) : BackingCollectionType
    fun fromInt(int : Int) : BackingCollectionType
    fun fromShort(short : Short): BackingCollectionType
    fun fromByte(byte : Byte) : BackingCollectionType


}

