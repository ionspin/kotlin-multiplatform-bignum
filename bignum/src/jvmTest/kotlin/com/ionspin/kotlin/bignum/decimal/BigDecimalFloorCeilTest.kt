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
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-May-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalFloorCeilTest {

    @Test
    fun testConversionToBigInteger() {
        assertTrue {
            val a = 12345.6789.toBigDecimal()
            val bigInt = a.toBigInteger()
            bigInt == 12345.toBigInteger()
        }

        assertTrue {
            val a = BigDecimal.fromLongWithExponent(123, 6)
            val bigInt = a.toBigInteger()
            bigInt == 1230000.toBigInteger()
        }
    }

    @Test
    fun testFloor() {
        assertTrue {
            val a = 12345.6789.toBigDecimal()
            val floor = a.floor()
            floor == 12345.toBigDecimal(exponent = 4.toBigInteger())
        }
    }

    @Test
    fun ceilFloor() {
        assertTrue {
            val a = 12345.6789.toBigDecimal()
            val ceil = a.ceil()
            ceil == 12346.toBigDecimal(exponent = 4.toBigInteger())
        }
    }
}