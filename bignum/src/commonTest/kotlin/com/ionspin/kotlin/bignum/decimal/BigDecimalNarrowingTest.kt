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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 9-Aug-2020
 */

class BigDecimalNarrowingTest {
    private val testDouble = 0.000000000000999111999111
    private val testFloat = 2.222E-5f

    @Test
    fun doubleValueTest() {
        var dub = BigDecimal.fromDouble(Double.MAX_VALUE)
        assertEquals(Double.MAX_VALUE, dub.doubleValue(true))
        dub = BigDecimal.fromDouble(-Double.MAX_VALUE)
        assertEquals(-Double.MAX_VALUE, dub.doubleValue(true))
        // Double.MIN_VALUE cant be represented exactly, it's actually 4.940656458412465441765687928682213...E-324
        // Since out from double is using parse string to get a value, documented value is
        // correctly created, but that is only because Double.toString() already rounds up the real value of Double.MIN_VALUE
        // to 4.9E-324. I'm leaving the commented out part of test for documentation purposes
//        dub = BigDecimal.fromDouble(Double.MIN_VALUE)
//        println(dub.toStringExpanded())
//
//        assertEquals(Double.MIN_VALUE, dub.doubleValue(true))
//        assertFailsWith<ArithmeticException> {
//            dub.divide(BigDecimal.TEN).doubleValue(true)
//        }
    }

    @Test
    fun floatValueTest() {
        var f = BigDecimal.fromFloat(Float.MAX_VALUE)
        assertEquals(Float.MAX_VALUE, f.floatValue(true))
        f = BigDecimal.fromFloat(-Float.MAX_VALUE)
        assertEquals(-Float.MAX_VALUE, f.floatValue(true))

        // Same as for double case, actual min value for float is 1.40129846432481707092372958328991613128026194187651577175706828388979108268586060148663818836212158203125E-45
        // But since we are creating a BigDecimal which is exactly 1.40129846432481707e-45f, requesting exact narrowing will
        // never be possible in IEEE754
//        f = BigDecimal.fromFloat(Float.MIN_VALUE)
//        assertEquals(Float.MIN_VALUE, f.floatValue(true))
//        assertFailsWith<ArithmeticException> {
//            f.divide(BigDecimal.TEN).floatValue(true)
//        }
    }

    @Test
    fun specificFloatNarrowingTest() {
        assertTrue {
            val bigDecimal = "12.375".toBigDecimal()
            val floatExpected = 12.375f // we know this can be represented
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }
        assertFailsWith<ArithmeticException> {
            val tooLargePositiveExponent = "1E128".toBigDecimal()
            tooLargePositiveExponent.floatValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val tooLargeNegativeExponent = "1E-127".toBigDecimal()
            tooLargeNegativeExponent.floatValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val notRepresentableByIEEE754 = "0.1".toBigDecimal()
            val narrowed = notRepresentableByIEEE754.floatValue(true)
        }
    }

    @Test
    fun specificDoubleNarrowingTest() {
        assertTrue {
            val bigDecimal = "12.375".toBigDecimal()
            val doubleExpected = 12.375 // we know this can be represented
            val narrowed = bigDecimal.doubleValue(true)
            narrowed == doubleExpected
        }
        assertFailsWith<ArithmeticException> {
            val tooLargePositiveExponent = "1E1024".toBigDecimal()
            tooLargePositiveExponent.doubleValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val tooLargeNegativeExponent = "1E-1023".toBigDecimal()
            tooLargeNegativeExponent.doubleValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val notRepresentableByIEEE754 = "0.1".toBigDecimal()
            val narrowed = notRepresentableByIEEE754.doubleValue(true)
        }
    }
}
