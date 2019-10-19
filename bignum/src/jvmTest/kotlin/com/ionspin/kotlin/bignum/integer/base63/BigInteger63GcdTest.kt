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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Apr-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63GcdTest {

    @Test
    fun testGcd() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 1..100) {

            val length = random.nextInt(2, 100)
            val a = ULongArray(length) {
                random.nextULong() shr 1
            }
            val divisorLength = random.nextInt(1, length)
            val b = ULongArray(divisorLength) {
                random.nextULong() shr 1
            }

            val job = GlobalScope.launch {
                testGcdSingle(a, b)
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun debugBinaryGcd() {
        val first = ulongArrayOf(4U)
        val second = ulongArrayOf(6U)
        testGcdSingle(first, second)
    }

    fun testGcdSingle(first: ULongArray, second: ULongArray) {
        val a = BigInteger63Arithmetic.gcd(first, second)
        val aJavaBigInt = first.toJavaBigInteger().gcd(second.toJavaBigInteger())
        assertTrue("Failed on ${first.toJavaBigInteger()} ${second.toJavaBigInteger()}") {
            a.toJavaBigInteger().compareTo(aJavaBigInt) == 0
        }
    }
}