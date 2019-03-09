package com.ionspin.kotlin.biginteger

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaMultiplyTest {
    @Test
    fun testMultiply() {
        assertTrue {
            val a = uintArrayOf(10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Operations.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf(10U, 10U)
            val b = uintArrayOf(20U, 20U)
            val c = BigInteger32Operations.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf((0U - 1U), 10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Operations.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

    }
}