package com.ionspin.kotlin.biginteger

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32ArithmeticAdditionTest {

    @Test
    fun testAddition() {
        assertTrue {
            val a = uintArrayOf(1U)
            val b = uintArrayOf(2U)
            val c = BigInteger32Arithmetic.addition(a, b)
            c[0] == 3U
        }
    }
}