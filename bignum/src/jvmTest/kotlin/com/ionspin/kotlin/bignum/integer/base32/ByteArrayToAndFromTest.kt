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

package com.ionspin.kotlin.bignum.integer.base32

import com.ionspin.kotlin.bignum.integer.Sign
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 28-Jul-2019
 */
@ExperimentalUnsignedTypes
class ByteArrayToAndFromTest {

    @Test
    fun testToByteArray() {
        val bigInt32Array = BigInteger32Arithmetic.fromInt(170)
        val javaBigInt = BigInteger.valueOf(170)
        val javaBigIntByteArray = javaBigInt.toByteArray()
        val intArray = BigInteger32Arithmetic.toTwosComplementBigEndianByteArray(bigInt32Array)
        assertTrue {
            javaBigIntByteArray.contentEquals(intArray.toByteArray())
        }
//        testSingleToByteArray(BigInteger32Arithmetic.fromInt(170))
    }

    @Test
    fun testFromByteArray() {
        val bigInt32Array = BigInteger32Arithmetic.fromInt(-170)
        val javaBigInt = BigInteger.valueOf(-170)
        val javaBigIntByteArray = javaBigInt.toByteArray()
        val reconstructedBigInt32ArrayAndSign =
            BigInteger32Arithmetic.fromTwosComplementBigEndianByteArray(javaBigIntByteArray.toTypedArray())
        assertTrue {
            BigInteger32Arithmetic.compare(
                bigInt32Array,
                reconstructedBigInt32ArrayAndSign.first
            ) == 0 && reconstructedBigInt32ArrayAndSign.second == Sign.NEGATIVE
        }
    }


    @Test
    fun testManyFromByteArrays() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..10 step 1) {
            val job = GlobalScope.launch {
                val number = UIntArray(10) {
                    random.nextUInt()
                }
                testSingleFromByteArray(number)
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach { it.join() }
        }


    }

    @Test
    fun testManyToByteArrays() {
        val seed = 1
        val random = Random(seed)

        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..10 step 1) {
            val job = GlobalScope.launch {
                val number = UIntArray(10) {
                    random.nextUInt()
                }
                testSingleToByteArray(number)
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach { it.join() }
        }


    }

    @Test
    fun testSpecificNumberToByteArray() {
        val number = uintArrayOf(1382576883U, 3185699695U, 3990229257U, 4214840377U, 1857151281U, 1056377018U, 1097481378U, 2989689242U, 528133458U, 1391926091U)
        testSingleToByteArray(number)
    }

    fun testSingleFromByteArray(bigInt32Array: UIntArray) {
        val javaBigInt = bigInt32Array.toJavaBigInteger()
        val javaBigIntByteArray = javaBigInt.toByteArray()
        val reconstructedBigInt32ArrayAndSign =
            BigInteger32Arithmetic.fromTwosComplementBigEndianByteArray(javaBigIntByteArray.toTypedArray())
        assertTrue("Failed on FROM byte array: \n val number = uintArrayOf(${bigInt32Array.joinToString(separator = ", ") { "${it}U" }})\n") {
            BigInteger32Arithmetic.compare(
                bigInt32Array,
                reconstructedBigInt32ArrayAndSign.first
            ) == 0 && reconstructedBigInt32ArrayAndSign.second == Sign.NEGATIVE
        }
    }

    fun testSingleToByteArray(bigInt32Array: UIntArray) {
        val javaBigInt = bigInt32Array.toJavaBigInteger()
        val javaBigIntByteArray = javaBigInt.toByteArray()
        val intArray = BigInteger32Arithmetic.toTwosComplementBigEndianByteArray(bigInt32Array)
        assertTrue("Failed on TO byte array: \n val number = uintArrayOf(${bigInt32Array.joinToString(separator = ", ") { "${it}U" }})\n") {
            javaBigIntByteArray.contentEquals(intArray.toByteArray())
        }
    }


}