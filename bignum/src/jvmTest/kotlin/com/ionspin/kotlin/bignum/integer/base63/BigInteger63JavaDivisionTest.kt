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

package com.ionspin.kotlin.bignum.integer.base63

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception
import java.lang.RuntimeException
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaDivisionTest {

    @Test
    fun testDivision() {
        assertTrue {
            val a = ulongArrayOf(40U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder

        }

        assertTrue {
            val a = ulongArrayOf(20U, 20U)
            val b = ulongArrayOf(10U, 10U)
            val c = BigInteger63Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

            val bla = 1L
            bla.toBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
        }
    }

    @Test
    fun `Test division with only one word`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 3001) {
            val a = random.nextULong() shr 1
            val b = random.nextULong() shr 1
            if (a > b) {
                divisionSingleTest(ulongArrayOf(a), ulongArrayOf(b))
            } else {
                divisionSingleTest(ulongArrayOf(b), ulongArrayOf(a))
            }

        }

    }

    @Test
    fun `Test two word divided by two words`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            val b = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            GlobalScope.launch {
                if (BigInteger63Arithmetic.compare(a, b) > 0) {
                    divisionSingleTest(a, b)
                } else {
                    divisionSingleTest(b, a)
                }
            }


        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Test four words divided by two words`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1
            )
            val b = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            GlobalScope.launch {
                try {
                    if (BigInteger63Arithmetic.compare(a, b) > 0) {
                        divisionSingleTest(a, b)
                    } else {
                        divisionSingleTest(b, a)
                    }
                } catch (e: Throwable) {
                    println(
                        "Failed on ulongArrayOf(${a.joinToString(separator = ",") { it.toString() + "U" }}), " +
                                "ulongArrayOf(${b.joinToString(separator = ",") { it.toString() + "U" }})"
                    )
                    e.printStackTrace()
                }
            }

        }

    }


    @Test
    fun preciseDebugTest() {
        divisionSingleTest(
            ulongArrayOf(
                7011262718134162982U,
                165064388400841479U,
                8071396034697521068U,
                3707335022938319120U
            ), ulongArrayOf(189041424779232614U, 1430782222387740366U)
        )
//        divisionSingleTest(ulongArrayOf(3449361588UL,1278830002UL,3123489057UL,3720277819UL, 1UL, 1UL, 1UL, 1UL), ulongArrayOf(1UL, 1UL))

    }

    fun divisionSingleTest(dividend: ULongArray, divisor: ULongArray) {
        assertTrue(
            "Failed on ulongArrayOf(${dividend.joinToString(separator = ",") { it.toString() + "U" }}), " +
                    "ulongArrayOf(${divisor.joinToString(separator = ",") { it.toString() + "U" }})"
        ) {
            val a = dividend
            val b = divisor
            try {
                val c = BigInteger63Arithmetic.divide(a, b)

                val bi64quotient = c.first.toJavaBigInteger()
                val bi64remainder = c.second.toJavaBigInteger()

                val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
                val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

                bi64quotient == bigIntQuotient && bi64remainder == bigIntRemainder
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }


        }
    }

    @Test
    fun testReciprocal() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1
            )
            val job = GlobalScope.launch {
                reciprocalSingleTest(a)
            }
            jobList.add(job)

        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun testReciprocalPrecise() {
//        val a = ulongArrayOf(1288756325682545368UL, 8091178732961339830UL, 8060639783838683711UL, 8865155242765229713UL)
//        val a = ulongArrayOf(1044716880932840986UL, 4262802357929821493UL, 8033697874689306672UL, 1362612340666419151UL)
//        val a = ulongArrayOf(12997UL)
        val a = ulongArrayOf(6486747088144942085UL, 1710444079094491755UL)
        reciprocalSingleTest(a)
    }

    fun reciprocalSingleTest(operand: ULongArray) {
        assertTrue("Failed on ulongArrayOf(${operand.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val a = operand
            val shift = if (a.size == 1) {
                1
            } else {
                a.size - 1
            }
            val recWithRem = try {
                BigInteger63Arithmetic.reciprocal(a)
            } catch (exception: Exception) {
                exception.printStackTrace()
                throw RuntimeException(
                    "Failed on ulongArrayOf(${operand.joinToString(separator = ", ") { it.toString() + "UL" }}) + ${exception.message}"
                )
            }
            val aRec = recWithRem.first

            val aRecPlusOne = BigInteger63Arithmetic.add(aRec, BigInteger63Arithmetic.ONE)
            val rawResult = BigInteger63Arithmetic.multiply(a, aRec)
            val rawResultWithRecPlusOne = BigInteger63Arithmetic.multiply(a, aRecPlusOne)
            val result =
                BigInteger63Arithmetic.shiftRight(rawResult, shift * 2 * 63)
            val resultWithRecPlusOne = BigInteger63Arithmetic.shiftRight(rawResultWithRecPlusOne, shift * 2 * 63)
            result.contentEquals(BigInteger63Arithmetic.ZERO) && resultWithRecPlusOne.contentEquals(
                BigInteger63Arithmetic.ONE
            )


        }
    }


    @Test
    fun `Test four words divided by two words using reciprocal division`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1
            )
            val b = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            val job = GlobalScope.launch {
                reciprocalDivisionSingleTest(a, b)
            }
            jobList.add(job)

        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }


    @Test
    fun testReciprocalDivision() {
//        val a = ulongArrayOf(0UL, 0UL, 0UL, 0UL, 4UL)
//        val b = ulongArrayOf(0UL, 0UL, 0UL, 2UL)
//        val a = ulongArrayOf(0UL, 0UL, 0UL, 4UL, 0UL, 4UL)
//        val b = ulongArrayOf(0UL, 0UL, 2UL, 0UL, 2UL)
//        val a = ulongArrayOf(0UL, 0UL, 0UL, 4UL, 0UL, 4UL)
//        val b = ulongArrayOf(2UL, 1UL, 2UL)
        val a = ulongArrayOf(35526194523336114UL, 5792101143026746304UL, 5736945918018393883UL, 8794898263700565859UL)
        val b = ulongArrayOf(6486747088144942085UL, 1710444079094491755UL)
        reciprocalDivisionSingleTest(a, b)
    }

    fun reciprocalDivisionSingleTest(first: ULongArray, second: ULongArray) {
        assertTrue(
            "Failed on \nval a = ulongArrayOf(${first.joinToString(separator = ", ") { it.toString() + "UL" }})\n" +
                    "val b = ulongArrayOf(${second.joinToString(separator = ", ") { it.toString() + "UL" }})"
        ) {
            val result = BigInteger63Arithmetic.reciprocalDivision(first, second)
            val normalResult = BigInteger63Arithmetic.divide(first, second)
            result.first.contentEquals(normalResult.first) && result.second.contentEquals(normalResult.second)
        }
    }

}

class DivisionBenchmark {

    data class BenchmarkSample(val dividend : ULongArray, val divisor : ULongArray, val expectedQuotient : ULongArray, val expectedRemainder : ULongArray)

    @Test
    fun runReciprocalVsBaseCaseBenchmark() {
        runBlocking {
            delay(10000)
        }
        val seed = 1
        val random = Random(seed)
        val sampleList = mutableListOf<BenchmarkSample>()

        val jobList: MutableList<Job> = mutableListOf()
        val generationStartTime = System.currentTimeMillis()
        for (i in 0..1000 step 1) {
            val job = GlobalScope.launch {
                println("Doing $i")
                val dividend = ULongArray(10) {
                    random.nextULong() shr 1
                }
                val divisor = ULongArray(9) {
                    random.nextULong() shr 1
                }
                val expectedQuotient = dividend.toJavaBigInteger() / divisor.toJavaBigInteger()
                val expectedRemainder = dividend.toJavaBigInteger() % divisor.toJavaBigInteger()
                sampleList.add(BenchmarkSample(
                    dividend,
                    divisor,
                    BigInteger63Arithmetic.parseForBase(expectedQuotient.toString(10), 10),
                    BigInteger63Arithmetic.parseForBase(expectedRemainder.toString(10), 10)))
                println("Done $i")
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
        val generationEndTime = System.currentTimeMillis()
        println("Done generating samples, took ${generationEndTime - generationStartTime} ms. Generated samples ${sampleList.size}")
        runBaseCaseOnSampleList(sampleList)
        runReciprocalOnSampleList(sampleList)
        runBaseCaseOnSampleList(sampleList)
        runReciprocalOnSampleList(sampleList)
        runBaseCaseOnSampleList(sampleList)
        runReciprocalOnSampleList(sampleList)
        runBaseCaseOnSampleList(sampleList)
        runReciprocalOnSampleList(sampleList)
        runBaseCaseOnSampleList(sampleList)
        runReciprocalOnSampleList(sampleList)
        1 == 1



    }

    fun runReciprocalOnSampleList(sampleList : List<BenchmarkSample>) {
        val reciprocalStartTime = System.currentTimeMillis()
        sampleList.forEach {
            divideUsingReciprocal(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
        }
        val reciprocalEndTime = System.currentTimeMillis()
        println("Done reciprocal divide in ${reciprocalEndTime - reciprocalStartTime}")
    }

    fun runBaseCaseOnSampleList(sampleList: List<BenchmarkSample>) {
        val baseCaseStartTime = System.currentTimeMillis()
        sampleList.forEach {
            divideUsingBaseDivide(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
        }
        val baseCaseEndTime = System.currentTimeMillis()
        println("Done basecase divide in ${baseCaseEndTime - baseCaseStartTime}")
    }



    fun divideUsingReciprocal(dividend : ULongArray, divisor : ULongArray, expectedQuotient : ULongArray, expectedRemainder : ULongArray) {
        val result = BigInteger63Arithmetic.reciprocalDivision(dividend, divisor)
        assertTrue {
            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }

    fun divideUsingBaseDivide(dividend : ULongArray, divisor : ULongArray, expectedQuotient : ULongArray, expectedRemainder : ULongArray) {
        val result = BigInteger63Arithmetic.divide(dividend, divisor)
        assertTrue {
            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }

}

