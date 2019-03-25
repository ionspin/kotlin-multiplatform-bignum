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

import org.junit.Test
import java.math.MathContext
import java.math.RoundingMode
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalJvmTest {


    @Test
    fun testCreation() {
        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(123L, 5)
            val javaBigDecimal = java.math.BigDecimal.valueOf(123, -3)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigDecimal = java.math.BigDecimal.valueOf(123, -2)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigDecimal = java.math.BigDecimal.valueOf(71, 3)

            val bigDecimalConverted = bigDecimal.toJavaBigDecimal()

            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0 // <- Ignores java bigint scale difference
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(125L, -7)
            val javaBigDecimal = java.math.BigDecimal.valueOf(125, 9)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, 15)
            val javaBigDecimal = java.math.BigDecimal.valueOf(71, -14)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }
    }

    @Test
    fun testAddition() {

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, 2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, -1)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(7111, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(7111, 5)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125L, -7)
            val javaBigFirst = java.math.BigDecimal.valueOf(125, 9)

            val second = BigDecimal.fromLongWithExponent(71, 15)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, -14)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125L, 15) // 15
            val javaBigFirst = java.math.BigDecimal.valueOf(125, -13 ) // -14

            val second = BigDecimal.fromLongWithExponent(71, -7) // -7
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 8) // 9

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }
    }

    @Test
    fun testSubtraction() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first - second
            val javaBigResult = javaBigFirst - javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testMultiplication() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first * second
            val javaBigResult = javaBigFirst * javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testDivision() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first / second
            val javaBigResult = javaBigFirst / javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }
}