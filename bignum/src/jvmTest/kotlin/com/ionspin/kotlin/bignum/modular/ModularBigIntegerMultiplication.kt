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

package com.ionspin.kotlin.bignum.modular

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.base63.toJavaBigInteger
import com.ionspin.kotlin.bignum.toProperType
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Apr-2019
 */

class ModularBigIntegerMultiplication {

    @Test
    fun testRandomModularMultiplication() {
        val seed = 1
        var random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..1000) {
            val a = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()
            val b = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()

            val modulo = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()
            val creator = ModularBigInteger.creatorForModulo(BigInteger(modulo, Sign.POSITIVE))

            val job = GlobalScope.launch {
                try {
                    val aMod = creator.fromBigInteger(BigInteger(a, Sign.POSITIVE))
                    val bMod = creator.fromBigInteger(BigInteger(b, Sign.POSITIVE))
                    singleMultiplicationTest(aMod, bMod)
                } catch (exception: Exception) {
                    exception.printStackTrace()
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

    @Test
    fun testRandomNegativeModularMultiplication() {
        val seed = 1
        var random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..1000) {
            val a = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()
            val b = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()

            val modulo = ULongArray(random.nextInt(1, 500)) {
                random.nextULong() shr 1
            }.toProperType()
            val creator = ModularBigInteger.creatorForModulo(BigInteger(modulo, Sign.POSITIVE))

            val job = GlobalScope.launch {
                try {
                    val aMod = creator.fromBigInteger(BigInteger(a, Sign.POSITIVE))
                    val bMod = creator.fromBigInteger(BigInteger(b, Sign.NEGATIVE))
                    singleMultiplicationTest(aMod, bMod)
                } catch (exception: Exception) {
                    exception.printStackTrace()
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

    fun singleMultiplicationTest(a: ModularBigInteger, b: ModularBigInteger) {
        assertTrue {
            val result = a * b
            val javaResult =
                (a.residue.toJavaBigInteger() * b.residue.toJavaBigInteger()).mod(a.modulus.toJavaBigInteger())
            result.residue.toJavaBigInteger() == javaResult
        }
    }
}
