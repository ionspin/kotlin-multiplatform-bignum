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
import com.ionspin.kotlin.bignum.integer.chosenArithmetic
import com.ionspin.kotlin.bignum.runBlockingTest
import com.ionspin.kotlin.bignum.toProperType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Oct-2019
 */
@ExperimentalUnsignedTypes
@ExperimentalTime
@Ignore // Benchmarks should only be ran manually
class MultiplicationBenchmark {

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

    fun benchmarkSmallOperands(seed: Int) = runBlockingTest {
        val random = Random(seed)
        var timeSpent = 0L
        val numberOfSamples = 100_000L
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(1) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(1) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
            }
        }
        jobList.forEach { it.join() }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000} MilliSeconds")
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

    fun benchmarkRangeOfOperands(seed: Int) = runBlockingTest {
        val random = Random(seed)
        var timeSpent = 0L
        val numberOfSamples = 1_000
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(i) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(i) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
            }
        }
        jobList.forEach { it.join() }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000} MilliSeconds")
    }

    @Test
    fun benchmarkLargeOperandsSuite() {
        val randomRandom = Random.Default
        println("Stable seed")
        for (i in 1 until 3) {
            benchmarkLargeOperands(i)
        }
        println("Random seeds")
        for (i in 1 until 2) {
            val seed = randomRandom.nextInt()
            println("Seed $seed")
            benchmarkLargeOperands(seed)
        }
    }

    fun benchmarkLargeOperands(seed: Int) = runBlockingTest {
        val random = Random(seed)
        var timeSpent = 0L
        val numberOfSamples = 100
        val operandSize = 16_000
        val jobList = mutableListOf<Job>()
        for (i in 0 until numberOfSamples) {
            jobList += GlobalScope.launch {
                val a = ULongArray(operandSize) { random.nextULong() shr 1 }.toProperType()
                val b = ULongArray(operandSize) { random.nextULong() shr 1 }.toProperType()
                timeSpent += runMultiplication(a, b)
            }
        }
        jobList.forEach { it.join() }

        println("Total ${timeSpent / 1_000_000} ms (${timeSpent / 1_000_000_000} s) over $numberOfSamples")
        println("Average run ${timeSpent / numberOfSamples} NanoSeconds ${timeSpent / numberOfSamples / 1_000_000_000} MilliSeconds")
    }

    @Test
    fun specificBenchmark() {
        val random = Random(1)
        val a = ULongArray(16_000) { random.nextULong() shr 1 }.toProperType()
        val b = ULongArray(16_000) { random.nextULong() shr 1 }.toProperType()
        runMultiplication(a, b)
    }

    fun runMultiplication(first: WordArray, second: WordArray): Long {
        return measureTime {
            chosenArithmetic.multiply(first, second)
        }.toLong(DurationUnit.NANOSECONDS)
    }
}