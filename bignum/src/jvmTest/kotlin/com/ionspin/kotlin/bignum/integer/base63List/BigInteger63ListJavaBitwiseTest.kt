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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63ListJavaBitwiseTest () {


    @Test
    fun `Random shift left test`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {
            jobList.add(
                GlobalScope.launch {
                    var a = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
                    a = BigInteger63LinkedListArithmetic.multiply(a, a)

                    shiftLeftSingleTest(random.nextInt(BigInteger63LinkedListArithmetic.bitLength(a)), a)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun `Test specific combinations for left shift`() {
        shiftLeftSingleTest(32, listOf(1U))
        shiftLeftSingleTest(32, listOf(2U))
        shiftLeftSingleTest(32, listOf(0UL - 1UL))
        shiftLeftSingleTest(35, listOf(0UL - 1UL))
        shiftLeftSingleTest(35, listOf(0UL - 1UL, 0UL, 1UL))
        shiftLeftSingleTest(64, listOf(0UL - 1UL))
        shiftLeftSingleTest(75, listOf(0UL - 1UL))
        shiftLeftSingleTest(5, listOf(0UL - 1UL))
        shiftLeftSingleTest(237, listOf(0UL - 1UL))
        shiftLeftSingleTest(215, listOf(1U))

    }

    fun shiftLeftSingleTest(places: Int, ulongs: List<ULong>) {
        assertTrue("Failed for $places and elements listOf(${ulongs.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val a = ulongs
            val result = BigInteger63LinkedListArithmetic.shiftLeft(a, places)
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
                    var a = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
                    a = BigInteger63LinkedListArithmetic.multiply(a, a)

                    shiftRightSingleTest(random.nextInt(BigInteger63LinkedListArithmetic.bitLength(a)), a)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun `Test specific combinations for right shift`() {
        shiftRightSingleTest(32, listOf(1UL))
        shiftRightSingleTest(32, listOf(2UL))
        shiftRightSingleTest(32, listOf(0UL - 1UL))
        shiftRightSingleTest(35, listOf(0UL - 1UL))
        shiftRightSingleTest(64, listOf(0UL - 1UL))
        shiftRightSingleTest(5, listOf(0UL - 1UL))
        shiftRightSingleTest(237, listOf(0UL - 1UL))
        shiftRightSingleTest(
            126,
            listOf(5724129373318154496UL, 4479429175062385556UL, 7319678748417918140UL, 201305160793401908UL)
        )
        shiftRightSingleTest(
            122,
            listOf(5724129373318154496UL, 4479429175062385556UL, 7319678748417918140UL, 201305160793401908UL)
        )
        shiftRightSingleTest(90, BigInteger63LinkedListArithmetic.parseForBase("100000000000000000000000000000000", 10))
    }

    fun shiftRightSingleTest(places: Int, ulongs: List<ULong>) {
        assertTrue("Failed for $places and elements listOf(${ulongs.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val a = ulongs
            val aBigInt = a.toJavaBigInteger()
            val result = BigInteger63LinkedListArithmetic.shiftRight(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = aBigInt shr places
            convertedResult == bigIntResult
        }
    }

    @Test
    fun `Test specific xor`() {
        val operand = BigInteger63LinkedListArithmetic.parseForBase("11110000", 2)
        val mask = BigInteger63LinkedListArithmetic.parseForBase("00111100", 2)
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
                    var a = listOf(random.nextULong() shr 1, random.nextULong() shr 1)
                    a = BigInteger63LinkedListArithmetic.multiply(a, a)

                    shiftRightSingleTest(random.nextInt(BigInteger63LinkedListArithmetic.bitLength(a)), a)
                }
            )

        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }


    fun singleXorTest(operand: List<ULong>, mask: List<ULong>) {
        val result = BigInteger63LinkedListArithmetic.xor(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().xor(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }


    @Test
    fun `Test specific or`() {
        val operand = BigInteger63LinkedListArithmetic.parseForBase("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger63LinkedListArithmetic.parseForBase("00000000FFFF0000000000", 16)
        singleOrTest(operand, mask)


    }

    fun singleOrTest(operand: List<ULong>, mask: List<ULong>) {
        val result = BigInteger63LinkedListArithmetic.or(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().or(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }


    @Test
    fun `Test specific and`() {
        val operand = BigInteger63LinkedListArithmetic.parseForBase("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger63LinkedListArithmetic.parseForBase("00000000FFFF0000000000", 16)
        singleAndTest(operand, mask)


    }

    fun singleAndTest(operand: List<ULong>, mask: List<ULong>) {
        val result = BigInteger63LinkedListArithmetic.and(operand, mask)
        val bigIntResult = operand.toJavaBigInteger().and(mask.toJavaBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }

    @Ignore
    @Test
    fun `Test specific inv`() {
        val operand = BigInteger63LinkedListArithmetic.parseForBase("1100", 2)
        singleInvTest(operand)

    }

    //Hmmm this is not behaving as I would expect it to on java side
    fun singleInvTest(operand: List<ULong>) {
        val result = BigInteger63LinkedListArithmetic.not(operand)
        val bigIntResult = operand.toJavaBigInteger()
            .xor(1.toBigInteger().shr(operand.toJavaBigInteger().bitLength()) - 1.toBigInteger())

        assertTrue { result.toJavaBigInteger() == bigIntResult }
    }
}