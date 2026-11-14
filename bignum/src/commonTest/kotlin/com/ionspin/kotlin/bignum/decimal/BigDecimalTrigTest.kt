package com.ionspin.kotlin.bignum.decimal

import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalTrigTest {

    @Test
    fun sinTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "0",
            "0.2" to "0.19866933079506121545",
            "0.4" to "0.38941834230865049166",
            "0.6" to "0.56464247339503535720",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).sin(decimalMode))
        }
    }

    @Test
    fun cosTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "1",
            "0.2" to "0.98006657784124163112",
            "0.4" to "0.92106099400288508279",
            "0.6" to "0.82533561490967829724",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).cos(decimalMode))
        }
    }

    @Test
    fun tanTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "0",
            "0.2" to "0.20271003550867248332",
            "0.4" to "0.42279321873816176198",
            "0.6" to "0.68413680834169231707",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).tan(decimalMode))
        }
    }

    @Test
    fun sinhTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "0",
            "0.2" to "0.20133600254109398762",
            "0.4" to "0.41075232580281550854",
            "0.6" to "0.63665358214824127112",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).sinh(decimalMode))
        }
    }

    @Test
    fun coshTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "1",
            "0.2" to "1.0200667556190758462",
            "0.4" to "1.0810723718384548092",
            "0.6" to "1.1854652182422677037",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).cosh(decimalMode))
        }
    }

    @Test
    fun tanhTest() {
        val decimalMode = DecimalMode(decimalPrecision = 20, roundingMode = RoundingMode.TOWARDS_ZERO)
        val examples = listOf(
            "0" to "0",
            "0.2" to "0.19737532022490400073",
            "0.4" to "0.37994896225522488526",
            "0.6" to "0.53704956699803528586",
        )

        for ((input, expected) in examples) {
            assertEquals(BigDecimal.parseString(expected), BigDecimal.parseString(input).tanh(decimalMode))
        }
    }
}
