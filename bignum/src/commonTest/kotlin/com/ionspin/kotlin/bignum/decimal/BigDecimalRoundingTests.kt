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
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 01-Dec-2019
 */

class BigDecimalRoundingTests {

    @Test
    fun testNumberOfDigits() {
        val a = BigDecimal.fromIntWithExponent(123, 3)
        val b = BigDecimal.fromIntWithExponent(1, -3)
        val c = BigDecimal.fromIntWithExponent(12345, 3)
        val d = BigDecimal.fromIntAsSignificand(10000)
        assertTrue {
            a.numberOfDecimalDigits() == 4L &&
                    b.numberOfDecimalDigits() == 4L &&
                    c.numberOfDecimalDigits() == 5L &&
                    d.numberOfDecimalDigits() == 1L
        }
    }

    @Test
    fun testRoundSignificand() {
        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, 3)
                .roundSignificand(DecimalMode(6, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            rounded.toStringExpanded() == "1234.57"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(1234, 3)
                .roundSignificand(DecimalMode(2, RoundingMode.CEILING))
            rounded.toStringExpanded() == "1300"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(1234, -3)
                .roundSignificand(DecimalMode(2, RoundingMode.CEILING))
            rounded.toStringExpanded() == "0.0013"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(1234, 3)
                .roundSignificand(DecimalMode(3, RoundingMode.FLOOR))
            rounded.toStringExpanded() == "1230"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(1234, 3)
                .roundSignificand(DecimalMode(3, RoundingMode.CEILING))
            rounded.toStringExpanded() == "1240"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(-1234, 3)
                .roundSignificand(DecimalMode(3, RoundingMode.FLOOR))
            rounded.toStringExpanded() == "-1240"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(-1234, 3)
                .roundSignificand(DecimalMode(3, RoundingMode.CEILING))
            rounded.toStringExpanded() == "-1230"
        }
    }

    @Test
    fun testRoundToDigitPosition() {
        assertTrue {
            val rounded = BigDecimal.parseString("1234.5678")
                .roundToDigitPosition(3, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1230"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("0.0012345678")
                .roundToDigitPosition(4, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "0.001"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("0.0012345678")
                .roundToDigitPosition(3, RoundingMode.CEILING)
            rounded.toStringExpanded() == "0.01"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("9.9999")
                .roundToDigitPosition(3, RoundingMode.CEILING)
            rounded.toStringExpanded() == "10"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, -3)
                .roundToDigitPosition(3, RoundingMode.FLOOR)
            rounded.toStringExpanded() == "0"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, -3)
                .roundToDigitPosition(3, RoundingMode.ROUND_HALF_FLOOR)
            rounded.toStringExpanded() == "0"
        }
    }

    @Test
    fun testRoundAfterDecimalPoint() {
        assertTrue {
            val rounded = 1234.5678.toBigDecimal()
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.CEILING)
            rounded.toStringExpanded() == "1234.568"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, 3)
                .roundToDigitPositionAfterDecimalPoint(4, RoundingMode.CEILING)
            rounded.toStringExpanded() == "1234.5679"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, 3)
                .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1234.57"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123455555, 3)
                .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            rounded.toStringExpanded() == "1234.56"
        }

        assertTrue {
            val rounded = BigDecimal.fromIntWithExponent(123456789, -2)
                .roundToDigitPositionAfterDecimalPoint(5, RoundingMode.CEILING)
            rounded.toStringExpanded() == "0.01235"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("123.456")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.CEILING)
            rounded.toStringExpanded() == "123.456"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("0.000123456")
                .roundToDigitPositionAfterDecimalPoint(5, RoundingMode.CEILING)
            rounded.toStringExpanded() == "0.00013"
        }

        assertTrue {
            val rounded = 999.999.toBigDecimal()
                .roundToDigitPositionAfterDecimalPoint(1, RoundingMode.CEILING)

            rounded.toStringExpanded() == "1000"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.12355")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            rounded.toStringExpanded() == "1000000.124"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.12355")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1000000.124"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.1235")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1000000.123"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.12055")
                .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            rounded.toStringExpanded() == "1000000.12"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.12055")
                .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1000000.12"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.12055")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1000000.121"
        }

        assertTrue {
            val rounded = BigDecimal.parseString("1000000.1205")
                .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
            rounded.toStringExpanded() == "1000000.12"
        }

        assertTrue {
            val rounded = 9.5400000000001.toBigDecimal()
                .roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            rounded.toStringExpanded() == "9.54"
        }
    }

    @Test
    fun testDecimalModeWhenRoundingAfterDigitPositionAfterDecimalPoint() {
        val a = 0.333333.toBigDecimal(decimalMode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)).roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val b = 1.toBigDecimal()
        assertEquals(a, 0.33.toBigDecimal())
        assertEquals(a.decimalMode?.decimalPrecision, 4)
        val res = a * b
        assertEquals(res, 0.33.toBigDecimal())
    }

    @Test
    fun testDecimalModeWhenRoundingAfterDigitPosition() {
        val a = 0.333333.toBigDecimal(decimalMode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)).roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val b = 1.toBigDecimal()
        assertEquals(a, 0.33.toBigDecimal())
        assertEquals(a.decimalMode?.decimalPrecision, 4)
        val res = a * b
        assertEquals(res, 0.33.toBigDecimal())
    }

    @Test
    fun `test ROUND_HALF_TO_EVEN`() {
        val map = mapOf(
            -1.005 to -1.00,
            -1.015 to -1.02,
            1.015 to 1.02,
            1.005 to 1.00,
            1.004 to 1.00,
            1.006 to 1.01,
            -1.004 to -1.00,
            -1.006 to -1.01,
            .0 to .0
        )
        map.forEach {
            assertEquals(
                (it.value).toBigDecimal(),
                BigDecimal.fromBigDecimal(
                    it.key.toBigDecimal(),
                    DecimalMode(10, roundingMode = RoundingMode.ROUND_HALF_TO_EVEN, scale = 2)
                ),
                "the ${it.key} should round to ${it.value}"
            )
        }
    }

    @Test
    fun `test ROUND_HALF_TO_ODD`() {
        val map = mapOf(
            -1.005 to -1.01,
            -1.025 to -1.03,
            1.015 to 1.01,
            1.005 to 1.01,
            1.004 to 1.00,
            1.006 to 1.01,
            -1.004 to -1.00,
            -1.006 to -1.01,
            .0 to .0
        )
        map.forEach {
            assertEquals(
                (it.value).toBigDecimal(),
                BigDecimal.fromBigDecimal(
                    it.key.toBigDecimal(),
                    DecimalMode(10, roundingMode = RoundingMode.ROUND_HALF_TO_ODD, scale = 2)
                ),
                "the ${it.key} should round to ${it.value}"
            )
        }
    }
}
