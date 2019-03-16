/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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