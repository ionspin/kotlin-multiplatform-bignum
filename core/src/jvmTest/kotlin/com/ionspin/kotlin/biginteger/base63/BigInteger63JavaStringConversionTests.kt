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

package com.ionspin.kotlin.biginteger.base63

import com.ionspin.kotlin.biginteger.base63.BigInteger63Arithmetic.divrem
import org.junit.Test
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-3/16/19
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaStringConversionTests {


    @Test
    fun `Test parsing with sepcific values`() {
//        testParsingSingleTest("1234", 10)
//        testParsingSingleTest("922337203685477580799999999999990776627963145224192", 10)
        testParsingSingleTest("52656145834278593348959013841835216159447547700274555627155488768", 10)
    }

    fun testParsingSingleTest(uIntArrayString: String, base: Int) {
        assertTrue {
            val parsed = BigInteger63Arithmetic.parseForBase(uIntArrayString, base)
            val javaBigIntParsed = BigInteger(uIntArrayString, base)

            parsed.toJavaBigInteger() == javaBigIntParsed
        }

    }

    @Test
    fun `Random toString test, in base 10`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 7001) {
            toStringSingleTest(ulongArrayOf(random.nextULong()), 10)
        }

    }

    @Test
    fun `Random toString test, in random base less than 36`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 7001) {
            toStringSingleTest(
                ulongArrayOf(random.nextULong(), random.nextULong()),
                random.nextInt(2, 36)
            ) //36 is the max java bigint supports
        }

    }

    @Test
    fun `Test toString with specific values`() {
        toStringSingleTest(ulongArrayOf(1234U), 10)
    }

    fun toStringSingleTest(uLongArray: ULongArray, base: Int) {
        assertTrue {
            val result = BigInteger63Arithmetic.toString(uLongArray, base)
            val javaBigIntResult = uLongArray.toJavaBigInteger().toString(base)

            result == javaBigIntResult
        }
    }







}