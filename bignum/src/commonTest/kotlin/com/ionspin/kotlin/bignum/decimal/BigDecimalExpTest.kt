package com.ionspin.kotlin.bignum.decimal

import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalExpTest {

    @Test
    fun expTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "1",
            "0.2" to "1.2214027581601698339",
            "0.4" to "1.4918246976412703178",
            "0.6" to "1.8221188003905089748",
            "0.8" to "2.2255409284924676045",
            "1" to "2.7182818284590452353",
            "2" to "7.3890560989306502272",
            "-0.2" to "0.81873075307798185866",
            "-0.4" to "0.67032004603563930074",
            "-0.6" to "0.54881163609402643262",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).exp(decimalMode))
        }
    }
}