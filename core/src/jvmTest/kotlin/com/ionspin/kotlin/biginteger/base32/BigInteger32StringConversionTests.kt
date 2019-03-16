package com.ionspin.kotlin.biginteger.base32

import org.junit.Test
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-3/16/19
 */
@ExperimentalUnsignedTypes
class BigInteger32StringConversionTests {



    @Test
    fun testParsing() {
        testParsingSingleTest("1234", 10)
    }

    fun testParsingSingleTest(uIntArrayString: String, base : Int) {
        assertTrue {
            val parsed = BigInteger32Arithmetic.parseForBase(uIntArrayString, base)
            val javaBigIntParsed = BigInteger(uIntArrayString, base)

            parsed.toJavaBigInteger() == javaBigIntParsed
        }

    }

    @Test
    fun randomToStringTest() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            toStringSingleTest(uintArrayOf(random.nextUInt()), 10)
        }

    }

    @Test
    fun randomToStringTestRandomBase() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            toStringSingleTest(uintArrayOf(random.nextUInt(), random.nextUInt()), random.nextInt(2,36)) //36 is the max java bigint supports
        }

    }

    @Test
    fun testToString() {
        toStringSingleTest(uintArrayOf(1234U), 10)
    }

    fun toStringSingleTest(uIntArray: UIntArray, base : Int) {
        assertTrue {
            val result = BigInteger32Arithmetic.toString(uIntArray, base)
            val javaBigIntResult = uIntArray.toJavaBigInteger().toString(base)

            result == javaBigIntResult
        }
    }

}