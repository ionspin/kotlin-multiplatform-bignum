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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-May-2019
 */

class BigDecimalDivPowTest {

    @Test
    fun testIntegerDivision() {
        assertTrue {
            val dividend = 4.123.toBigDecimal()
            val divisor = 2.toBigDecimal()
            val (quotient, remainder) = dividend divrem divisor

            quotient == 2.toBigDecimal() && remainder == 0.123.toBigDecimal()
        }
    }

    @Test
    fun testSpecificIntegerDivision() {
        assertTrue {
            val dividend = Short.MIN_VALUE.toBigDecimal()
            val divisor = 1.toBigDecimal()
            val (quotient, remainder) = dividend divrem divisor

            quotient == Short.MIN_VALUE.toBigDecimal() && remainder == 0.toBigDecimal()
        }
    }

    @Test
    fun testDecimalExponentiation() {
        assertTrue {
            val a = 2.toBigDecimal()
            val result = a.pow(2)
            result.compareTo(4) == 0
        }

        assertTrue {
            val a = 2.toBigDecimal()
            val result = a.pow(10)
            result.compareTo(1024) == 0
        }

        assertTrue {
            val a = 0.5.toBigDecimal()
            val result = a.pow(2)
            result.compareTo(0.25) == 0
        }

        assertTrue {
            val a = 4.toBigDecimal(decimalMode = DecimalMode(decimalPrecision = 10, roundingMode = RoundingMode.FLOOR))
            val result = a.pow(-1)
            result.compareTo(0.25) == 0
        }

        assertTrue {
            val a = 4.toBigDecimal(decimalMode = DecimalMode(decimalPrecision = 10, roundingMode = RoundingMode.FLOOR))
            val result = a.pow(-2)
            result.compareTo(1f / 16) == 0
        }

        assertFailsWith(ArithmeticException::class) {
            val a = 0.toBigDecimal()
            val result = a.pow(-1)
        }
    }
}
