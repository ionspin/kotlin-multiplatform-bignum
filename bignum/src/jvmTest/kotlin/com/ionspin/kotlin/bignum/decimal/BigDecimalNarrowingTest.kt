package com.ionspin.kotlin.bignum.decimal

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests (JVM for easy debugging) on Narrowing functionality for BigDecimal.  Short, Int,
 * Long are all tested around the edges using the respective MIN_VALUE and MAX_VALUE.  Float
 * and Double are a little different, but test similar edges.
 */
class JvmBigDecimalNarrowingTest {

    @Test
    fun divideRoundingTest() {
        val dub = BigDecimal.fromDouble(Double.MIN_VALUE)
        val dub2 = dub.divide(BigDecimal.TEN)
        assertTrue(dub2 < dub)
        val dub3 = dub.multiply(BigDecimal.TEN)
        val realDub = dub3.doubleValue(true)
        assertEquals(Double.MIN_VALUE, realDub)

        val bigNumber = "12345678901234567890.123456789"
        val divisior = "87654.7654"
        val maxPrecision = bigNumber.length

        val javaDub = java.math.BigDecimal(bigNumber)
        val javaResult = javaDub.divide(java.math.BigDecimal(divisior), java.math.MathContext(maxPrecision, java.math.RoundingMode.HALF_UP))
        val result = BigDecimal.parseStringWithMode(bigNumber)
        assertFailsWith<ArithmeticException> {
            result.divide(BigDecimal.parseStringWithMode(divisior))
        }
        val result2 = result.divide(BigDecimal.parseStringWithMode(divisior), DecimalMode(maxPrecision.toLong(), RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
        val javaStr = javaResult.toEngineeringString().trimEnd('0')
        val str = result2.toStringExpanded()
        assertEquals(javaStr, str)
        assertFailsWith<ArithmeticException> {
            result2.doubleValue(true)
        }
        assertFailsWith<ArithmeticException> {
            result2.floatValue(true)
        }

        val javaDubN = java.math.BigDecimal(bigNumber).negate()
        val javaResultN = javaDubN.divide(java.math.BigDecimal(divisior).negate(), java.math.MathContext(maxPrecision, java.math.RoundingMode.HALF_UP))
        val resultN = BigDecimal.parseStringWithMode(bigNumber).negate()
        assertFailsWith<ArithmeticException> {
            resultN.divide(BigDecimal.parseStringWithMode(divisior).negate())
        }
        val result2N = resultN.divide(BigDecimal.parseStringWithMode(divisior).negate(), DecimalMode(maxPrecision.toLong(), RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
        val javaStrN = javaResultN.toEngineeringString().trimEnd('0')
        val strN = result2N.toStringExpanded()
        assertEquals(javaStrN, strN)
        assertFailsWith<ArithmeticException> {
            result2N.doubleValue(true)
        }
        assertFailsWith<ArithmeticException> {
            result2N.floatValue(true)
        }
    }

    @Test
    fun doubleValueTest() {
        var dub = BigDecimal.fromDouble(Double.MAX_VALUE)
        assertEquals(Double.MAX_VALUE, dub.doubleValue(true))
        dub = BigDecimal.fromDouble(-Double.MAX_VALUE)
        assertEquals(-Double.MAX_VALUE, dub.doubleValue(true))
        dub = BigDecimal.fromDouble(Double.MIN_VALUE)
        assertEquals(Double.MIN_VALUE, dub.doubleValue(true))
        val dub2 = dub.divide(BigDecimal.TEN)
        assertFailsWith<ArithmeticException> {
            dub2.doubleValue(true)
        }
    }

    @Test
    fun floatValueTest() {
        var f = BigDecimal.fromFloat(Float.MAX_VALUE)
        assertEquals(Float.MAX_VALUE, f.floatValue(true))
        f = BigDecimal.fromFloat(-Float.MAX_VALUE)
        assertEquals(-Float.MAX_VALUE, f.floatValue(true))
        f = BigDecimal.fromFloat(Float.MIN_VALUE)
        assertEquals(Float.MIN_VALUE, f.floatValue(true))
        assertFailsWith<ArithmeticException> {
            f.divide(BigDecimal.TEN).floatValue(true)
        }
    }

    @Test
    fun shortValueTest() {
        val i = BigDecimal.fromShort(Short.MAX_VALUE)
        assertEquals(Short.MAX_VALUE, i.shortValue())
        assertFailsWith<ArithmeticException> {
            i.plus(BigDecimal.ONE).shortValue(true)
        }

        val iN = BigDecimal.fromShort(Short.MIN_VALUE)
        assertEquals(Short.MIN_VALUE, iN.shortValue())
        assertFailsWith<ArithmeticException> {
            iN.minus(BigDecimal.ONE).shortValue(true)
        }
    }

    @Test
    fun intValueTest() {
        val i = BigDecimal.fromInt(Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, i.intValue())
        assertFailsWith<ArithmeticException> {
            i.plus(BigDecimal.ONE).intValue(true)
        }

        val iN = BigDecimal.fromInt(Int.MIN_VALUE)
        assertEquals(Int.MIN_VALUE, iN.intValue())
        assertFailsWith<ArithmeticException> {
            iN.minus(BigDecimal.ONE).intValue(true)
        }
    }

    @Test
    fun longValueTest() {
        val i = BigDecimal.fromLong(Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE, i.longValue())
        assertFailsWith<ArithmeticException> {
            i.plus(BigDecimal.ONE).longValue(true)
        }

        // kludge this for now because BigInteger longValue has error when MIN_VALUE is set
        val iN = BigDecimal.fromLong(Long.MIN_VALUE + 1)
        assertEquals(Long.MIN_VALUE+1, iN.longValue())
        assertFailsWith<ArithmeticException> {
            iN.minus(BigDecimal.ONE).minus(BigDecimal.ONE).longValue(true)
        }
    }
}
