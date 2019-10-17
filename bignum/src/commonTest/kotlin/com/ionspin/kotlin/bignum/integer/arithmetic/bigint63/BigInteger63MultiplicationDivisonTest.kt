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
 * on 17-Oct-2019
 */

@ExperimentalUnsignedTypes
class BigInteger63MultiplicationDivisonTest {
    @Test
    fun testToomCook3() {
        val a = ulongArrayOf(1U, 1U, 1U)
        val b = ulongArrayOf(1U, 2U, 1U)
        val result = BigInteger63Arithmetic.toomCook3Multiply(a, b)
        val basecaseMultiply = BigInteger63Arithmetic.basecaseMultiply(a, b)
        assertTrue {
            result.contentEquals(basecaseMultiply)
        }

    }
}