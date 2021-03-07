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
 * on 15-Feb-2020
 */

class ReportedIssueReplicationTest {

    /**
     * Negative figures round gives incorrect result
     * https://github.com/ionspin/kotlin-multiplatform-bignum/issues/90
     */
    @Test
    fun github90NegativeRounding() {

        assertTrue {
            val a = (3.19).toBigDecimal() * 0.00001.toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (0.00).toBigDecimal()
        }

        assertTrue {
            val a = (0.005).toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (0.01).toBigDecimal()
        }

        assertTrue {
            val a = (-3.19).toBigDecimal() * 0.00001.toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (-0.00).toBigDecimal()
        }

        assertTrue {
            val a = (-0.005).toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (-0.01).toBigDecimal()
        }
    }

    @Test
    fun decimalModeTest() {
        assertTrue {
            val mode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3)
            val a = 500.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            val expected = "1.667E+2".toBigDecimal()
            println(result)
            result == expected
        }

        assertTrue {
            val mode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3)
            val a = 500.toBigDecimal(decimalMode = mode)
            val b = 3.toBigDecimal(decimalMode = mode)
            val result = a.divide(b, mode)
            val expected = "1.667E+2".toBigDecimal()
            println(result)
            result == expected
        }
    }

    /**
     * This reproduction use case was reported via email and until it was fixed expected and unexpected results differed,
     * now that the issue is fixed the wording is a bit misleading, but both unexpected and expected
     * results need to be the same for the test to pass.
     */
    @Test
    fun decimalModeIssue() {
        val DECIMAL_MODE = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 6)
        val bigDecimalWithoutMode = BigDecimal.parseString("1.2345E+2")
        val bigDecimalWithModeAddedLater = BigDecimal.fromBigDecimal(bigDecimalWithoutMode, DECIMAL_MODE)
        val bigDecimalWithModeAtParseTime = BigDecimal.parseStringWithMode("1.2345E+2", DECIMAL_MODE)
        val divisorWithMode = "1.21".toBigDecimal(0, DECIMAL_MODE)
        val divisorWithoutMode = "1.21".toBigDecimal()
        val expectedResult1 = bigDecimalWithoutMode.divide(divisorWithoutMode, DECIMAL_MODE)
        println("Expected result 1 (withoutMode / withoutMode) $expectedResult1")
        val unexpectedResult1 = bigDecimalWithoutMode.divide(divisorWithMode, DECIMAL_MODE)
        println("Unexpected result 1 (withoutMode / withMode) $unexpectedResult1")
        assertTrue { expectedResult1 == unexpectedResult1 }
        val unexpectedResult2 = bigDecimalWithModeAddedLater.divide(divisorWithMode, DECIMAL_MODE)
        println("Unexpected result 2 (withModeAddedLater / withMode) $unexpectedResult2")
        val expectedResult2 = bigDecimalWithModeAtParseTime.divide(divisorWithMode, DECIMAL_MODE)
        println("Expected result 2 (withMode / withMode) $expectedResult2")
        assertTrue { expectedResult2 == unexpectedResult2 }
    }
}
