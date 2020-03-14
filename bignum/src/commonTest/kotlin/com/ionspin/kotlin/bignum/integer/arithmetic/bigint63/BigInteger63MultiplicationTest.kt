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

package com.ionspin.kotlin.bignum.integer.arithmetic.bigint63

import com.ionspin.kotlin.bignum.integer.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Oct-2019
 */

@ExperimentalUnsignedTypes
class BigInteger63MultiplicationTest {
    @Test
    fun testToomCook3() {
        assertTrue {
            val a = ulongArrayOf(0U, 0U, 1U)
            val b = ulongArrayOf(0U, 0U, 1U)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(basecaseMultiply)
        }

        assertTrue {
            val a = ulongArrayOf((0UL - 1UL) shr 1, (0UL - 1UL) shr 1, (0UL - 1UL) shr 1)
            val b = ulongArrayOf((0UL - 1UL) shr 1, (0UL - 1UL) shr 1, (0UL - 1UL) shr 1)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            // println(BigInteger63Arithmetic.toString(result, 10))
            // println(BigInteger63Arithmetic.toString(basecaseMultiply, 10))
            result.contentEquals(basecaseMultiply)
        }

        assertTrue {
            val a = ulongArrayOf(1U, 1U, 1U)
            val b = ulongArrayOf(1U, 0U, 0U)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(BigInteger63Arithmetic.removeLeadingZeros(basecaseMultiply))
        }

        assertTrue {
            val a = ulongArrayOf(1U, 2U, 3U, 4U, 5U, 6U)
            val b = ulongArrayOf(1U, 2U, 3U, 4U, 5U, 6U)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(basecaseMultiply)
        }

        assertTrue {
            val a = ulongArrayOf(0U, 0U, 0U, 1U)
            val b = ulongArrayOf(0U, 0U, 0U, 1U)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(basecaseMultiply)
        }

        assertTrue {
            val a = ulongArrayOf(100U, 200U, 300U, 50U)
            val b = ulongArrayOf(301U, 201U, 101U, 40U)
            val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(basecaseMultiply)
        }
    }

    @Test
    fun testKaratsubaSimple() {
        assertTrue {
            val a = ulongArrayOf(100UL, 200UL, 300UL, 50UL)
            val b = ulongArrayOf(301UL, 201UL, 101UL, 40UL)
            val result = BigInteger63Arithmetic.karatsubaMultiply(a, b)
            val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
            result.contentEquals(basecaseMultiply)
        }
    }
}