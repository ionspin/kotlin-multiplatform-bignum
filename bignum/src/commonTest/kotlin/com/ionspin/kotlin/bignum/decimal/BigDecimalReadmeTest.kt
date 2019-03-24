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

package com.ionspin.kotlin.bignum.decimal

import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalReadmeTest {
    @Test
    fun toStringTest() {
        val bigDecimal = BigDecimal.fromLongWithExponent(1, (-5).toBigInteger())
        println("BigDecimal $bigDecimal")

        assertTrue { bigDecimal.toString() == "1E-5" }
    }

    @Test
    fun toStringWithoutExponentTest() {
        val bigDecimal = BigDecimal.fromLongWithExponent(250000, (-5).toBigInteger())
        println("BigDecimal ${bigDecimal.toStringExpanded()}")

        assertTrue { bigDecimal.toStringExpanded() == "2.5" }
    }

    @Test
    fun readmeMultiplicationTest() {
        val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
        val second = BigDecimal.fromLongWithExponent(71, (15).toBigInteger())

        val product = first * second

        println("Product without exponent: ${product.toStringExpanded()}")
        println("Product: ${product}")
        val expectedResult = BigDecimal.fromLongWithExponent(8875, (-3).toBigInteger())

//        assertTrue { product == expectedResult }


    }
}