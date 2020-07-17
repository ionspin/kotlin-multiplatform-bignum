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

package com.ionspin.kotlin.bignum.integer

import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Apr-2019
 */
@Ignore // TODO refactor and move to benchmarks

class ProfilerRunner {
    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun main(args: List<String>) {
            ProfilerRunner().runReciprocalVsBaseCaseBenchmark()
        }
    }

    data class BenchmarkSample(
        val dividend: ULongArray,
        val divisor: ULongArray,
        val expectedQuotient: ULongArray,
        val expectedRemainder: ULongArray
    )

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
                val dividend = ULongArray(200) {
                    random.nextULong() shr 1
                }
                val divisor = ULongArray(180) {
                    random.nextULong() shr 1
                }
                val expectedQuotient = dividend.toJavaBigInteger() / divisor.toJavaBigInteger()
                val expectedRemainder = dividend.toJavaBigInteger() % divisor.toJavaBigInteger()
                sampleList.add(
                    BenchmarkSample(
                        dividend,
                        divisor,
                        BigInteger63Arithmetic.parseForBase(expectedQuotient.toString(10), 10),
                        BigInteger63Arithmetic.parseForBase(expectedRemainder.toString(10), 10)
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

    fun runReciprocalOnSampleList(sampleList: List<BenchmarkSample>) {
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

    fun divideUsingReciprocal(
        dividend: ULongArray,
        divisor: ULongArray,
        expectedQuotient: ULongArray,
        expectedRemainder: ULongArray
    ) {
        val result = BigInteger63Arithmetic.reciprocalDivision(dividend, divisor)
        assertTrue {
            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }

    fun divideUsingBaseDivide(
        dividend: ULongArray,
        divisor: ULongArray,
        expectedQuotient: ULongArray,
        expectedRemainder: ULongArray
    ) {
        val result = BigInteger63Arithmetic.divide(dividend, divisor)
        assertTrue {
            result.first.contentEquals(expectedQuotient) && result.second.contentEquals(expectedRemainder)
        }
    }

    private fun ULongArray.toJavaBigInteger(): BigInteger {
        return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
            acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 63))
        }
    }
}
