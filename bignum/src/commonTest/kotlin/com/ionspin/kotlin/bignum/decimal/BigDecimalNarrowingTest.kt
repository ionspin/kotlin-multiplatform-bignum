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
        // Same situation as with min value, max value cannot be expressed either, it's actually
        // 179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368
        // So we don't ask for exact value in this case
        var dub = BigDecimal.fromDouble(Double.MAX_VALUE)
        assertEquals(Double.MAX_VALUE, dub.doubleValue(false))
        dub = BigDecimal.fromDouble(-Double.MAX_VALUE)
        assertEquals(-Double.MAX_VALUE, dub.doubleValue(false))
        // Double.MIN_VALUE cant be represented exactly, it's actually 4.940656458412465441765687928682213...E-324
        // Since out from double is using parse string to get a value, documented value is
        // correctly created, but that is only because Double.toString() already rounds up the real value of Double.MIN_VALUE
        // to 4.9E-324. I'm leaving the commented out part of test for documentation purposes
        dub = BigDecimal.fromDouble(Double.MIN_VALUE)
        println(dub.toStringExpanded())

        assertEquals(Double.MIN_VALUE, dub.doubleValue(false))
        assertFailsWith<ArithmeticException> {
            dub.divide(BigDecimal.TEN).doubleValue(true)
        }
    }

    @Test
    fun floatValueTest() {
        // Same situation as with min value, max value cannot be expressed either, it's actually
        // 340282346638528859811704183484516925440
        // So we don't ask for exact value in this case
        var f = BigDecimal.fromFloat(Float.MAX_VALUE)
        assertEquals(Float.MAX_VALUE, f.floatValue(false))
        f = BigDecimal.fromFloat(-Float.MAX_VALUE)
        assertEquals(-Float.MAX_VALUE, f.floatValue(false))

        // Same as for double case, actual min value for float is 1.40129846432481707092372958328991613128026194187651577175706828388979108268586060148663818836212158203125E-45
        // But since we are creating a BigDecimal which is exactly 1.40129846432481707e-45f, requesting exact narrowing will
        // never be possible in IEEE754
        // So we don't ask for exact value in this case
        f = BigDecimal.fromFloat(Float.MIN_VALUE)
        assertEquals(Float.MIN_VALUE, f.floatValue(false))
        assertFailsWith<ArithmeticException> {
            f.divide(BigDecimal.TEN).floatValue(true)
        }
    }

    @Test
    fun specificFloatNarrowingTest() {
        assertTrue {
            val bigDecimal = "12.375".toBigDecimal()
            val floatExpected = 12.375f // we know this can be represented
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }

        assertTrue {
            val bigDecimal = "375E35".toBigDecimal()
            val floatExpected = 375E35f // we know this can be represented
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }

        assertTrue {
            val bigDecimal = "0.375E-22".toBigDecimal()
            val floatExpected = 0.375E-22f // we know this can be represented
            println("Expected float value: $floatExpected")
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }

        assertTrue {
            val bigDecimal = "0.00375E-22".toBigDecimal()
            val floatExpected = 0.00375E-22f // we know this can be represented
            println("Expected float value: $floatExpected")
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }

        assertTrue {
            val bigDecimal = "3.75E-42".toBigDecimal()
            val floatExpected = 3.75E-42f // we know this can be represented
            println("Expected float value: $floatExpected")
            val narrowed = bigDecimal.floatValue(true)
            narrowed == floatExpected
        }

        assertFailsWith<ArithmeticException> {
            val tooLargePositiveExponent = "1E39".toBigDecimal()
            tooLargePositiveExponent.floatValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val tooLargeNegativeExponent = "1E-46".toBigDecimal()
            tooLargeNegativeExponent.floatValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val notRepresentableByIEEE754 = "0.1".toBigDecimal()
            val narrowed = notRepresentableByIEEE754.floatValue(true)
        }

        assertTrue {
            // 27 bits representable in 24 bits 134217720 = 111111111111111111111111000
            val representableFloatBigDecimal = "134217720".toBigDecimal()
            val expectedFloat = 134217720f
            representableFloatBigDecimal.floatValue(true) == expectedFloat
        }

        assertFailsWith<ArithmeticException> {
            // 27 bits unrepresentable in 24 bits 134217721 == 111111111111111111111111001
            val unrepresentableFloatBigDecimal = "134217721".toBigDecimal()
            unrepresentableFloatBigDecimal.floatValue(true)
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
        assertTrue {
            val bigDecimal = "375E305".toBigDecimal()
            val floatExpected = 375E305 // we know this can be represented
            val narrowed = bigDecimal.doubleValue(true)
            narrowed == floatExpected
        }

        assertTrue {
            val bigDecimal = "375E-326".toBigDecimal()
            val floatExpected = 375E-326 // we know this can be represented
            val narrowed = bigDecimal.doubleValue(true)
            narrowed == floatExpected
        }
        assertFailsWith<ArithmeticException> {
            val tooLargePositiveExponent = "1E309".toBigDecimal()
            tooLargePositiveExponent.doubleValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val tooLargeNegativeExponent = "1E-325".toBigDecimal()
            tooLargeNegativeExponent.doubleValue(exactRequired = true)
        }

        assertFailsWith<ArithmeticException> {
            val notRepresentableByIEEE754 = "0.1".toBigDecimal()
            val narrowed = notRepresentableByIEEE754.doubleValue(true)
        }

        assertTrue {
            // 56 bits representatble in 53 bits 11111111111111111111111111111111111111111111111111111000 = 72057594037927928
            val representableDoubleBigDecimal = "72057594037927928".toBigDecimal()
            val expectedDouble = 72057594037927928.0
            representableDoubleBigDecimal.doubleValue(true) == expectedDouble
        }

        assertFailsWith<ArithmeticException> {
            // 56 bits unrepresentatble in 53 bits 11111111111111111111111111111111111111111111111111111001 = 72057594037927929
            val unrepresentableDoubleBigDecimal = "72057594037927929".toBigDecimal()
            unrepresentableDoubleBigDecimal.floatValue(true)
        }
    }
}
