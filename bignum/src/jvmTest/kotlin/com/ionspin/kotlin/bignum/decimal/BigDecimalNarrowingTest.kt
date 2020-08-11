package com.ionspin.kotlin.bignum.decimal

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JvmBigDecimalNarrowingTest {

    @Test
    fun divideRoundingTest() {
        val dub = BigDecimal.fromDouble(Double.MIN_VALUE)
        val dub2 = dub.divide(BigDecimal.TEN)
        assertTrue(dub2 < dub)
        val dub3 = dub.multiply(BigDecimal.TEN)
        val realDub = dub3.doubleValue(true)
        assertEquals(Double.MIN_VALUE, realDub)
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
}
