package com.ionspin.kotlin.biginteger

import com.ionspin.kotlin.biginteger.base32.BigInteger32Arithmetic
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32ArithmeticSubstractionTest {

    @Test
    fun testAddition() {
        assertTrue {
            val a = uintArrayOf(10U, 20U)
            val b = uintArrayOf(15U, 5U)
            val c = BigInteger32Arithmetic.substract(a, b)
            c[1] == 14U
        }
    }
}