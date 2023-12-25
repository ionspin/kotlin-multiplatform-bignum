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

package com.ionspin.kotlin.bignum.integer.benchmark

import com.ionspin.kotlin.bignum.integer.WordArray
import com.ionspin.kotlin.bignum.integer.base63.toJavaBigInteger
import com.ionspin.kotlin.bignum.integer.chosenArithmetic
import com.ionspin.kotlin.bignum.toProperType
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Oct-2019
 */

@ExperimentalTime
@Ignore // Benchmarks should only be ran manually
class JavaMultiplicationBenchmark {

    @BeforeTest
    fun setup() {
        // println("Word array type: ${TypeHelper.instance::class.simpleName}")
    }

    @Test
    fun benchmarkSmallOperandsSuite() {
        val randomRandom = Random.Default
        println("Stable seed")
        for (i in 1 until 10) {
            benchmarkSmallOperands(i)
        }
        println("Random seeds")
        for (i in 1 until 10) {
            val seed = randomRandom.nextInt()
            println("Seed $seed")
            benchmarkSmallOperands(seed)
        }
    }

    fun benchmarkSmallOperands(seed: Int) = runTest {
        val random = Random(seed)
        var timeSpent = 0L
        var javaTimeSpent = 0L
        val numberOfSamples = 100_000L
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(1) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(1) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
                javaTimeSpent += runJavaMultipltication(a, b)
            }
        }
        jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")

        println("Java Total ${javaTimeSpent / 1_000_000} ms (${javaTimeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Java Average run ${javaTimeSpent / numberOfSamples} NanoSeconds ${javaTimeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")
    }

    @Test
    fun benchmarkRangeOfOperandsSuite() {
        val randomRandom = Random.Default
        println("Stable seed")
        for (i in 1 until 10) {
            benchmarkRangeOfOperands(i)
        }
        println("Random seeds")
        for (i in 1 until 10) {
            val seed = randomRandom.nextInt()
            println("Seed $seed")
            benchmarkRangeOfOperands(seed)
        }
    }

    fun benchmarkRangeOfOperands(seed: Int) = runTest {
        val random = Random(seed)
        var timeSpent = 0L
        var javaTimeSpent = 0L
        val numberOfSamples = 1_000
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(i) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(i) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
                javaTimeSpent += runJavaMultipltication(a, b)
            }
        }
        jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")

        println("Java Total ${javaTimeSpent / 1_000_000} ms (${javaTimeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Java Average run ${javaTimeSpent / numberOfSamples} NanoSeconds ${javaTimeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")
    }

    @Test
    fun benchmarkLargeOperandsSuite() {
        val randomRandom = Random.Default
        println("Stable seed")
        for (i in 1 until 10) {
            benchmarkLargeOperands(i)
        }
        println("Random seeds")
        for (i in 1 until 10) {
            val seed = randomRandom.nextInt()
            println("Seed $seed")
            benchmarkLargeOperands(seed)
        }
    }

    fun benchmarkLargeOperands(seed: Int) = runTest {
        val random = Random(seed)
        var timeSpent = 0L
        var javaTimeSpent = 0L
        val numberOfSamples = 100
        val operandSize = 5_000
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(operandSize) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(operandSize) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
                javaTimeSpent += runJavaMultipltication(a, b)
            }
        }
        jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")

        println("Java Total ${javaTimeSpent / 1_000_000} ms (${javaTimeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Java Average run ${javaTimeSpent / numberOfSamples} NanoSeconds ${javaTimeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")
    }

    fun runMultiplication(first: WordArray, second: WordArray): Long {
        var result: WordArray
        return measureTime {
            result = chosenArithmetic.multiply(first, second)
        }.toLong(DurationUnit.NANOSECONDS)
        System.out.println("Result: ${chosenArithmetic.toString(result, 10)}")
    }

    fun runJavaMultipltication(first: WordArray, second: WordArray): Long {
        val firstJavaBigInt = first.toJavaBigInteger()
        val secondJavaBigInt = second.toJavaBigInteger()
        var javaResult: BigInteger
        return measureTime {
            javaResult = firstJavaBigInt.multiply(secondJavaBigInt)
        }.toLong(DurationUnit.NANOSECONDS)
        println("JavaResult ${javaResult.toString(10)}")
    }

    @Test
    fun runSingleMultiplication() {
        val random = Random(1)
        val a = ULongArray(10_000) { random.nextULong() shr 1 }.toProperType()
        val b = ULongArray(10_000) { random.nextULong() shr 1 }.toProperType()
        // val timeSpent = runMultiplication(a, b)
        val javaTimeSpent = runJavaMultipltication(a, b)
    }
}
