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

package com.ionspin.kotlin.bignum.integer.arithmetic

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.WordArray
import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-3/23/19
 */
@ExperimentalUnsignedTypes
class ConversionTest {

    @Test
    fun testFromInt() {
        val one = BigInteger.fromInt(1)
        val negativeOne = BigInteger.fromInt(-1)
        val maxInt = BigInteger.fromInt(Int.MAX_VALUE)
        val minInt = BigInteger.fromInt(Int.MIN_VALUE)

        //This if is meaningless, it wouldn't compile if we switched backing word to uintArray
        if (WordArray::class == ULongArray::class) {
            val maxIntBigInt = BigInteger.fromWordArray(ulongArrayOf(Int.MAX_VALUE.absoluteValue.toULong()), Sign.POSITIVE)
            val minIntBigInt = BigInteger.fromWordArray(ulongArrayOf(Int.MIN_VALUE.absoluteValue.toULong()), Sign.NEGATIVE)
            assertTrue { maxInt == maxIntBigInt }
            assertTrue { minInt == minIntBigInt }
        }

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }
    }

    @Test
    fun testFromLong() {
        val one = BigInteger.fromLong(1)
        val negativeOne = BigInteger.fromLong(-1)
        val maxLong = BigInteger.fromLong(Long.MAX_VALUE)
        val minLong = BigInteger.fromLong(Long.MIN_VALUE)

        //This if is meaningless, it wouldn't compile if we switched backing word to uintArray
        if (WordArray::class == ULongArray::class) {
            val maxLongBigLong = BigInteger.fromWordArray(ulongArrayOf(Long.MAX_VALUE.absoluteValue.toULong()), Sign.POSITIVE)
            val minLongBigLong = BigInteger.fromWordArray(ulongArrayOf(0U, 1U), Sign.NEGATIVE)
            assertTrue { maxLong == maxLongBigLong }
            assertTrue { minLong == minLongBigLong }
        }

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }
    }

    @Test
    fun testFromShort() {
        val one = BigInteger.fromShort(1)
        val negativeOne = BigInteger.fromShort(-1)
        val maxShort = BigInteger.fromShort(Short.MAX_VALUE)
        val minShort = BigInteger.fromShort(Short.MIN_VALUE)

        //This if is meaningless, it wouldn't compile if we switched backing word to uintArray
        if (WordArray::class == UShortArray::class) {
            val maxShortBigShort = BigInteger.fromWordArray(ulongArrayOf(Short.MAX_VALUE.toInt().absoluteValue.toULong()), Sign.POSITIVE)
            val minShortBigShort = BigInteger.fromWordArray(ulongArrayOf(Short.MIN_VALUE.toInt().absoluteValue.toULong()), Sign.NEGATIVE)
            assertTrue { maxShort == maxShortBigShort }
            assertTrue { minShort == minShortBigShort }
        }

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }
    }

    @Test
    fun testFromByte() {
        val one = BigInteger.fromByte(1)
        val negativeOne = BigInteger.fromByte(-1)
        val maxByte = BigInteger.fromByte(Byte.MAX_VALUE)
        val minByte = BigInteger.fromByte(Byte.MIN_VALUE)

        //This if is meaningless, it wouldn't compile if we switched backing word to uintArray
        if (WordArray::class == UByteArray::class) {
            val maxByteBigByte = BigInteger.fromWordArray(ulongArrayOf(Byte.MAX_VALUE.toInt().absoluteValue.toULong()), Sign.POSITIVE)
            val minByteBigByte = BigInteger.fromWordArray(ulongArrayOf(Byte.MIN_VALUE.toInt().absoluteValue.toULong()), Sign.NEGATIVE)
            assertTrue { maxByte == maxByteBigByte }
            assertTrue { minByte == minByteBigByte }
        }

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }
    }
}