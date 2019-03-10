package com.ionspin.kotlin.biginteger.base32

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
class BigInteger32StringTest {

    @Test
    fun testToString() {
        val a = uintArrayOf(1U, 1U)
        toStringSingleTest(a)
    }

    @Test
    fun randomToStringTest() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            toStringSingleTest(uintArrayOf(random.nextUInt()))
        }

    }



    fun toStringSingleTest(uintArray : UIntArray) {
        assertTrue {
            val result = BigInteger32Arithmetic.toString(uintArray, 10)
            val bigIntResult = uintArray.toJavaBigInteger().toString()
            result == bigIntResult
        }


    }
}