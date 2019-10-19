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

import com.ionspin.kotlin.bignum.integer.base63.BigInteger63Arithmetic
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Oct-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63ArithmeticDivisionTest {

    @Test
    fun testExactDivisionBy3() {
        assertTrue {
            val dividend = ulongArrayOf(999UL)
            val expected = ulongArrayOf(333UL)
            val result = BigInteger63Arithmetic.exactDivideBy3(dividend)
            result.contentEquals(expected)
        }

        assertTrue {
            val dividend = ulongArrayOf(999_999UL)
            val expected = ulongArrayOf(333_333UL)
            val result = BigInteger63Arithmetic.exactDivideBy3(dividend)
            result.contentEquals(expected)
        }

        assertTrue {
            val dividend = ulongArrayOf(3UL, 0UL, 0UL)
            val expected = BigInteger63Arithmetic.parseForBase("85070591730234615865843651857942052864", 10)
            val result = BigInteger63Arithmetic.exactDivideBy3(dividend)
            result.contentEquals(expected)
        }
    }

    @Test
    fun `Test random words`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 5001) {
            val dividend = ulongArrayOf(random.nextULong() shr 1)
            if (BigInteger63Arithmetic.divide(dividend, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                exactDivide(dividend)
            } else {
                val dividend2 = BigInteger63Arithmetic.add(dividend, ulongArrayOf(1U))
                if (BigInteger63Arithmetic.divide(dividend2, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                    exactDivide(dividend2)
                } else {
                    val dividend3 = BigInteger63Arithmetic.add(dividend2, ulongArrayOf(1U))
                    if (BigInteger63Arithmetic.divide(dividend3, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                        exactDivide(dividend3)
                    } else {
                        println("Impossible.")
                    }
                }
            }
        }
    }

    @Test
    fun `Test long words`() {
        val seed = 1
        val random = Random(seed)

        for (i in 1 .. 100) {
            val dividend = ULongArray(1500) {
                random.nextULong() shr 1
            }
            if (BigInteger63Arithmetic.divide(dividend, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                exactDivide(dividend)
            } else {
                val dividend2 = BigInteger63Arithmetic.add(dividend, ulongArrayOf(1U))
                if (BigInteger63Arithmetic.divide(dividend2, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                    exactDivide(dividend2)
                } else {
                    val dividend3 = BigInteger63Arithmetic.add(dividend2, ulongArrayOf(1U))
                    if (BigInteger63Arithmetic.divide(dividend3, ulongArrayOf(3U)).second.contentEquals(BigInteger63Arithmetic.ZERO)) {
                        exactDivide(dividend3)
                    } else {
                        println("Impossible.")
                    }
                }
            }
        }
    }

    @Test
    fun specificDivide() {
        val dividend = ulongArrayOf(0U, 0U, 0U, 3U)
        exactDivide(dividend)
    }

    fun exactDivide(dividend : ULongArray) {
        val basecaseDivideResult = BigInteger63Arithmetic.divide(dividend, ulongArrayOf(3U))
        val result = BigInteger63Arithmetic.exactDivideBy3(dividend)
        assertTrue(
            "Failed on ulongArrayOf(${dividend.joinToString(separator = ", ") { it.toString() + "UL" }})"
        ){ basecaseDivideResult.first.contentEquals(result) }
    }
}