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

import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

class BigInteger63JavaBitwiseTest {

    @Test
    fun `Random shift left test`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {
            jobList.add(
                GlobalScope.launch {
                    var a = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
                    a = BigInteger63Arithmetic.multiply(a, a)

                    shiftLeftSingleTest(random.nextInt(BigInteger63Arithmetic.bitLength(a)), a)
                }
            )
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

    @Test
    fun `Test specific combinations for left shift`() {
        shiftLeftSingleTest(32, ulongArrayOf(1U))
        shiftLeftSingleTest(32, ulongArrayOf(2U))
        shiftLeftSingleTest(32, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(35, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(35, ulongArrayOf(0UL - 1UL, 0UL, 1UL))
        shiftLeftSingleTest(64, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(75, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(5, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(237, ulongArrayOf(0UL - 1UL))
        shiftLeftSingleTest(215, ulongArrayOf(1U))
    }

    fun shiftLeftSingleTest(places: Int, ulongs: ULongArray) {
        assertTrue("Failed for $places and elements ulongArrayOf(${ulongs.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val a = ulongs
            val result = BigInteger63Arithmetic.shiftLeft(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = a.toJavaBigInteger() shl places
            convertedResult == bigIntResult
        }
    }

    @Test
    fun `Random shift right test`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 1..Int.MAX_VALUE step 3001) {
            jobList.add(
                GlobalScope.launch {
                    var a = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
                    a = BigInteger63Arithmetic.multiply(a, a)

                    shiftRightSingleTest(random.nextInt(BigInteger63Arithmetic.bitLength(a)), a)
                }
            )
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

    @Test
    fun `Test specific combinations for right shift`() {
        shiftRightSingleTest(32, ulongArrayOf(1UL))
        shiftRightSingleTest(32, ulongArrayOf(2UL))
        shiftRightSingleTest(32, ulongArrayOf(0UL - 1UL))
        shiftRightSingleTest(35, ulongArrayOf(0UL - 1UL))
        shiftRightSingleTest(64, ulongArrayOf(0UL - 1UL))
        shiftRightSingleTest(5, ulongArrayOf(0UL - 1UL))
        shiftRightSingleTest(237, ulongArrayOf(0UL - 1UL))
        shiftRightSingleTest(
            126,
            ulongArrayOf(5724129373318154496UL, 4479429175062385556UL, 7319678748417918140UL, 201305160793401908UL)
        )
        shiftRightSingleTest(
            122,
            ulongArrayOf(5724129373318154496UL, 4479429175062385556UL, 7319678748417918140UL, 201305160793401908UL)
        )
        shiftRightSingleTest(90, BigInteger63Arithmetic.parseForBase("100000000000000000000000000000000", 10))
    }

    fun shiftRightSingleTest(places: Int, ulongs: ULongArray) {
        assertTrue("Failed for $places and elements ulongArrayOf(${ulongs.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val a = ulongs
            val aBigInt = a.toJavaBigInteger()
            val result = BigInteger63Arithmetic.shiftRight(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = aBigInt shr places
            convertedResult == bigIntResult
        }
    }

    @Test
    fun `Test specific xor`() {
        val operand = BigInteger63Arithmetic.parseForBase("11110000", 2)
        val mask = BigInteger63Arithmetic.parseForBase("00111100", 2)
        singleXorTest(operand, mask)
    }

    @Test
    fun `Test random xor`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {
            jobList.add(
                GlobalScope.launch {
                    val a = ULongArray(2) { random.nextULong() shr 1 }
                    val b = ULongArray(1) { random.nextULong() shr 1 }
                    singleXorTest(a, b)
                }
            )
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

    fun singleXorTest(operand: ULongArray, mask: ULongArray) {
        val result = BigInteger63Arithmetic.xor(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().xor(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }

    @Test
    fun `Test specific or`() {
        val operand = BigInteger63Arithmetic.parseForBase("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger63Arithmetic.parseForBase("00000000FFFF0000000000", 16)
        singleOrTest(operand, mask)
    }

    fun singleOrTest(operand: ULongArray, mask: ULongArray) {
        val result = BigInteger63Arithmetic.or(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().or(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }

    @Test
    fun `Test specific and`() {
        val operand = BigInteger63Arithmetic.parseForBase("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger63Arithmetic.parseForBase("00000000FFFF0000000000", 16)
        singleAndTest(operand, mask)
    }

    fun singleAndTest(operand: ULongArray, mask: ULongArray) {
        val result = BigInteger63Arithmetic.and(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().and(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }

    @Ignore
    @Test
    fun `Test specific inv`() {
        val operand = BigInteger63Arithmetic.parseForBase("1100", 2)
        singleInvTest(operand)
    }

    // Hmmm this is not behaving as I would expect it to on java side
    fun singleInvTest(operand: ULongArray) {
        val result = BigInteger63Arithmetic.not(operand)
        val bigIntResult = operand.toJavaBigInteger()
            .xor(1.toBigInteger().shr(operand.toJavaBigInteger().bitLength()) - 1.toBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }
}
