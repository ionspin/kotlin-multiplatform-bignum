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
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Apr-2019
 */

class BigInteger63ListGcdTest() {

    @Test
    fun testGcd() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..1000) {

            val length = random.nextInt(2, 100)
            val a = List<ULong>(length) {
                random.nextULong() shr 1
            }
            val divisorLength = random.nextInt(1, length)
            val b = List<ULong>(divisorLength) {
                random.nextULong() shr 1
            }
            val job = GlobalScope.launch {
                try {
                    testGcdSingle(a, b)
                } catch (exception: Exception) {
                    println("Failed on $length $divisorLength")
                }
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }
        }
    }

    fun testGcdSingle(first: List<ULong>, second: List<ULong>) {
        val a = BigInteger63LinkedListArithmetic.gcd(first, second)
        val aJavaBigInt = first.toJavaBigInteger().gcd(second.toJavaBigInteger())
        assertTrue {
            a.toJavaBigInteger().compareTo(aJavaBigInt) == 0
        }
    }
}
