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
 * on 30-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalCreationTest {

    @Test
    fun testExpandedParsing() {
        assertTrue {
            val a = BigDecimal.parseStringWithMode("1.23")
            val b = BigDecimal.fromIntWithExponent(123, 0)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-1.23")
            val b = BigDecimal.fromIntWithExponent(-123, 0)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("0.0123")
            val b = BigDecimal.fromIntWithExponent(123, -2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-0.0123")
            val b = BigDecimal.fromIntWithExponent(-123, -2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("123.123")
            val b = BigDecimal.fromIntWithExponent(123123, 2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-123.123")
            val b = BigDecimal.fromIntWithExponent(-123123, 2)
            a == b
        }
    }

    @Test
    fun testSciNotationParsing() {
        assertTrue {
            val a = BigDecimal.parseStringWithMode("1.23E+0")
            val b = BigDecimal.fromIntWithExponent(123, 0)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-1.23e+0")
            val b = BigDecimal.fromIntWithExponent(-123, 0)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("1.23E-2")
            val b = BigDecimal.fromIntWithExponent(123, -2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-1.23E-2")
            val b = BigDecimal.fromIntWithExponent(-123, -2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("1.23123e+2")
            val b = BigDecimal.fromIntWithExponent(123123, 2)
            a == b
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-1.23123E+2")
            val b = BigDecimal.fromIntWithExponent(-123123, 2)
            a == b
        }
    }

    @Test
    fun fromDoubleTest() {
        assertTrue {
            val a = BigDecimal.fromDouble(0.000000123)
            val b = BigDecimal.fromIntWithExponent(123, -7)
            a == b
        }
    }

    @Test
    fun fromFloatTest() {
        assertTrue {
            val a = BigDecimal.fromFloat(0.000000000000123f)
            val b = BigDecimal.fromIntWithExponent(123, -13)
            a == b
        }
    }

    @Test
    fun testMovePoint() {

        assertTrue {
            val original = 123.45.toBigDecimal()
            val moved = original.moveDecimalPoint(0)
            val expected = 123.45.toBigDecimal()
            moved == expected
        }

        assertTrue {
            val original = 123.45.toBigDecimal()
            val moved = original.moveDecimalPoint(1)

            moved == 1234.5.toBigDecimal()
        }

        assertTrue {
            val original = 123.45.toBigDecimal()
            val moved = original.moveDecimalPoint(3)
            val expected = 123450.toBigDecimal()
            moved == expected
        }

        assertTrue {
            val original = 123.45.toBigDecimal()
            val moved = original.moveDecimalPoint(-3)
            val expected = 0.12345.toBigDecimal()
            moved == expected
        }

        assertTrue {
            val original = 123.45.toBigDecimal()
            val moved = original.moveDecimalPoint(-5)
            val expected = 0.0012345.toBigDecimal()
            moved == expected
        }
    }
}