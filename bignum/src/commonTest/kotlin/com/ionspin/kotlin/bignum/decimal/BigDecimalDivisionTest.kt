package com.ionspin.kotlin.bignum.decimal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-Oct-2020
 */
class BigDecimalDivisionTest {
    @Test
    fun testInfinitePrecisionDivision() {
        assertTrue {
            val a = 2.2.toBigDecimal()
            val b = 2.toBigDecimal()
            val result = a / b
            result == 1.1.toBigDecimal()
        }

        assertTrue {
            val a = 2.2.toBigDecimal()
            val b = 0.5.toBigDecimal()
            val result = a / b
            result == 4.4.toBigDecimal()
        }

        assertTrue {
            val a = 2000.212.toBigDecimal()
            val b = 2.toBigDecimal()
            val result = a / b
            result == 1000.106.toBigDecimal()
        }

        assertTrue {
            val a = 202020.toBigDecimal()
            val b = 10101.toBigDecimal()
            val result = a / b
            result == 20.toBigDecimal()
        }

        assertTrue {
            val a = 202020.toBigDecimal()
            val b = 20.toBigDecimal()
            val result = a / b
            result == 10101.toBigDecimal()
        }

        assertTrue {
            val a = 0.00000202020.toBigDecimal()
            val b = 20.toBigDecimal()
            val result = a / b
            result == 0.00000010101.toBigDecimal()
        }

        assertTrue {
            val a = 0.00000202020.toBigDecimal()
            val b = 0.020.toBigDecimal()
            val result = a / b
            result == 0.00010101.toBigDecimal()
        }

        assertTrue {
            val a = 10.toBigDecimal()
            val b = 20000000000000000.toBigDecimalUsingSignificandAndExponent(exponent = 0) // This is equal to 2.0000000000000000
            val result = a / b
            result == 5.toBigDecimal()
        }

        assertTrue {
            val a = 10000000000000000.toBigDecimalUsingSignificandAndExponent(exponent = 1) // This is equal to 10.000000000000000
            val b = 20000000000000000.toBigDecimalUsingSignificandAndExponent(exponent = 0) // This is equal to 2.0000000000000000
            val result = a / b
            result == 5.toBigDecimal()
        }

        assertTrue {
            val a = 10000000000000000.toBigDecimalUsingSignificandAndExponent(exponent = 1) // This is equal to 10.000000000000000
            val b = 2.toBigDecimal() // This is equal to 2.0000000000000000
            val result = a / b
            result == 5.toBigDecimal()
        }

        assertTrue {
            val first = BigDecimal.fromFloat(123.456f)
            val second = BigDecimal.fromFloat(2.0f)
            var bigDecimal = first.divide(second)
            bigDecimal == 61.728f.toBigDecimal()
        }

        assertTrue {
            val first = BigDecimal.fromFloat(123.456f)
            val second = BigDecimal.fromFloat(0.5f)
            var bigDecimal = first.divide(second)
            bigDecimal == 246.912f.toBigDecimal()
        }
    }

    @Test
    fun testInfinitePrecisionNonTerminating() {
        val a = 1.toBigDecimal()
        val b = 3.toBigDecimal()
        assertFailsWith<ArithmeticException> {
            val result = a / b
        }
    }

    @Test
    fun testDivideAndRemainder() {
        run {
            val a = 0.14.toBigDecimal()
            val b = BigDecimal.ONE
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ZERO, quotient)
            assertEquals(a, remainder)
        }

