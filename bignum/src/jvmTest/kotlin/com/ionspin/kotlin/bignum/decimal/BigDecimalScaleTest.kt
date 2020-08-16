package com.ionspin.kotlin.bignum.decimal

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests (JVM for easy debugging) on arithmetic operations using the "scale" functionality
 * similar to java BigDecimal scale.
 */
class JvmBigDecimalScaleTest {

    @Test
    fun syntaxTest() {
        var e = assertFailsWith<ArithmeticException> {
            DecimalMode(scale = 3)
        }
        assertTrue(e.message?.contains("requires a RoundingMode that is not NONE") ?: false)
        e = assertFailsWith {
            DecimalMode(0, RoundingMode.ROUND_HALF_TOWARDS_ZERO)
        }
        assertTrue(e.message?.startsWith("Rounding mode with 0") ?: false)
        e = assertFailsWith {
            DecimalMode(12, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, -2)
        }
        assertTrue(e.message?.startsWith("Negative Scale") ?: false)
        e = assertFailsWith {
            DecimalMode(12, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 12)
        }
        assertTrue(e.message?.contains("must be less than precision") ?: false)
    }

    /**
     * Tests of scale functionality when used as a currency
     */
    @Test
    fun usDollarTest() {
        val bigAmountStr = "12345678901234567890.12"
        val bigAmountStr1 = "12345678901234567890.13"
        val bigAmountStr2 = "12345678901234567890.11"
        val usdMode = DecimalMode.US_CURRENCY
        val usdFractionMode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3)
        assertFalse(usdMode.isPrecisionUnlimited)
        assertTrue(usdMode.usingScale)
        assertEquals(2, usdMode.scale)
        val bigAmount = BigDecimal.parseStringWithMode(bigAmountStr, usdMode)
        assertEquals(2, usdMode.scale)
        val oldend = BigDecimal.parseStringWithMode("0.004", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
        val oldway = oldend.roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val addend = BigDecimal.parseStringWithMode("0.004", usdFractionMode)
        assertTrue(oldway.compare(addend) == 0)
        var addWrk = bigAmount.add(addend)
        assertEquals(3, addWrk.scale)
        var add = addWrk.scale(2)
        assertEquals(bigAmountStr, add.toPlainString())
        assertEquals(2, add.scale)

        var x = BigDecimal.parseStringWithMode("1234567.128965",
            DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
        assertFalse(x.usingScale)
        x = x.scale(2)
        var x1 = BigDecimal.parseStringWithMode("1234567.13", DecimalMode.US_CURRENCY)
        assertTrue(x.compare(x1) == 0)
        x = x.scale(0)
        x1 = x1.scale(0)
        assertTrue(x.compare(x1) == 0)
        x = x.removeScale()
        x1 = x1.removeScale()
        assertTrue(x.compare(x1) == 0)

        /*
        Test for rounding and scale operation when adding or subtracting larger scale on a smaller
        scale.
         */
        val halfCent = BigDecimal.parseStringWithMode("0.005", usdFractionMode)
        addWrk = bigAmount.add(halfCent)
        assertEquals(3, addWrk.scale)
        add = addWrk.scale(2)
        assertEquals(bigAmountStr1, add.toPlainString())
        assertEquals(2, add.scale)
        addWrk = bigAmount.minus(halfCent)
        assertEquals(3, addWrk.scale)
        add = addWrk.scale(2)
        assertEquals(bigAmountStr, add.toPlainString())
        assertEquals(2, add.scale)
        addWrk = bigAmount.minus(halfCent).minus(halfCent)
        assertEquals(3, addWrk.scale)
        add = addWrk.scale(2)
        assertEquals(bigAmountStr2, add.toPlainString())
        assertEquals(2, add.scale)

        /*
        Tests for rounding and scale operation when multiplying or dividing no scale operating on
        a specified scale.
         */
        val scaleZero = DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0)
        val factor = BigDecimal.parseStringWithMode("2", scaleZero)
        var wrk = bigAmount.multiply(factor)
        assertEquals(2, wrk.scale)
        var javaWrk = java.math.BigDecimal(bigAmountStr).setScale(2).multiply(java.math.BigDecimal("2"))
        assertEquals(2, javaWrk.scale())
        val s = wrk.toPlainString()
        val s1 = javaWrk.toPlainString()
        assertTrue(s.equals(s1))
        wrk = wrk.scale(2)
        javaWrk = javaWrk.setScale(2)
        assertTrue(wrk.toPlainString().equals(javaWrk.toPlainString()))
    }

    @Test
    fun scaleAndNegativeExponentTest() {
        val r1 = BigDecimal.parseStringWithMode(".0125", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
        val r2 = BigDecimal.parseString(".012")
        val r3 = BigDecimal.parseString(".013")
        assertEquals(3, r1.scale)
        assertEquals(0,r1.compare(r3))
        assertEquals(1,r1.compare(r2))
        val r4 = BigDecimal.parseStringWithMode(".0124", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
        assertEquals(-1, r4.compare(r3))
        assertEquals(0, r4.compare(r2))

        val r5 = BigDecimal.parseStringWithMode(".01234567890123245", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 16))
        val r6 = BigDecimal.parseString(".0123456789012324")
        val r7 = BigDecimal.parseString(".0123456789012325")
        assertEquals(16, r5.scale)
        assertEquals(1,r5.compare(r6))
        assertEquals(0,r5.compare(r7))

        val rNearZero = BigDecimal.parseStringWithMode(".00005", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        val r8 = BigDecimal.parseString(".0001")
        assertEquals(0,rNearZero.compare(r8))
        var rZero = BigDecimal.parseStringWithMode(".00004", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        assertEquals(0,rZero.compare(BigDecimal.ZERO))
        rZero = BigDecimal.parseStringWithMode(".00000004", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        assertEquals(0,rZero.compare(BigDecimal.ZERO))
    }
}
