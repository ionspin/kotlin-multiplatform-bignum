package com.ionspin.kotlin.biginteger.base32

import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-3/16/19
 */
@ExperimentalUnsignedTypes
class BigInteger32StringConversion {



    @Test
    fun testParsing() {
        testParsingSingleTest("1234", 10)
    }

    fun testParsingSingleTest(uIntArrayString: String, base : Int) {
        assertTrue {
            val parsed = BigInteger32Arithmetic.parseBase(uIntArrayString, base)
            val javaBigIntParsed = BigInteger(uIntArrayString, base)

            parsed.toJavaBigInteger() == javaBigIntParsed
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