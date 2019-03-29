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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */
@ExperimentalUnsignedTypes
class BitwiseTest {


    @Test
    fun testNegate() {
        val bigInt = BigInteger.fromInt(123)
        val negatedBigInt = -bigInt
        val expectedBigInt = BigInteger.fromInt(-123)

        assertTrue { negatedBigInt == expectedBigInt }

    }

    @Test
    fun numberOfDigitsTest() {
        val bigInteger = BigInteger.parseString("123456789012345678901234567890123456", 10)
        val numberOfDigits = bigInteger.numberOfDigits()
        val expectedDigits = 36L
        assertTrue { numberOfDigits == expectedDigits }
    }

    @Test
    fun numberOfDigitsBigTest() {
        for (i in 1 .. 504L step 101) {
            val string = buildString (i.toInt()) { for (j in 1 .. i) this.append('1')  }
            val bigInteger = BigInteger.parseString(string, 10)
            val numberOfDigits = bigInteger.numberOfDigits()
            assertTrue { numberOfDigits == i }
        }

    }



}