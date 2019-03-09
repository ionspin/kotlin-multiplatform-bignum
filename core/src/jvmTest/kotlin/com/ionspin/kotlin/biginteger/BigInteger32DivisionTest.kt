package com.ionspin.kotlin.biginteger

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32DivisionTest {

    @Test
    fun testDivision() {
        assertTrue {
            val a = uintArrayOf(40U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Operations.basicDivide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder

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
    }
}