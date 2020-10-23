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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 9-Aug-2020
 */

class BigDecimalNarrowingTest {
    private val testDouble = 0.000000000000999111999111

    @Test
    fun doubleValueTest() {
        var dub = BigDecimal.fromDouble(Double.MAX_VALUE)
        assertEquals(Double.MAX_VALUE, dub.doubleValue(true))
        dub = BigDecimal.fromDouble(-Double.MAX_VALUE)
        assertEquals(-Double.MAX_VALUE, dub.doubleValue(true))
        dub = BigDecimal.fromDouble(Double.MIN_VALUE)
        assertEquals(Double.MIN_VALUE, dub.doubleValue(true))
        assertFailsWith<ArithmeticException> {
            dub.divide(BigDecimal.TEN).doubleValue(true)
        }

        /*
         * Test a range of exponents on the testDouble value.  
         */
        var dubIn = testDouble
        for (factor in 0..22) {
            dubIn *= 10.0
            val bd = BigDecimal.fromDouble(dubIn)
            val dubOut = bd.doubleValue(true)
            assertEquals(dubIn, dubOut)
        }
    }

    @Test
    fun floatValueTest() {
        var f = BigDecimal.fromFloat(Float.MAX_VALUE)
        assertEquals(Float.MAX_VALUE, f.floatValue(true))
        f = BigDecimal.fromFloat(-Float.MAX_VALUE)
        assertEquals(-Float.MAX_VALUE, f.floatValue(true))
        f = BigDecimal.fromFloat(Float.MIN_VALUE)
        assertEquals(Float.MIN_VALUE, f.floatValue(true))
        assertFailsWith<ArithmeticException> {
            f.divide(BigDecimal.TEN).floatValue(true)
        }
    }
}
