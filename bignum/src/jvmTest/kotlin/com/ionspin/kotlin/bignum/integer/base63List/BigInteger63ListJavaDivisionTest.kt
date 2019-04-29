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
import kotlinx.coroutines.*
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63ListJavaDivisionTest () {

    @Test
    fun testDivision() {
        assertTrue {
            val a = listOf(40UL)
            val b = listOf(20UL)
            val c = BigInteger63LinkedListArithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder

        }

        assertTrue {
            val a = listOf(20UL, 20UL)
            val b = listOf(10UL, 10UL)
            val c = BigInteger63LinkedListArithmetic.divide(a, b)

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
                divisionSingleTest(listOf(a), listOf(b))
            } else {
                divisionSingleTest(listOf(b), listOf(a))
            }

        }

    }

    @Test
    fun `Test two word divided by two words`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
            val b = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
            jobList.add(
                GlobalScope.launch {
                    if (BigInteger63LinkedListArithmetic.compare(a, b) > 0) {
                        divisionSingleTest(a, b)
                    } else {
                        divisionSingleTest(b, a)
                    }
                }
            )


        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Test four words divided by two words`() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = listOf(
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1,
                random.nextULong() shr 1
            )
            val b = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
            jobList.add(
                GlobalScope.launch {
                    try {
                        if (BigInteger63LinkedListArithmetic.compare(a, b) > 0) {
                            divisionSingleTest(a, b)
                        } else {
                            divisionSingleTest(b, a)
                        }
                    } catch (e: Throwable) {
                        println(
                            "Failed on listOf(${a.joinToString(separator = ",") { it.toString() + "U" }}), " +
                                    "listOf(${b.joinToString(separator = ",") { it.toString() + "U" }})"
                        )
                        e.printStackTrace()
                    }
                }
            )

        }

    }


    @Test
    fun preciseDebugTest() {
        divisionSingleTest(
            listOf(
                7011262718134162982U,
                165064388400841479U,
                8071396034697521068U,
                3707335022938319120U
            ), listOf(189041424779232614U, 1430782222387740366U)
        )
//        divisionSingleTest(listOf(3449361588UL,1278830002UL,3123489057UL,3720277819UL, 1UL, 1UL, 1UL, 1UL), listOf(1UL, 1UL))

    }

    fun divisionSingleTest(dividend: List<ULong>, divisor: List<ULong>) {
        assertTrue(
            "Failed on listOf(${dividend.joinToString(separator = ",") { it.toString() + "U" }}), " +
                    "listOf(${divisor.joinToString(separator = ",") { it.toString() + "U" }})"
        ) {
            val a = dividend
            val b = divisor
            try {
                val c = BigInteger63LinkedListArithmetic.divide(a, b)

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

//    @Test
//    fun testReciprocal() {
//        val seed = 1
//        val random = Random(seed)
//
//        val jobList: MutableList<Job> = mutableListOf()
//        for (i in 1..Int.MAX_VALUE step 3001) {
//
//            val a = listOf(
//                random.nextULong() shr 1,
//                random.nextULong() shr 1,
//                random.nextULong() shr 1,
//                random.nextULong() shr 1
//            )
//            val job = GlobalScope.launch {
//                reciprocalSingleTest(a)
//            }
//            jobList.add(job)
//
//        }
//        runBlocking {
//            jobList.forEach { it.join() }
//        }
//    }

//    @Test
//    fun testReciprocalPrecise() {
////        val a = listOf(1288756325682545368UL, 8091178732961339830UL, 8060639783838683711UL, 8865155242765229713UL)
////        val a = listOf(1044716880932840986UL, 4262802357929821493UL, 8033697874689306672UL, 1362612340666419151UL)
////        val a = listOf(12997UL)
//        val a = listOf(6486747088144942085UL, 1710444079094491755UL)
//        reciprocalSingleTest(a)
//    }

//    fun reciprocalSingleTest(operand: List<ULong>) {
//        assertTrue("Failed on listOf(${operand.joinToString(separator = ", ") { it.toString() + "UL" }})") {
//            val a = operand
//            val shift = if (a.size == 1) {
//                1
//            } else {
//                a.size - 1
//            }
//            val recWithRem = try {
//                BigInteger63LinkedListArithmetic.reciprocal(a)
//            } catch (exception: Exception) {
//                exception.printStackTrace()
//                throw RuntimeException(
//                    "Failed on listOf(${operand.joinToString(separator = ", ") { it.toString() + "UL" }}) + ${exception.message}"
//                )
//            }
//            val aRec = recWithRem.first
//
//            val aRecPlusOne = BigInteger63LinkedListArithmetic.add(aRec, BigInteger63LinkedListArithmetic.ONE)
//            val rawResult = BigInteger63LinkedListArithmetic.multiply(a, aRec)
//            val rawResultWithRecPlusOne = BigInteger63LinkedListArithmetic.multiply(a, aRecPlusOne)
//            val result =
//                BigInteger63LinkedListArithmetic.shiftRight(rawResult, shift * 2 * 63)
//            val resultWithRecPlusOne = BigInteger63LinkedListArithmetic.shiftRight(rawResultWithRecPlusOne, shift * 2 * 63)
//            result.equals(BigInteger63LinkedListArithmetic.ZERO) && resultWithRecPlusOne.equals(
//                BigInteger63LinkedListArithmetic.ONE
//            )
//
//
//        }
//    }


//    @Test
//    fun `Test four words divided by two words using reciprocal division`() {
//        val seed = 1
//        val random = Random(seed)
//        val jobList: MutableList<Job> = mutableListOf()
//        for (i in 1..Int.MAX_VALUE step 3001) {
//
//            val a = listOf(
//                random.nextULong() shr 1,
//                random.nextULong() shr 1,
//                random.nextULong() shr 1,
//                random.nextULong() shr 1
//            )
//            val b = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
//            val job = GlobalScope.launch {
//                reciprocalDivisionSingleTest(a, b)
//            }
//            jobList.add(job)
//
//        }
//        runBlocking {
//            jobList.forEach { it.join() }
//        }
//
//    }

//    @Test
//    fun `Test random number of word divided using reciprocal division`() {
//        val seed = 1
//        val random = Random(seed)
//        val jobList: MutableList<Job> = mutableListOf()
//        for (i in 1..100) {
//
//            val length = random.nextInt(2, 5000)
//            val a = List<ULong>(length) {
//                random.nextULong() shr 1
//            }
//            val divisorLength = random.nextInt(1, length)
//            val b = List<ULong>(divisorLength) {
//                random.nextULong() shr 1
//            }
//            val job = GlobalScope.launch {
//                try {
//                    reciprocalDivisionSingleTest(a, b)
//                } catch (exception: Exception) {
//                    println("Failed on $length $divisorLength")
//                }
//            }
//            jobList.add(job)
//
//        }
//        runBlocking {
//            jobList.forEach { it.join() }
//        }
//
//    }


//    @Test
//    fun testReciprocalDivision() {
////        val a = listOf(0UL, 0UL, 0UL, 0UL, 4UL)
////        val b = listOf(0UL, 0UL, 0UL, 2UL)
////        val a = listOf(0UL, 0UL, 0UL, 4UL, 0UL, 4UL)
////        val b = listOf(0UL, 0UL, 2UL, 0UL, 2UL)
////        val a = listOf(0UL, 0UL, 0UL, 4UL, 0UL, 4UL)
////        val b = listOf(2UL, 1UL, 2UL)
////        val a = listOf(35526194523336114UL, 5792101143026746304UL, 5736945918018393883UL, 8794898263700565859UL)
////        val b = listOf(6486747088144942085UL, 1710444079094491755UL)
//        val seed = 1
//        val random = Random(1916)
//        val a = List<ULong>(1374) {
//            random.nextULong() shr 1
//        }
//        val b = List<ULong>(871) {
//            random.nextULong() shr 1
//        }
//
//
//
//        reciprocalDivisionSingleTest(a, b)
//    }

//    private fun reciprocalDivisionSingleTest(first: List<ULong>, second: List<ULong>) {
//        assertTrue(
//            "Failed on \nval a = listOf(${first.joinToString(separator = ", ") { it.toString() + "UL" }})\n" +
//                    "val b = listOf(${second.joinToString(separator = ", ") { it.toString() + "UL" }})"
//        ) {
//            val result = BigInteger63LinkedListArithmetic.reciprocalDivision(first, second)
//            val normalResult = BigInteger63LinkedListArithmetic.divide(first, second)
//            result.first.equals(normalResult.first) && result.second.equals(normalResult.second)
//        }
//    }

}

//@ExperimentalUnsignedTypes
//class ListDivisionBenchmark {
//
//    data class BenchmarkSample(
//        val dividend: List<ULong>,
//        val divisor: List<ULong>,
//        val expectedQuotient: List<ULong>,
//        val expectedRemainder: List<ULong>
//    )
//    @Suppress("UnusedEquals")
//    @Ignore("This should be run only when necessary")
//    @Test
//    fun runReciprocalVsBaseCaseBenchmark() {
//        runBlocking {
//            delay(10000)
//        }
//        val seed = 1
//        val random = Random(seed)
//        val sampleList = mutableListOf<BenchmarkSample>()
//
//        val jobList: MutableList<Job> = mutableListOf()
//        val generationStartTime = System.currentTimeMillis()
//        for (i in 0..1000 step 1) {
//            val job = GlobalScope.launch {
//                println("Doing $i")
//                val dividend = List<ULong>(10) {
//                    random.nextULong() shr 1
//                }
//                val divisor = List<ULong>(9) {
//                    random.nextULong() shr 1
//                }
//                val expectedQuotient = dividend.toJavaBigInteger() / divisor.toJavaBigInteger()
//                val expectedRemainder = dividend.toJavaBigInteger() % divisor.toJavaBigInteger()
//                sampleList.add(
//                    BenchmarkSample(
//                        dividend,
//                        divisor,
//                        BigInteger63LinkedListArithmetic.parseForBase(expectedQuotient.toString(10), 10),
//                        BigInteger63LinkedListArithmetic.parseForBase(expectedRemainder.toString(10), 10)
//                    )
//                )
//                println("Done $i")
//            }
//            jobList.add(job)
//        }
//        runBlocking {
//            jobList.forEach { it.join() }
//        }
//        val generationEndTime = System.currentTimeMillis()
//        println("Done generating samples, took ${generationEndTime - generationStartTime} ms. Generated samples ${sampleList.size}")
//        runBaseCaseOnSampleList(sampleList)
//        runReciprocalOnSampleList(sampleList)
//        runBaseCaseOnSampleList(sampleList)
//        runReciprocalOnSampleList(sampleList)
//        runBaseCaseOnSampleList(sampleList)
//        runReciprocalOnSampleList(sampleList)
//        runBaseCaseOnSampleList(sampleList)
//        runReciprocalOnSampleList(sampleList)
//        runBaseCaseOnSampleList(sampleList)
//        runReciprocalOnSampleList(sampleList)
//        1 == 1
//
//
//    }
//
//    fun runReciprocalOnSampleList(sampleList: List<BenchmarkSample>) {
//        val reciprocalStartTime = System.currentTimeMillis()
//        sampleList.forEach {
//            divideUsingReciprocal(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
//        }
//        val reciprocalEndTime = System.currentTimeMillis()
//        println("Done reciprocal divide in ${reciprocalEndTime - reciprocalStartTime}")
//    }
//
//    fun runBaseCaseOnSampleList(sampleList: List<BenchmarkSample>) {
//        val baseCaseStartTime = System.currentTimeMillis()
//        sampleList.forEach {
//            divideUsingBaseDivide(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
//        }
//        val baseCaseEndTime = System.currentTimeMillis()
//        println("Done basecase divide in ${baseCaseEndTime - baseCaseStartTime}")
//    }
//
//
//    fun divideUsingReciprocal(
//        dividend: List<ULong>,
//        divisor: List<ULong>,
//        expectedQuotient: List<ULong>,
//        expectedRemainder: List<ULong>
//    ) {
//        val result = BigInteger63LinkedListArithmetic.reciprocalDivision(dividend, divisor)
//        assertTrue {
//            result.first.equals(expectedQuotient) && result.second.equals(expectedRemainder)
//        }
//    }
//
//    fun divideUsingBaseDivide(
//        dividend: List<ULong>,
//        divisor: List<ULong>,
//        expectedQuotient: List<ULong>,
//        expectedRemainder: List<ULong>
//    ) {
//        val result = BigInteger63LinkedListArithmetic.divide(dividend, divisor)
//        assertTrue {
//            result.first.equals(expectedQuotient) && result.second.equals(expectedRemainder)
//        }
//    }
//
//}

