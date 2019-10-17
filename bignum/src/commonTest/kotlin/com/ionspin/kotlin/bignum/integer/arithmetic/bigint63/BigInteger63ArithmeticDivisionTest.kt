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
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Oct-2019
 */
class BigInteger63ArithmeticDivisionTest {
    @ExperimentalUnsignedTypes
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
}