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

@file:Suppress("SimplifyBooleanWithConstants")

package com.ionspin.kotlin.bignum.integer.base32

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */

class BigInteger32JavaDivisionTest {

    @Test
    fun testDivision() {
        assertTrue {
            val a = uintArrayOf(40U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
        }

        assertTrue {
            val a = uintArrayOf(20U, 20U)
            val b = uintArrayOf(10U, 10U)
            val c = BigInteger32Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
        }
    }

    @Test
    fun `Test division with only one word`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {
            val a = random.nextUInt()
            val b = random.nextUInt()
            jobList.add(
                GlobalScope.launch {
                    if (a > b) {
                        divisionSingleTest(uintArrayOf(a), uintArrayOf(b))
                    } else {
                        divisionSingleTest(uintArrayOf(b), uintArrayOf(a))
                    }
                }
            )
        }

        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun `Test should throw arithmetic exception because of division with zero`() {
        val dividend = uintArrayOf(1U)
        val divisor = uintArrayOf(0U)

        assertFailsWith<ArithmeticException> {
            BigInteger32Arithmetic.divide(dividend, divisor)
        }
    }

    @Test
    fun `Test division by one`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 10001) {
            val a = random.nextUInt()
            val b = 1U
            divisionSingleTest(uintArrayOf(a), uintArrayOf(b))
        }
    }

    @Test
    fun `Test division with of two words with two words`() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {

            val a = uintArrayOf(random.nextUInt(), random.nextUInt())
            val b = uintArrayOf(random.nextUInt(), random.nextUInt())
            jobList.add(
                GlobalScope.launch {
                    if (BigInteger32Arithmetic.compare(a, b) > 0) {
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
    fun `Test division of 4 word dividend with 2 word divisor`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {
            val a = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt())
            val b = uintArrayOf(random.nextUInt(), random.nextUInt())
            jobList.add(
                GlobalScope.launch {
                    divisionSingleTest(a, b)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun `Test division with a large number of words in divisor`() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 10..5000 step 19) {
            val a = UIntArray(i) { random.nextUInt() }
            var randomDivisorSize = random.nextInt(i - 1)
            if (randomDivisorSize == 0) {
                randomDivisorSize = 1
            }
            val b = UIntArray(randomDivisorSize) { random.nextUInt() }
            jobList.add(
                GlobalScope.launch {
                    divisionSingleTest(a, b)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    // Can't remember why I created this one
    @Test
    fun `Test divison of 12 word dividend by four word divisor`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 5001) {

            val a = uintArrayOf(
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt(),
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt(),
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt()
            )
            val b = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt())
            jobList.add(
                GlobalScope.launch {
                    divisionSingleTest(a, b)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    fun divisionSingleTest(dividend: UIntArray, divisor: UIntArray) {
        assertTrue(
            "Failed on uintArrayOf(${dividend.joinToString(separator = ",") { it.toString() + "U" }}), " +
                "uintArrayOf(${divisor.joinToString(separator = ",") { it.toString() + "U" }})"
        ) {
            val a = dividend
            val b = divisor
            try {
                val c = BigInteger32Arithmetic.basicDivide(a, b)

                val quotientBigInt = c.first.toJavaBigInteger()
                val remainderBigInt = c.second.toJavaBigInteger()

                val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
                val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

                quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }
    }

    @Test
    fun reciprocalTest() {
        val a = uintArrayOf(100u)
        val reciprocal = BigInteger32Arithmetic.reciprocalSingleWord(100u)
        println("Rec ${reciprocal.first} * 2 ^ - ${reciprocal.second}")
        val reconstructed = BigInteger32Arithmetic.shiftRight(
            BigInteger32Arithmetic.multiply(a, reciprocal.first),
            reciprocal.second
        )
        println("Reconstructed: ${reconstructed.contentToString()}")
    }

    @Ignore("Obsolete  reciprocal method")
    @Test
    fun debugSingleReciprocalOneWordTest() {
        singleReciprocalOneWordTest(1074310744U)
    }

    @Ignore("Obsolete reciprocal method")
    @Test
    fun reciprocalOneWordRandomTest() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 10001) {
            val word = random.nextUInt()
            jobList.add(
                GlobalScope.launch {
                    singleReciprocalOneWordTest(word)
                }
            )
        }
        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    fun singleReciprocalOneWordTest(word: UInt) {
        assertTrue("Failed on $word") {
            val reciprocal = BigInteger32Arithmetic.reciprocalSingleWord(word)
            val resultJavaBigDecimal = reciprocal.first.toJavaBigInteger().toBigDecimal()
            val wordJavaBigDecimal = uintArrayOf(word).toJavaBigInteger().toBigDecimal()
            val multiplication = (resultJavaBigDecimal * wordJavaBigDecimal)
            val result = multiplication / 2.toBigInteger().pow(reciprocal.second).toBigDecimal()
            val rounded = result.round(MathContext(2, RoundingMode.HALF_EVEN))
            rounded.compareTo(BigDecimal.valueOf(1.0)) == 0
        }
    }

    @Test
    fun testRandomReciprocalDivisionWithOneWord() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {
            val word = random.nextUInt()
            val dividend = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt())
            jobList.add(
                GlobalScope.launch {
                    testSingleReciprocalDivisonWithOneWord(dividend, word)
                }
            )
        }
        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    @Test
    fun singleReciprocalDivisionTestOneWord() {
        val dividend = uintArrayOf(3504818262U, 3616430151U, 2530148830U)
        val divisor = (3959362403U)
        testSingleReciprocalDivisonWithOneWord(dividend, divisor)
    }

    fun testSingleReciprocalDivisonWithOneWord(dividend: UIntArray, divisor: UInt) {

        val arrayDivisor = uintArrayOf(divisor)
        val javaDividend = dividend.toJavaBigInteger()
        val javaDivisor = arrayDivisor.toJavaBigInteger()
        val expectedQuotient = javaDividend / javaDivisor
        val expectedRemainder = javaDividend.rem(javaDivisor)
        val (resultQuotient, resultRemainder) = BigInteger32Arithmetic.reciprocalDivision(dividend, arrayDivisor)
        val javaResultQuotient = resultQuotient.toJavaBigInteger()
        val javaResultRemainder = resultRemainder.toJavaBigInteger()
        assertTrue(
            "Failed on \nval dividend = uintArrayOf(${dividend.joinToString(separator = ", ") { "${it}U" }})" +
                "\nval divisor = ${divisor}U\n"
        ) {
            javaResultQuotient.compareTo(expectedQuotient) == 0 &&
                javaResultRemainder.compareTo(expectedRemainder) == 0
        }
    }

    @Test
    fun singleReciprocalDivisionTest() {
//        val dividend = uintArrayOf(3504818262U, 3616430151U, 2530148830U)
//        val divisor = (3959362403U)
        val dividend = uintArrayOf(
            3449361588U,
            1756843090U,
            1453467775U,
            4067428664U,
            3191437204U,
            2658797275U,
            4118577093U,
            1510408169U,
            1655916993U,
            393078469U
        )
        val divisor = uintArrayOf(
            2509423399U,
            2721619526U,
            1561595252U,
            3307230229U,
            3373635748U,
            761261538U,
            3754926477U,
            2390345401U,
            616031684U
        )
//        val dividend = uintArrayOf(2412223697U, 4003211491U, 2443024406U, 2156654946U, 3316815183U, 3124443256U, 48471415U, 3368762895U, 1329509906U, 2006838763U)
//        val divisor = uintArrayOf(3868219553U, 1852467980U, 291647052U, 1400631124U, 969215731U, 750739415U, 7935153U, 2131261330U, 553254777U)
        testSingleReciprocalDivison(dividend, divisor)
    }

    fun testSingleReciprocalDivison(dividend: UIntArray, divisor: UIntArray) {

        val javaDividend = dividend.toJavaBigInteger()
        val javaDivisor = divisor.toJavaBigInteger()
        val expectedQuotient = javaDividend / javaDivisor
        val expectedRemainder = javaDividend.rem(javaDivisor)
        val (resultQuotient, resultRemainder) = BigInteger32Arithmetic.reciprocalDivision(dividend, divisor)
        val javaResultQuotient = resultQuotient.toJavaBigInteger()
        val javaResultRemainder = resultRemainder.toJavaBigInteger()
        assertTrue(
            "Failed on \nval dividend = uintArrayOf(${dividend.joinToString(separator = ", ") { "${it}U" }})" +
                "\nval divisor = ${divisor}U\n"
        ) {
            javaResultQuotient.compareTo(expectedQuotient) == 0 &&
                javaResultRemainder.compareTo(expectedRemainder) == 0
        }
    }
}


class DivisionBenchmark {

    data class BenchmarkSample32Bit(
        val dividend: UIntArray,
        val divisor: UIntArray,
        val expectedQuotient: UIntArray,
        val expectedRemainder: UIntArray
    )

    @Ignore("This should be run only when necessary")
    @Test
    fun runReciprocalVsBaseCaseBenchmark() {
        runBlocking {
            delay(10000)
        }
        val seed = 1
        val random = Random(seed)
        val sampleList = mutableListOf<BenchmarkSample32Bit>()

        val jobList: MutableList<Job> = mutableListOf()
        val generationStartTime = System.currentTimeMillis()
        for (i in 0..1000 step 1) {
            val job = GlobalScope.launch {
                println("Doing $i")
                val dividend = UIntArray(10) {
                    random.nextUInt()
                }
                val divisor = UIntArray(9) {
                    random.nextUInt()
                }
                val expectedQuotient = dividend.toJavaBigInteger() / divisor.toJavaBigInteger()
                val expectedRemainder = dividend.toJavaBigInteger() % divisor.toJavaBigInteger()
                sampleList.add(
                    BenchmarkSample32Bit(
                        dividend,
                        divisor,
                        BigInteger32Arithmetic.parseForBase(expectedQuotient.toString(10), 10),
                        BigInteger32Arithmetic.parseForBase(expectedRemainder.toString(10), 10)
                    )
                )
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

    fun runReciprocalOnSampleList(sampleList: List<BenchmarkSample32Bit>) {
        val reciprocalStartTime = System.currentTimeMillis()
        sampleList.forEach {
            divideUsingReciprocal(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
        }
        val reciprocalEndTime = System.currentTimeMillis()
        println("Done reciprocal divide in ${reciprocalEndTime - reciprocalStartTime}")
    }

    fun runBaseCaseOnSampleList(sampleList: List<BenchmarkSample32Bit>) {
        val baseCaseStartTime = System.currentTimeMillis()
        sampleList.forEach {
            divideUsingBaseDivide(it.dividend, it.divisor, it.expectedQuotient, it.expectedRemainder)
        }
        val baseCaseEndTime = System.currentTimeMillis()
        println("Done basecase divide in ${baseCaseEndTime - baseCaseStartTime}")
    }

    fun divideUsingReciprocal(
        dividend: UIntArray,
        divisor: UIntArray,
        expectedQuotient: UIntArray,
        expectedRemainder: UIntArray
    ) {
        val result = BigInteger32Arithmetic.reciprocalDivision(dividend, divisor)
        assertTrue("Failed on: \n val dividend = uintArrayOf(${dividend.joinToString(separator = ", ") { "${it}U" }})\n" +
            "val divisor = uintArrayOf(${divisor.joinToString(separator = ", ") { "${it}U" }})"
        ) {

            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }

    fun divideUsingBaseDivide(
        dividend: UIntArray,
        divisor: UIntArray,
        expectedQuotient: UIntArray,
        expectedRemainder: UIntArray
    ) {
        val result = BigInteger32Arithmetic.divide(dividend, divisor)
        assertTrue {
            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }
}
