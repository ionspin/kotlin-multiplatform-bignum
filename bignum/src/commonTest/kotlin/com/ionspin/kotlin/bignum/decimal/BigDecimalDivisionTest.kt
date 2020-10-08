package com.ionspin.kotlin.bignum.decimal

import kotlin.test.Test
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
            val a = 2000.212.toBigDecimal()
            val b = 2.toBigDecimal()
            val result = a / b
            result == 1000.106.toBigDecimal()
        }

        assertTrue {
            val a = 202020.toBigDecimal()
            val b = 20.toBigDecimal()
            val result = a / b
            result == 10101.toBigDecimal()
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
}
