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

import kotlinx.coroutines.*
import org.junit.Test
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */
@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class BigInteger32JavaMultiplyTest {

    @Test
    fun `Test for sentimental value`() {
        assertTrue {
            val a = uintArrayOf(10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf(10U, 10U)
            val b = uintArrayOf(20U, 20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf((0U - 1U), 10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }


    }

    @Test
    fun `Test multiplying three words`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 1..Int.MAX_VALUE step 5001) {
            jobList.add(
                GlobalScope.launch {
                    multiplySingleTest(random.nextUInt(), random.nextUInt(), random.nextUInt())
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Multiply two large words`()  {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val first = UIntArray(numberOfElements) {
            random.nextUInt()
        }

        val second = UIntArray(numberOfElements) {
            random.nextUInt()
        }
        multiplySingleTestArray(first, second)
    }

    @Test
    fun `Test multiplying a lot of words`() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val first = UIntArray(numberOfElements) {
            random.nextUInt()
        }

        multiplySingleTest(*first)
    }

    fun multiplySingleTest(vararg elements: UInt) {
        assertTrue("Failed on ${elements.contentToString()}") {
            val time = false
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = elements.foldIndexed(UIntArray(1) { 1U }) { index, acc, uInt ->
                BigInteger32Arithmetic.multiply(acc, uInt)
            }
            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = elements.foldIndexed(BigInteger.ONE) { index, acc, uInt ->
                acc * BigInteger(uInt.toString(), 10)
            }
            if (time) {
                lastTime = LocalDateTime.now()
                println("Java Big Integer total time ${Duration.between(startTime, lastTime)}")
            }

            bigIntResult == convertedResult
        }
    }

    fun multiplySingleTestArray(first: UIntArray, second: UIntArray) {
        assertTrue(
            "Failed on uintArrayOf(${first.joinToString(separator = ", ")})" +
                    ", uintArrayOf(${second.joinToString(separator = ", ")})"
        ) {
            val time = true
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime
            println("Creating java big integers")
            var firstBigInt = first.toJavaBigInteger()
            var secondBigInt = second.toJavaBigInteger()

            println("Starting")
            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = BigInteger32Arithmetic.multiply(first, second)

            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }

            val bigIntResult = firstBigInt * secondBigInt

            if (time) {
                lastTime = LocalDateTime.now()
                println("Java Big Integer total time ${Duration.between(startTime, lastTime)}")
            }
            val resultBigInt = result.toJavaBigInteger()
            bigIntResult == resultBigInt
        }
    }
}