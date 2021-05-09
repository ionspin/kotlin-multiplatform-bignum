package com.ionspin.kotlin.bignum.decimal

import kotlin.test.assertEquals
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-May-2021
 */
class ReportedIssuesTest {

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
