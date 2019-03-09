package com.ionspin.kotlin.biginteger

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaSubstractTest {

    val basePower = 32

    @Test
    fun substractionTest() {
        assertTrue {
            val a = uintArrayOf(10U, 20U)
            val b = uintArrayOf(15U, 5U)
            val c = BigInteger32Operations.substract(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val cBigInt = aBigInt - bBigInt

            resultBigInt == cBigInt


        }
    }
}