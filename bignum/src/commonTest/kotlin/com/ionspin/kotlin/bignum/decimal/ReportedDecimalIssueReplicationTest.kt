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
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 15-Feb-2020
 */

class ReportedDecimalIssueReplicationTest {

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

    @Test
    fun testHashCode() {
        val DECIMAL_MODE = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 6)
        val matchingScaleLastDigitNonZero = "1.23456701E+2"
        val matchingScaleLastDigitZero = "1.23456700E+2"
        val smallerScale = "1.23456E+2"
        val largerScale = "1.23456701234E+2"
        val msnz = matchingScaleLastDigitNonZero.toBigDecimal()
        val msnzdm = matchingScaleLastDigitNonZero.toBigDecimal(decimalMode = DECIMAL_MODE)
        val ms0 = matchingScaleLastDigitZero.toBigDecimal()
        val ms0dm = matchingScaleLastDigitZero.toBigDecimal(decimalMode = DECIMAL_MODE)
        val ss = smallerScale.toBigDecimal()
        val ssdm = smallerScale.toBigDecimal(decimalMode = DECIMAL_MODE)
        val lsdm = largerScale.toBigDecimal(decimalMode = DECIMAL_MODE)
        assertEquals(msnz, msnzdm) // ok
        assertEquals(msnz.hashCode(), msnzdm.hashCode()) // ok
        assertEquals(ms0, ms0dm) // ok
        assertEquals(ms0.hashCode(), ms0dm.hashCode()) // hashcodes differ
        assertEquals(ss, ssdm) // ok
        assertEquals(ss.hashCode(), ssdm.hashCode()) // hashcodes differ
        assertEquals(msnz, lsdm) // ok
        assertEquals(msnz.hashCode(), lsdm.hashCode()) // ok
    }

    @Test
    fun testNegativeDouble() {
        val bigDecimal = BigDecimal.parseString("-1234.1234")
        assertTrue(bigDecimal.isNegative) // ok
        val doubleFromString = bigDecimal.toPlainString().toDouble() // ok
        val asDoubleValue = bigDecimal.doubleValue(false) // loses the sign
        assertTrue(doubleFromString < 0)
        assertFalse(asDoubleValue > 0) // Double is positive...
    }

    @Test
    fun precisionLossOnCarryTest() {
        val bd = BigDecimal.parseStringWithMode("8.099172", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 6))
        println(bd)
        val bd2 = BigDecimal.parseStringWithMode("8.", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 6))
        println(bd2)
        val sum = bd2.plus(bd)
        assertEquals("16.099172", sum.toStringExpanded())

        assertTrue {
            val a = 8.099172.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 6))
            val b = 8.toBigDecimal()
            val result = a + b
            result.toStringExpanded() == "16.099172"
        }

        assertTrue {
            val a = 10.123.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = 1.0001.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
            val result = a.subtract(b, DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            result.toStringExpanded() == "9.123"
        }

        assertTrue {
            val a = 2.001.toBigDecimal(decimalMode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = 5.0.toBigDecimal(decimalMode = DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val result = a * b
            result.toStringExpanded() == "10.005"
        }

        assertTrue {
            val a = 10.005.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = 5.0.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val result = a / b
            result.toStringExpanded() == "2.001"
        }
    }

    @Test
    fun precisionLossOnCarry2() {
        assertTrue {
            val a = 5000.0.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = (-4999.9).toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val result = a + b
            result == 0.1.toBigDecimal()
        }

        assertTrue {
            val a = (-5000.0).toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = 4999.9.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val result = a + b
            result == (-0.1).toBigDecimal()
        }

        assertTrue {
            val a = (-5000.0).toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val b = 4999.9.toBigDecimal(decimalMode = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
            val result = a + b
            result == (-0.1).toBigDecimal()
        }

        assertTrue {
            val a = 10.toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val b = (-1).toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val result = a.add(b, DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            result == 9.toBigDecimal() && result.precision == 1L
        }

        assertTrue {
            val a = 100.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val b = (-99).toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val result = a.add(b, DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            result == 1.toBigDecimal() && result.precision == 1L
        }

        assertTrue {
            val a = 10.toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val b = (0.1).toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 1))
            val result = a.multiply(b, DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            result == 1.toBigDecimal() && result.precision == 1L
        }

        assertTrue {
            val a = 100.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val b = (0.01).toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2))
            val result = a.multiply(b, DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            result == 1.toBigDecimal() && result.precision == 1L
        }

        assertTrue {
            val a = 10.toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            val b = (0.1).toBigDecimal(decimalMode = DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 1))
            val result = a.multiply(b, DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
            result == 1.toBigDecimal() && result.precision == 1L
        }

        assertTrue {
            val a = 100.0001.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
            val b = 1000.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2))
            val result = a.multiply(b, DecimalMode(8, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 1))
            result == 100000.1.toBigDecimal() && result.precision == 7L
        }

        assertTrue {
            val a = 1.01.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
            val b = 0.1.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2))
            val result = a.divide(b, DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 1))
            result == 10.1.toBigDecimal() && result.precision == 3L
        }

        assertTrue {
            val a = 1.toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
            val b = (-1).toBigDecimal(decimalMode = DecimalMode(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
            val result = a + b
            result == BigDecimal.ZERO
        }
    }

    @Test
    fun additionAndSubstractionWithZeroAndNegativeExponent() {
        val zeroBigDecimal = BigDecimal.ZERO
        val bigDecimalWithNegativeExponent = 0.5.toBigDecimal()

        assertTrue {
            zeroBigDecimal + bigDecimalWithNegativeExponent == bigDecimalWithNegativeExponent
        }

        assertTrue {
            bigDecimalWithNegativeExponent + zeroBigDecimal == bigDecimalWithNegativeExponent
        }

        assertTrue {
            zeroBigDecimal - bigDecimalWithNegativeExponent == -bigDecimalWithNegativeExponent
        }

        assertTrue {
            bigDecimalWithNegativeExponent - zeroBigDecimal == bigDecimalWithNegativeExponent
        }
    }

    @Test
    fun testToDouble() {
        val result = "29514.9598393574297189".toBigDecimal()
        val doubleValue = result.doubleValue(false)
        println("$doubleValue")
        val required: Double = 29514.9598393574297189
        assertEquals(29514.9598393574297189, doubleValue)
    }

    @Test
    fun testToPlainStringScale() {
        assertEquals("1.000000", 1000000.toBigDecimal().moveDecimalPoint(-6).scale(6).toPlainString())
    }

    @Test
    fun testIsWhole() {
        val bigDecimalWhole = "1.1234567826".toBigDecimal(
            decimalMode = DecimalMode(18, RoundingMode.ROUND_HALF_CEILING, 18)
        ).moveDecimalPoint(9)

        assertEquals("1123456782.600000000000000000", bigDecimalWhole.toPlainString())
        assertEquals(false, bigDecimalWhole.isWholeNumber())
    }

    @Test
    fun testMoveDecimalPoint() {
        val bigDecimal = "1.123456780".toBigDecimal()
        val eightPlaces = bigDecimal.moveDecimalPoint(8)
        assertEquals(112345678L, eightPlaces.longValue())
        val tenPlaces = bigDecimal.moveDecimalPoint(10)
        assertEquals(11234567800L, tenPlaces.longValue())
        val ninePlaces = bigDecimal.moveDecimalPoint(9)
        assertEquals(1123456780L, ninePlaces.longValue())
    }

    @Test
    fun showBigDecimalWithHigherPrecision() {
        val bigDecimal = "345.67".toBigDecimal(decimalMode = DecimalMode(scale = 5, roundingMode = RoundingMode.CEILING))
        println("Significand: ${bigDecimal.significand}")
        println("Exponent: ${bigDecimal.exponent}")
        println("Precision ${bigDecimal.precision}")
        println("Is using scale? ${bigDecimal.usingScale} Scale ${bigDecimal.scale}")
        println("DecimalMode ${bigDecimal.decimalMode}")
    }

    @Test
    fun testRemainder() {
        val a = BigDecimal.fromDouble(1815.5)
        val b = BigDecimal.fromInt(360)

        val (q, r) = a.divideAndRemainder(b)
        assertEquals("5", q.toStringExpanded())
        assertEquals("15.5", r.toStringExpanded())
    }

    @Test
    fun roundingHalfEven() {
        val bigDecimal = BigDecimal.parseString("0.5")
        val rounded = bigDecimal.roundToDigitPositionAfterDecimalPoint(0, roundingMode = RoundingMode.ROUND_HALF_TO_EVEN)
        assertEquals(
            BigDecimal.ZERO,
            rounded
        )
    }

    @Test
    fun roundHalfAway() {
        val result = BigDecimal.fromInt(2)
            .divide(BigDecimal.fromInt(3), DecimalMode(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            .doubleValue(false)
        assertEquals(0.67f, result.toFloat())
    }

    @Test
    fun github316toByteArrayTest() {
        val a = BigInteger.ZERO.toByteArray()
        val b = BigInteger.fromULong(0uL).toByteArray()
        val c = BigInteger.fromLong(0L).toByteArray()
        val d = BigInteger.fromInt(0).toByteArray()

        assertContentEquals(a, b)
        assertContentEquals(a, c)
        assertContentEquals(a, d)
    }

    @Test
    fun github337RoundHalfToEvenDivisionTest() {
        val result = 145690.toBigDecimal().multiply(11.toBigDecimal())
            .divide(30.toBigDecimal(), DecimalMode(7L, RoundingMode.ROUND_HALF_TO_EVEN))
        assertEquals("53419.67".toBigDecimal(), result)
    }

    @Test
    fun github331DivisionWithScaleAndUnlimitedPrecisionTest() {
        run {
            val result = "460.0".toBigDecimal().divide(
                other = 40.toBigDecimal(),
                decimalMode = DecimalMode(scale = 2, roundingMode = RoundingMode.FLOOR)
            )
            assertEquals("11.5".toBigDecimal(), result)
            assertEquals(2L, result.scale)
        }

        run {
            val result = "460.0".toBigDecimal().divide(
                other = 40.toBigDecimal(),
                decimalMode = DecimalMode(decimalPrecision = 6, scale = 2, roundingMode = RoundingMode.FLOOR)
            )
            assertEquals("11.5".toBigDecimal(), result)
        }
    }

    @Test
    fun github330CreationWithScaleAndUnlimitedPrecisionTest() {
        val decimalMode = DecimalMode(scale = 2, roundingMode = RoundingMode.ROUND_HALF_TO_EVEN)

        run {
            val a = 1234.0.toBigDecimal(decimalMode = decimalMode)
            val b = "1234.0".toBigDecimal(decimalMode = decimalMode)
            assertEquals("1234".toBigDecimal(), a)
            assertEquals(b, a)
            assertEquals(2L, a.scale)
        }

        run {
            val a = 1234.toBigDecimal(decimalMode = decimalMode)
            assertEquals("1234".toBigDecimal(), a)
        }

        run {
            val a = 1234.567.toBigDecimal(decimalMode = decimalMode)
            assertEquals("1234.57".toBigDecimal(), a)
        }
    }

    @Test
    fun github318DivisionRoundingCarryTest() {
        val a = "10858.9200008919".toBigDecimal()
        val b = "108589.2300018812".toBigDecimal()
        val roundedDown = listOf("0.099999", "0.0999999", "0.09999997")
        val roundedUp = listOf("0.1", "0.1", "0.09999998")
        val roundedToNearest = listOf("0.1", "0.1", "0.09999997")
        val expected = mapOf(
            RoundingMode.FLOOR to roundedDown,
            RoundingMode.TOWARDS_ZERO to roundedDown,
            RoundingMode.CEILING to roundedUp,
            RoundingMode.AWAY_FROM_ZERO to roundedUp,
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO to roundedToNearest,
            RoundingMode.ROUND_HALF_TOWARDS_ZERO to roundedToNearest,
            RoundingMode.ROUND_HALF_CEILING to roundedToNearest,
            RoundingMode.ROUND_HALF_FLOOR to roundedToNearest,
            RoundingMode.ROUND_HALF_TO_EVEN to roundedToNearest,
            RoundingMode.ROUND_HALF_TO_ODD to roundedToNearest
        )
        expected.forEach { (mode, results) ->
            listOf(5L, 6L, 7L).forEachIndexed { index, precision ->
                val result = a.divide(b, DecimalMode(precision, mode))
                assertEquals(results[index].toBigDecimal(), result)
            }
        }
    }

    @Test
    fun github284ZeroWithScaleKeepsScaleTest() {
        run {
            val result = BigDecimal.parseString("1").scale(0) + BigDecimal.parseString("2").scale(1)
            assertEquals(1L, result.scale)
            assertEquals("3".toBigDecimal(), result)
        }

        run {
            val result = BigDecimal.parseString("0").scale(1) + BigDecimal.parseString("2").scale(1)
            assertEquals(1L, result.scale)
            assertEquals("2".toBigDecimal(), result)
        }

        run {
            val result = BigDecimal.parseString("0").scale(0) + BigDecimal.parseString("2").scale(1)
            assertEquals(1L, result.scale)
            assertEquals("2".toBigDecimal(), result)
        }

        run {
            val result = BigDecimal.parseString("2").scale(1) + BigDecimal.parseString("0").scale(0)
            assertEquals(1L, result.scale)
            assertEquals("2".toBigDecimal(), result)
        }

        run {
            val result = BigDecimal.parseString("1").scale(0) - BigDecimal.parseString("2").scale(1)
            assertEquals(1L, result.scale)
            assertEquals("-1".toBigDecimal(), result)
        }

        run {
            val result = BigDecimal.parseString("0").scale(0) - BigDecimal.parseString("2").scale(1)
            assertEquals(1L, result.scale)
            assertEquals("-2".toBigDecimal(), result)
        }
    }

    @Test
    fun github276DivideAndRemainderTest() {
        run {
            val full = BigDecimal.fromInt(360)
            val x = BigDecimal.fromDouble(15.5) + full * 5
            val (quotient, remainder) = x.divideAndRemainder(full)
            assertEquals("5".toBigDecimal(), quotient)
            assertEquals("15.5".toBigDecimal(), remainder)
            assertEquals("15.5".toBigDecimal(), x % full)
        }

        run {
            val full = BigDecimal.fromInt(360)
            val x = BigDecimal.fromDouble(-15.5) - full * 5
            val (quotient, remainder) = x.divideAndRemainder(full)
            assertEquals("-5".toBigDecimal(), quotient)
            assertEquals("-15.5".toBigDecimal(), remainder)
        }
    }

    @Test
    fun github267UnlimitedPrecisionWithScaleTest() {
        run {
            val mode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, scale = 4)
            assertEquals("123".toBigDecimal(), BigDecimal.fromInt(123, decimalMode = mode))
            assertEquals("1234".toBigDecimal(), BigDecimal.fromInt(1234, decimalMode = mode))
            assertEquals("12345".toBigDecimal(), BigDecimal.fromInt(12345, decimalMode = mode))
            assertEquals("12345678".toBigDecimal(), BigDecimal.fromInt(12345678, decimalMode = mode))
            assertEquals("12345678".toBigDecimal(), BigDecimal.fromLong(12345678L, decimalMode = mode))
            assertEquals(4L, BigDecimal.fromInt(12345678, decimalMode = mode).scale)
            assertEquals(
                "12345678.1235".toBigDecimal(),
                "12345678.1234567".toBigDecimal(decimalMode = mode)
            )
        }

        run {
            val mode = DecimalMode(roundingMode = RoundingMode.ROUND_HALF_CEILING, scale = 8)
            val a = "200.5230146281866563757715403019550414480770532190116073".toBigDecimal()
            val b = "1111.99009855".toBigDecimal()
            val result = a.divide(b, mode)
            assertEquals("0.18032806".toBigDecimal(), result)
        }
    }

    @Test
    fun github263ScientificNotationTest() {
        val expected = BigInteger.parseString("1000000000000000000000000000000")
        assertEquals(expected, BigInteger.parseString("1e+30"))
        assertEquals(expected, BigInteger.parseString("1E30"))
        assertEquals(expected, BigInteger.parseString("1.0e+30"))
        assertEquals(expected.negate(), BigInteger.parseString("-1e+30"))
        assertEquals(BigInteger.parseString("1200"), BigInteger.parseString("12e2"))
        assertEquals(BigInteger.parseString("1500"), BigInteger.parseString("1.5e+3"))
        assertFailsWith<NumberFormatException> {
            BigInteger.parseString("5e-1")
        }
        assertEquals(BigInteger.parseString("30"), BigInteger.parseString("1e", 16))
    }


}
