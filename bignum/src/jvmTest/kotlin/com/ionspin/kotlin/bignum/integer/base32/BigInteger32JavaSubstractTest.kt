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

import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */

class BigInteger32JavaSubtractTest {

    val basePower = 32

    @Test
    fun `Test subtraction with specific values`() {
        assertTrue {
            val a = uintArrayOf(10U, 20U)
            val b = uintArrayOf(15U, 5U)
            val c = BigInteger32Arithmetic.subtract(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val cBigInt = aBigInt - bBigInt

            resultBigInt == cBigInt
        }
    }

    @Test
    fun `Test subtraction with random values`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 1..Int.MAX_VALUE step 5001) {
            jobList.add(
                GlobalScope.launch {
                    subtractSingleTest(random.nextUInt(), random.nextUInt(), random.nextUInt())
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun `Test subtraction with a large number of random values`() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val lotOfElements = UIntArray(numberOfElements) {
            random.nextUInt()
        }
        subtractSingleTest(*lotOfElements)
    }

    fun subtractSingleTest(vararg elements: UInt) {
        assertTrue("Failed on ${elements.contentToString()}") {
            val time = false
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val first =
                elements.copyOfRange(0, elements.size / 2).fold(UIntArray(1) { 1U }) { acc, uInt ->
                    BigInteger32Arithmetic.multiply(acc, uInt)
                }
            val second = elements.copyOfRange(elements.size / 2, elements.size - 1)
                .fold(UIntArray(1) { 1U }) { acc, uInt ->
                    BigInteger32Arithmetic.multiply(acc, uInt)
                }
            val result = BigInteger32Arithmetic.subtract(first, second)

            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }

            val convertedResult = result.toJavaBigInteger()
            val bigIntFirst =
                elements.copyOfRange(0, elements.size / 2).fold(BigInteger.ONE) { acc, uInt ->
                    acc * BigInteger(uInt.toString(), 10)
                }
            val bigIntSecond = elements.copyOfRange(elements.size / 2, elements.size - 1)
                .fold(BigInteger.ONE) { acc, uInt ->
                    acc * BigInteger(uInt.toString(), 10)
                }
            val bigIntResult = bigIntFirst - bigIntSecond
            if (time) {
                println("Result $convertedResult")
                println("Total time ${Duration.between(startTime, lastTime)}")
            }

            bigIntResult.abs() == convertedResult
        }
    }
}
