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

package com.ionspin.kotlin.bignum.integer.base63List

import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Mar-2019
 */


class BigInteger63ListJavaAdditionTest() {

    @Test
    fun `Test specific values for addition`() {
        val first = listOf(9223372036854775800UL, 9223372036854775807UL, 9223372036854775807UL, 67108863UL)
        val second = listOf(8UL)
        val result = BigInteger63LinkedListArithmetic.add(first, second)

        val bigIntResult = first.toJavaBigInteger() + second.toJavaBigInteger()
        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }
}
