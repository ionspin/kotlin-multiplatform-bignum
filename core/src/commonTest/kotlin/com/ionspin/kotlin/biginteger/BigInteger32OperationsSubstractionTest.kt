package com.ionspin.kotlin.biginteger

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32OperationsSubstractionTest {

    @Test
    fun testAddition() {
        assertTrue {
            val a = uintArrayOf(10U, 20U)
            val b = uintArrayOf(15U, 5U)
            val c = BigInteger32Operations.substract(a, b)
            c[1] == 14U
        }
    }
}