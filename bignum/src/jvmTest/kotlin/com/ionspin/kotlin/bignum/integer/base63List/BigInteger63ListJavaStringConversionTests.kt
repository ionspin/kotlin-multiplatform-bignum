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

package com.ionspin.kotlin.bignum.integer.base63List

import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-2019
 */

class BigInteger63ListJavaStringConversionTests() {

    @Test
    fun `Test parsing with sepcific values`() {
//        testParsingSingleTest("1234", 10)
//        testParsingSingleTest("922337203685477580799999999999990776627963145224192", 10)
        testParsingSingleTest("52656145834278593348959013841835216159447547700274555627155488768", 10)
        testParsingSingleTest("f", 16)
    }

    fun testParsingSingleTest(uIntArrayString: String, base: Int) {
        assertTrue {
            val parsed = BigInteger63LinkedListArithmetic.parseForBase(uIntArrayString, base)
            val javaBigIntParsed = BigInteger(uIntArrayString, base)

            parsed.toJavaBigInteger() == javaBigIntParsed
        }
    }

    @Test
    fun `Random toString test, in base 10`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 1..Int.MAX_VALUE step 7001) {
            jobList.add(
                GlobalScope.launch {
                    toStringSingleTest(listOf(random.nextULong()), 10)
                }
            )
        }
        runBlocking {
            jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }
        }
    }

    @Test
    fun `Random toString test, in random base less than 36`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 7001) {
            jobList.add(
                GlobalScope.launch {
                    toStringSingleTest(
                        listOf(random.nextULong(), random.nextULong()),
                        random.nextInt(2, 36)
                    ) // 36 is the max java bigint supports
                }
            )
        }

        runBlocking {
            jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }
        }
    }

    @Test
    fun `Test toString with specific values`() {
        toStringSingleTest(listOf(1234U), 10)
    }

    fun toStringSingleTest(operand: List<ULong>, base: Int) {
        assertTrue {
            val result = BigInteger63LinkedListArithmetic.toString(operand, base)
            val javaBigIntResult = operand.toJavaBigInteger().toString(base)

            result == javaBigIntResult
        }
    }
}
