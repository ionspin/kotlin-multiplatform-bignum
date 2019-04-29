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

package com.ionspin.kotlin.bignum.integer.base32

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaStringConversionTests  {


    @Test
    fun `Test parsing with sepcific values`() {
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
    fun `Random toString test, in base 10`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {
            jobList.add(
                GlobalScope.launch {
                    toStringSingleTest(uintArrayOf(random.nextUInt()), 10)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Random toString test, in random base less than 36`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {
            jobList.add(
                GlobalScope.launch {
                    toStringSingleTest(
                        uintArrayOf(random.nextUInt(), random.nextUInt()),
                        random.nextInt(2, 36)
                    ) //36 is the max java bigint supports
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Test toString with specific values`() {
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