        run {
            val a = 1.14.toBigDecimal()
            val b = BigDecimal.ONE
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ONE, quotient)
            assertEquals(0.14.toBigDecimal(), remainder)
        }

        run {
            val a = 101.14.toBigDecimal()
            val b = 100.toBigDecimal()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(1.toBigDecimal(), quotient)
            assertEquals(1.14.toBigDecimal(), remainder)
        }

        run {
            val a = 10100.14.toBigDecimal()
            val b = 100.toBigDecimal()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(101.toBigDecimal(), quotient)
            assertEquals(0.14.toBigDecimal(), remainder)
        }

        run {
            val a = 10100.14.toBigDecimal()
            val b = 1000.toBigDecimal()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(10.toBigDecimal(), quotient)
            assertEquals(100.14.toBigDecimal(), remainder)
        }

        run {
            val a = 1.14.toBigDecimal()
            val b = 1000.toBigDecimal()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ZERO, quotient)
            assertEquals(1.14.toBigDecimal(), remainder)
        }

        run {
            val a = 1.toBigDecimal()
            val b = BigDecimal.ONE
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ONE, quotient)
            assertEquals(BigDecimal.ZERO, remainder)
        }

        run {
            val a = 1.toBigDecimal()
            val b = BigDecimal.TWO
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ZERO, quotient)
            assertEquals(BigDecimal.ONE, remainder)
        }

        run {
            val a = 1.toBigDecimal()
            val b = BigDecimal.TWO.negate()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ZERO, quotient)
            assertEquals(BigDecimal.ONE, remainder)
        }

        run {
            val a = -1.toBigDecimal()
            val b = BigDecimal.TWO.negate()
            val (quotient, remainder) = a.divideAndRemainder(b)
            assertEquals(BigDecimal.ZERO, quotient)
            assertEquals(BigDecimal.ONE.negate(), remainder)
        }
    }

    @Test
    fun testDirectedRoundingWithInexactQuotient() {
        run {
            val a = 1.toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(1, RoundingMode.CEILING))
            assertEquals(2.toBigDecimal(), result)
        }

        run {
            val a = 1.toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(2, RoundingMode.AWAY_FROM_ZERO))
            assertEquals("1.1".toBigDecimal(), result)
        }

        run {
            val a = (-1).toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(1, RoundingMode.FLOOR))
            assertEquals((-2).toBigDecimal(), result)
        }

        run {
            val a = (-1).toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(1, RoundingMode.CEILING))
            assertEquals((-1).toBigDecimal(), result)
        }

        run {
            val a = 900000004.toBigDecimal()
            val b = 300000001.toBigDecimal()
            val result = a.divide(b, DecimalMode(2, RoundingMode.CEILING))
            assertEquals("3.1".toBigDecimal(), result)
        }

        run {
            val a = 1.toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            assertEquals(1.toBigDecimal(), result)
        }

        run {
            val a = 1.toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            val result = a.divide(b, DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            assertEquals(1.toBigDecimal(), result)
        }

        run {
            val a = 1.toBigDecimal()
            val b = "0.9999999".toBigDecimal()
            assertFailsWith<ArithmeticException> {
                a.divide(b, DecimalMode(1, RoundingMode.NONE))
            }
        }
    }

    @Test
    fun testDivisionWithScaleAndUnlimitedPrecision() {
        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = 2.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.67".toBigDecimal(), result)
            assertEquals(2L, result.scale)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = 1.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.33".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = 200.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("66.67".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = 10.toBigDecimal()
            val b = 4.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("2.5".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = (-2).toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("-0.67".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.TOWARDS_ZERO, 2)
            val a = 2.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.66".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.CEILING, 2)
            val a = 1.toBigDecimal()
            val b = 3000.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.01".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.FLOOR, 2)
            val a = (-1).toBigDecimal()
            val b = 3000.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("-0.01".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = 1.toBigDecimal()
            val b = 3000.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals(BigDecimal.ZERO, result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_TO_EVEN, 2)
            val a = 1.toBigDecimal()
            val b = 8.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.12".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_TO_EVEN, 2)
            val a = 3.toBigDecimal()
            val b = 8.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.38".toBigDecimal(), result)
        }

        run {
            val mode = DecimalMode(0, RoundingMode.ROUND_HALF_TO_EVEN, 0)
            val a = 5.toBigDecimal()
            val b = 2.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals(2.toBigDecimal(), result)
        }
    }

    @Test
    fun testZeroDividendKeepsDecimalMode() {
        run {
            val mode = DecimalMode(3, RoundingMode.CEILING)
            val a = BigDecimal.ZERO
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals(BigDecimal.ZERO, result)
            assertEquals(mode, result.decimalMode)
        }

        run {
            val mode = DecimalMode(5, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
            val a = BigDecimal.ZERO
            val b = 3.toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals(BigDecimal.ZERO, result)
            assertEquals(2L, result.scale)
            assertEquals(RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, result.roundingMode)
        }

        run {
            val a = BigDecimal.ZERO
            val b = BigDecimal.ZERO
            assertFailsWith<ArithmeticException> {
                a.divide(b)
            }
        }
    }

    @Test
    fun devTest() {
        val mode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3)
        println(BigDecimal.fromDouble(12345.678, mode).toStringExpanded())
        println("12345.678".toBigDecimal().roundSignificand(mode).toStringExpanded())
    }
}
