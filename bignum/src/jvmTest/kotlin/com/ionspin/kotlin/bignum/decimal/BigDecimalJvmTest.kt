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
            val javaBigDecimal = java.math.BigDecimal.valueOf(123, -5)
            bigDecimal.toJavaBigDecimal() == javaBigDecimal
        }
    }

    @Test
    fun testAddition() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 2)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -3)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testSubtraction() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 2)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -3)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first - second
            val javaBigResult = javaBigFirst - javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testMultiplication() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 2)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -3)
            println("${second.toString()}")
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first * second
            val javaBigResult = javaBigFirst * javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testDivision() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 2)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -3)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 2)

            val result = first - second
            val javaBigResult = javaBigFirst - javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }
}