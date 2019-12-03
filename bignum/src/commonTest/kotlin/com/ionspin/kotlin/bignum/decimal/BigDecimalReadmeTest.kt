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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertFailsWith
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

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(1, (10).toBigInteger())
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.0E+10"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(1, (-5).toBigInteger())
            println("BigDecimal: $bigDecimal")
            println("BigDecimalExpanded: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toString() == "1.0E-5"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "7.1E-2"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongAsSignificand(7111)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "7.111"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromIntAsSignificand(10)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.0"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromShort(301)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "3.01E+2"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromByte(11)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.1E+1"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLong(7111)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "7.111E+3"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromInt(10)
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.0E+1"
        }
    }

    @Test
    fun toStringWithoutExponentTest() {

        assertTrue {
            val bigDecimal = BigDecimal.parseStringWithMode("123.456")
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "123.456"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (0).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "5.25"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (1).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "52.5"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (5).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "525000"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(250001, (-5).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.0000250001"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-1).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.71"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("BigDecimal: ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.071"
        }
    }

    @Test
    fun readmeAdditionTest() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (15).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first + second

            println("Sum without exponent: ${product.toStringExpanded()}")
            println("Sum:      $product")
            val expectedResult = BigDecimal.fromBigIntegerWithExponent(
                BigInteger.parseString("7100000000000000000000125", 10), (15).toBigInteger()
            )
            println("Expected: $product")
            product == expectedResult
        }
    }

    @Test
    fun readmeCreationTest() {
        assertTrue {
            val bigDecimal = BigDecimal.parseStringWithMode("0.00000123")
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.23E-6"
        }

        assertTrue {
            val bigDecimal = BigDecimal.parseStringWithMode("1.23E-6")
            println("BigDecimal: $bigDecimal")
            bigDecimal.toString() == "1.23E-6"
        }
    }

    @Test
    fun readmeMultiplicationTest() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (15).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first * second

            println("Product without exponent: ${product.toStringExpanded()}")
            println("Product: $product")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (8).toBigInteger())
            product == expectedResult
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first * second

            println("Product without exponent: ${product.toStringExpanded()}")
            println("Product: $product")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (-9).toBigInteger())
            product == expectedResult
        }
    }

    @Test
    fun readmeDivisionTest() {
        assertFailsWith(ArithmeticException::class) {
            val a = 1.toBigDecimal()
            val b = 3.toBigDecimal()
            a.divide(b)
        }

        assertTrue {
            val a = 1.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            result.toString() == "3.3333333333333333333E-1"
        }
    }

    @Test
    fun readmeBigDecimalExtensionsTest() {
        assertTrue("String to big decimal failed") {
            val bigint = "123456789012345678901234567890.1234567890".toBigDecimal()
            val expected = BigDecimal.parseStringWithMode("123456789012345678901234567890.1234567890")
            bigint == expected
        }

        assertTrue("Float to big decimal failed") {
            val bigint = 1234f.toBigDecimal()
            val expected = BigDecimal.parseStringWithMode("1.234E+3")
            bigint == expected
        }

        assertTrue("Double to big decimal failed") {
            val bigint = 123456789012.0.toBigDecimal()
            val expected = BigDecimal.parseStringWithMode("1.23456789012E+11")
            bigint == expected
        }
    }


}