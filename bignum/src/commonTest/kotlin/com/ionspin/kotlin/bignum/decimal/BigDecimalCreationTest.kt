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
 * on 30-Mar-2019
 */

class BigDecimalCreationTest {

    @Test
    fun testExpandedParsing() {
        assertTrue {
            val a = BigDecimal.parseStringWithMode("1.23")
            val b = BigDecimal.fromIntWithExponent(123, 0)
            val c = 123.toBigDecimalUsingSignificandAndExponent(0)
            a == b && a == c
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-1.23")
            val b = BigDecimal.fromIntWithExponent(-123, 0)
            val c = (-123).toBigDecimalUsingSignificandAndExponent(0)
            a == b && a == c
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("0.0123")
            val b = BigDecimal.fromIntWithExponent(123, -2)
            val c = 123.toBigDecimalUsingSignificandAndExponent(-2)
            a == b && a == c
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-0.0123")
            val b = BigDecimal.fromIntWithExponent(-123, -2)
            val c = (-123).toBigDecimalUsingSignificandAndExponent(-2)
            a == b && a == c
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("123.123")
            val b = BigDecimal.fromIntWithExponent(123123, 2)
            val c = 123123.toBigDecimalUsingSignificandAndExponent(2)
            a == b && a == c
        }

        assertTrue {
            val a = BigDecimal.parseStringWithMode("-123.123")
            val b = BigDecimal.fromIntWithExponent(-123123, 2)
            val c = (-123123).toBigDecimalUsingSignificandAndExponent(2)
            a == b && a == c
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

        assertTrue {
            val a = BigDecimal.parseStringWithMode("5E-324")
            val b = BigDecimal.fromIntWithExponent(5, -324)
            a == b
        }
    }

    @Test
    fun testFromPrimitive() {
        val expected = BigDecimal.fromBigInteger(123.toBigInteger())
        assertTrue {
            val created = 123L.toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = 123.toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = 123.toShort().toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = 123.toByte().toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = "123".toByte().toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = 123.0.toBigDecimal()
            expected == created
        }
        assertTrue {
            val created = 123f.toBigDecimal()
            expected == created
        }
        // Using as significand
        assertTrue {
            val created = 123L.toBigDecimalUsingSignificandAndExponent(2)
            expected == created
        }
        assertTrue {
            val created = 123.toBigDecimalUsingSignificandAndExponent(2)
            expected == created
        }
        assertTrue {
            val created = 123.toShort().toBigDecimalUsingSignificandAndExponent(2)
            expected == created
        }
        assertTrue {
            val created = 123.toByte().toBigDecimalUsingSignificandAndExponent(2)
            expected == created
        }

        val expectedModified = BigDecimal.fromBigInteger(123000.toBigInteger())
        assertTrue {
            val created = 123L.toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = 123.toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = 123.toShort().toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = 123.toByte().toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = "123".toByte().toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = 123.0.toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }
        assertTrue {
            val created = 123f.toBigDecimal(exponentModifier = 3)
            expectedModified == created
        }

        val expectedModified2 = BigDecimal.parseString("000.123")
        assertTrue {
            val created = 123L.toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = 123.toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = 123.toShort().toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = 123.toByte().toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = "123".toByte().toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = 123.0.toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
        }
        assertTrue {
            val created = 123f.toBigDecimal(exponentModifier = -3)
            expectedModified2 == created
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
