package com.ionspin.kotlin.biginteger.base63

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
class BigInteger64StringTest {

    @Test
    fun testToString() {
        val a = ulongArrayOf(1UL, 1UL)
        toStringSingleTest(a)
    }



    fun toStringSingleTest(ulongArray : ULongArray) {
        assertTrue {
            val result = BigInteger63Arithmetic.toString(ulongArray, 10)
            val bigIntResult = ulongArray.toJavaBigInteger().toString()
            println("Result $result \nBigInt $bigIntResult")
            result == bigIntResult
        }


    }
}