package com.ionspin.kotlin.bignum.decimal

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

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
    }

    private val bigAmountString = "12345678901234567890.12"
    private val fractionMode = DecimalMode(4, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3)
    private val bigAmount = BigDecimal.parseStringWithMode(bigAmountString, DecimalMode.US_CURRENCY)
    private val halfCent = BigDecimal.parseStringWithMode("0.005", fractionMode)

    @Test
    fun usCurrencyModeUsesScale() {
        val usdMode = DecimalMode.US_CURRENCY
        assertFalse(usdMode.isPrecisionUnlimited)
        assertTrue(usdMode.usingScale)
        assertEquals(2, usdMode.scale)
        assertEquals(2, bigAmount.scale)
        assertEquals(bigAmountString, bigAmount.toPlainString())
    }

    @Test
    fun scaledValueMatchesRoundingToDigitPosition() {
        val rounded = BigDecimal.parseStringWithMode("0.004", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            .roundToDigitPositionAfterDecimalPoint(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val scaled = BigDecimal.parseStringWithMode("0.004", fractionMode)
        assertEquals(0, rounded.compare(scaled))
    }

    @Test
    fun addingLargerScaleKeepsLargerScale() {
        val belowHalfCent = BigDecimal.parseStringWithMode("0.004", fractionMode)
        val roundedDown = bigAmount.add(belowHalfCent)
        assertEquals(3, roundedDown.scale)
        assertEquals(2, roundedDown.scale(2).scale)
        assertEquals(bigAmountString, roundedDown.scale(2).toPlainString())

        val roundedUp = bigAmount.add(halfCent)
        assertEquals(3, roundedUp.scale)
        assertEquals("12345678901234567890.13", roundedUp.scale(2).toPlainString())
    }

    @Test
    fun subtractingLargerScaleKeepsLargerScale() {
        val once = bigAmount.minus(halfCent)
        assertEquals(3, once.scale)
        assertEquals(2, once.scale(2).scale)
        assertEquals(bigAmountString, once.scale(2).toPlainString())

        val twice = once.minus(halfCent)
        assertEquals(3, twice.scale)
        assertEquals("12345678901234567890.11", twice.scale(2).toPlainString())
    }

    @Test
    fun roundingModeDecidesHalfCentRounding() {
        val towardsZero = RoundingMode.ROUND_HALF_TOWARDS_ZERO
        val amountTowardsZero =
            BigDecimal.parseStringWithMode(bigAmountString, DecimalMode.US_CURRENCY.copy(roundingMode = towardsZero))
        val halfCentTowardsZero = BigDecimal.parseStringWithMode("0.005", fractionMode.copy(roundingMode = towardsZero))

        val difference = amountTowardsZero.minus(halfCentTowardsZero).scale(2)
        assertEquals(2, difference.scale)
        assertEquals("12345678901234567890.11", difference.toPlainString())
        assertEquals(bigAmountString, bigAmount.minus(halfCent).scale(2).toPlainString())

        assertFailsWith<ArithmeticException> {
            amountTowardsZero.minus(halfCent)
        }
    }

    @Test
    fun changingAndRemovingScale() {
        var unscaled = BigDecimal.parseStringWithMode(
            "1234567.128965",
            DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        )
        var currency = BigDecimal.parseStringWithMode("1234567.13", DecimalMode.US_CURRENCY)
        assertFalse(unscaled.usingScale)

        unscaled = unscaled.scale(2)
        assertEquals(0, unscaled.compare(currency))

        unscaled = unscaled.scale(0)
        currency = currency.scale(0)
        assertEquals(0, unscaled.compare(currency))

        unscaled = unscaled.removeScale()
        currency = currency.removeScale()
        assertFalse(unscaled.usingScale)
        assertEquals(0, unscaled.compare(currency))
    }

    @Test
    fun multiplyingKeepsLargerScale() {
        val factor = BigDecimal.parseStringWithMode("2", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 0))
        val product = bigAmount.multiply(factor)
        val javaProduct = java.math.BigDecimal(bigAmountString).setScale(2).multiply(java.math.BigDecimal("2"))
        assertEquals(2, product.scale)
        assertEquals(2, javaProduct.scale())
        assertEquals(javaProduct.toPlainString(), product.toPlainString())
        assertEquals(javaProduct.setScale(2).toPlainString(), product.scale(2).toPlainString())
    }

    @Test
    fun scaleAndNegativeExponentTest() {
        val r1 = BigDecimal.parseStringWithMode(".0125", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
        val r2 = BigDecimal.parseString(".012")
        val r3 = BigDecimal.parseString(".013")
        assertEquals(3, r1.scale)
        assertEquals(0, r1.compare(r3))
        assertEquals(1, r1.compare(r2))
        val r4 = BigDecimal.parseStringWithMode(".0124", DecimalMode(10, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 3))
        assertEquals(-1, r4.compare(r3))
        assertEquals(0, r4.compare(r2))

        val r5 = BigDecimal.parseStringWithMode(".01234567890123245", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 16))
        val r6 = BigDecimal.parseString(".0123456789012324")
        val r7 = BigDecimal.parseString(".0123456789012325")
        assertEquals(16, r5.scale)
        assertEquals(1, r5.compare(r6))
        assertEquals(0, r5.compare(r7))

        val rNearZero = BigDecimal.parseStringWithMode(".00005", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        val r8 = BigDecimal.parseString(".0001")
        assertEquals(0, rNearZero.compare(r8))
        var rZero = BigDecimal.parseStringWithMode(".00004", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        assertEquals(0, rZero.compare(BigDecimal.ZERO))
        rZero = BigDecimal.parseStringWithMode(".00000004", DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 4))
        assertEquals(0, rZero.compare(BigDecimal.ZERO))
    }
}
