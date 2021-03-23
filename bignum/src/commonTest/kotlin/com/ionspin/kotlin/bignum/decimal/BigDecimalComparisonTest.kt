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

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Aug-2019
 */

class BigDecimalComparisonTest {
    @Test
    fun testDifferentPrecisionComparison() {
        val a = BigDecimal.fromIntWithExponent(200, 0)
        val b = BigDecimal.fromIntWithExponent(2, 0)
        assertTrue { a == b }
    }

    @Test
    fun testDifferentPrecisionComparison2() {
        val a = BigDecimal.fromIntWithExponent(2000000, 4)
        val b = BigDecimal.fromIntWithExponent(2000, 4)
        assertTrue { a == b }
    }

    @Test
    fun testDifferentPrecisionComparisonSamePrecision() {
        val a = BigDecimal.fromIntWithExponent(2000, 4)
        val b = BigDecimal.fromIntWithExponent(2000, 4)
        assertTrue { a == b }
    }

    @Test
    fun testHashCodeContract() {
        val a = BigDecimal.fromIntWithExponent(2000000, 4)
        val b = BigDecimal.fromIntWithExponent(2000, 4)
        assertTrue { a == b }
        println(a.hashCode())
        println(b.hashCode())
        assertTrue { a.hashCode() == b.hashCode() }
    }

    @Test
    fun testHashCodeContractLong() {
        val a = BigDecimal.fromLongWithExponent(123400000000, -192)
        val b = BigDecimal.fromIntWithExponent(1234, -192)
        assertTrue { a == b }
        assertTrue { a.hashCode() == b.hashCode() }
    }
}
