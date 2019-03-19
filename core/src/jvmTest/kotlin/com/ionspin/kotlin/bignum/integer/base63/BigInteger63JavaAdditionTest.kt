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

package com.ionspin.kotlin.bignum.integer.base63

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Mar-3/17/19
 */
/*
0 = 9223372036854775800
1 = 9223372036854775807
2 = 9223372036854775807
3 = 67108863

+ 8
 */

@ExperimentalUnsignedTypes
class BigInteger63JavaAdditionTest {

    @Test
    fun `Test specific values for addition`() {
        val first = ulongArrayOf(9223372036854775800UL, 9223372036854775807UL, 9223372036854775807UL, 67108863UL)
        val second = ulongArrayOf(8UL)
        val result = BigInteger63Arithmetic.add(first, second)

        val bigIntResult = first.toJavaBigInteger() + second.toJavaBigInteger()
        assertTrue { result.toJavaBigInteger() == bigIntResult }

    }





    
}