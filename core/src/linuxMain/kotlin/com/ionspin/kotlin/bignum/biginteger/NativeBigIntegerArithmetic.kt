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

package com.ionspin.kotlin.bignum.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
actual object NativeBigIntegerArithmetic : BigIntegerArithmetic<IntArray, Int> {
    override val ZERO: IntArray
        get() = TODO("not implemented yet")
    override val ONE: IntArray
        get() = TODO("not implemented yet")
    val base: Int
        get() = TODO("not implemented yet")
    override val basePowerOfTwo: Int
        get() = TODO("not implemented yet")

    val baseMask = 0x7FFFFFFFL

    override fun numberOfLeadingZeroes(value: Int): Int {
        TODO("not implemented yet")
    }

    override fun bitLength(value: IntArray): Int {
        TODO("not implemented yet")
    }

    override fun shiftLeft(operand: IntArray, places: Int): IntArray {
        TODO("not implemented yet")
    }

    override fun shiftRight(operand: IntArray, places: Int): IntArray {
        TODO("not implemented yet")
    }

    override fun compare(first: IntArray, second: IntArray): Int {
        TODO("not implemented yet")
    }

    override fun add(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun substract(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun multiply(first: IntArray, second: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun divide(first: IntArray, second: IntArray): Pair<IntArray, IntArray> {
        TODO("not implemented yet")
    }
    override fun parseForBase(number: String, base: Int) : IntArray {
        TODO("not implemented yet")
    }

    override fun toString(operand: IntArray, base: Int): String {
        TODO("not implemented yet")
    }

    override fun fromLong(long: Long): IntArray = intArrayOf((long and baseMask).toInt(), (long shr basePowerOfTwo).toInt() )

    override fun fromInt(int: Int): IntArray = intArrayOf(int)

    override fun fromShort(short: Short): IntArray = intArrayOf(short.toInt())

    override fun fromByte(byte: Byte): IntArray = intArrayOf(byte.toInt())

    override fun or(operand: IntArray, mask: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun xor(operand: IntArray, mask: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun and(operand: IntArray, mask: IntArray): IntArray {
        TODO("not implemented yet")
    }

    override fun inv(operand: IntArray): IntArray {
        TODO("not implemented yet")
    }

}