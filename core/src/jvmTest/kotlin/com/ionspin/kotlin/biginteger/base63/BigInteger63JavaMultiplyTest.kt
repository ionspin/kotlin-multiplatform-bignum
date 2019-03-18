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

import com.ionspin.kotlin.biginteger.util.runTest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaMultiplyTest {
    @Test
    fun `Test for sentimental value`() {
        assertTrue {
            val a = ulongArrayOf(10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = ulongArrayOf(10U, 10U)
            val b = ulongArrayOf(20U, 20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = ulongArrayOf((0UL - 1UL), 10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }


    }

    @Test
    fun `Test multiplying three words`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 5001) {
            multiplySingleTest(random.nextULong() shr 1, random.nextULong() shr 1, random.nextULong() shr 1)
        }

    }

    @Test
    fun `Multiply two large words`() = runTest {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val first = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }

        val second = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }
        multiplySingleTest(first, second)
    }

    @Test
    fun `Test multiplying a lot of words`() = runTest {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val first = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }

        multiplySingleTest(*first)
    }

    @Test
    fun preciseMultiplyTest() {
        multiplySingleTest(ulongArrayOf(3751237528UL, 9223372035661198284UL, 7440555637UL, 0UL, 2UL, 0UL, 2UL), ulongArrayOf(1UL, 1UL))
//        multiplySingleTest(1193170172382743678UL, 17005332033106823254UL, 15532449225048523230UL) Invalid sample, 64 bit number!
    }

    fun multiplySingleTest(first : ULongArray, second : ULongArray) {
        assertTrue("Failed on ulongArrayOf(${first.joinToString(separator = ", ") { it.toString() + "UL" }})," +
                " ulongArrayOf(${second.joinToString(separator = ", ") { it.toString() + "UL" }})") {

            val result = BigInteger63Arithmetic.multiply(first, second)


            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
            bigIntResult == convertedResult
        }
    }

    fun multiplySingleTest(vararg elements: ULong) {
        val elementsArray = elements
        assertTrue("Failed on (${elementsArray.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val time = elements.size > 100
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = elements.fold(ULongArray(1) { 1U }) { acc, uLong ->
                BigInteger63Arithmetic.multiply(acc, uLong)
            }
            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = elements.fold(BigInteger.ONE) { acc, uInt ->
                acc * BigInteger(uInt.toString(), 10)
            }
            if (time) {
                println("Result ${convertedResult}")
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
            }

            bigIntResult == convertedResult
        }
    }
}