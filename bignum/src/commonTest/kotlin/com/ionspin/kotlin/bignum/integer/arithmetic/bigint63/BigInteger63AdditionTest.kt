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
 * on 20-Oct-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63AdditionTest {
    @Test
    fun testAddition() {
        assertTrue {
            val a = ulongArrayOf(10U, 20U)
            val b = ulongArrayOf(15U, 5U)
            val c = BigInteger63Arithmetic.add(a, b)
            c[0] == 25UL && c[1] == 25UL
        }
    }

    @Test
    fun testAdditionWithLeadingZeros() {
        assertTrue {
            val a = ulongArrayOf(10U, 20U, 0U, 0U)
            val b = ulongArrayOf(15U, 5U, 0U, 0U, 0U)
            val c = BigInteger63Arithmetic.add(a, b)
            c[0] == 25UL && c[1] == 25UL
        }
    }
}