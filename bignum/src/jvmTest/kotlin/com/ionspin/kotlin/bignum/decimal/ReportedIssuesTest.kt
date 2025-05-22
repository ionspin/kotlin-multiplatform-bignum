package com.ionspin.kotlin.bignum.decimal

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-May-2021
 */
class ReportedIssuesTest {

    @Test
    fun testMod2() {

        val a = "15.5".toBigDecimal()
        val b = "360".toBigDecimal()

        assertEquals("15.5", ((b * 5 + a) % b).toStringExpanded())

        assertEquals("${-15.5 % 360.0}", (-a % b).toStringExpanded())
    }

    @Test
    fun multiplicationPrecisionLoss() {
        val a = "5.61".toBigDecimal().roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val b = "2.95".toBigDecimal().roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val aJava = a.toJavaBigDecimal()
        val bJava = b.toJavaBigDecimal()
        val javaRes = aJava.multiply(bJava).setScale(2, java.math.RoundingMode.HALF_UP)
        val res = a.multiply(b).roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        assertEquals(res.toStringExpanded(), javaRes.toPlainString())
    }
}
