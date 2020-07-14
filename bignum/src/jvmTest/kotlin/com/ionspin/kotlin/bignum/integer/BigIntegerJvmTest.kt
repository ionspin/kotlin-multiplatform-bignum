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

import com.ionspin.kotlin.bignum.integer.BigInteger.Companion.ZERO
import com.ionspin.kotlin.bignum.integer.base63.toJavaBigInteger
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import com.ionspin.kotlin.bignum.toProperType
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
 * on 22-Apr-2019
 */

class BigIntegerJvmTest {

    @Test
    fun testModInverse() {
        val a = BigInteger(11)
        val aInverse = a.modInverse(5.toBigInteger())
        val aJavaInverse = a.toJavaBigInteger().modInverse(java.math.BigInteger.valueOf(5))
        assertTrue {
            aInverse.toJavaBigInteger() == aJavaInverse
        }
    }

    // TODO need to implement better testcase, need better coprime generation
    @Test
    fun testRandomModInverse() {
        val seed = 1
        var random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..1000) {
            val length = random.nextInt(2, 50)
            val a = ULongArray(length) {
                random.nextULong() shr 1
            }

            val job = GlobalScope.launch {
                try {
                    val aBigInt = BigInteger(a.toProperType(), Sign.POSITIVE)
                    val aPrepared = if (aBigInt % 2 != ZERO) {
                        aBigInt
                    } else {
                        aBigInt - 1
                    }

                    val bPrepared = aPrepared - 1
                    testSingleModInverse(aPrepared, bPrepared)
                } catch (exception: Exception) {
                    println("Failed on $length")
                    exception.printStackTrace()
                }
            }
            jobList.add(job)
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    fun testSingleModInverse(bigInteger: BigInteger, modulo: BigInteger) {
        assertTrue {
            val inverse = bigInteger.modInverse(modulo)
            val javaInverse = bigInteger.toJavaBigInteger().modInverse(modulo.toJavaBigInteger())
            inverse.toJavaBigInteger().compareTo(javaInverse) == 0
        }
    }

    @Test
    fun testModPow() {
        val creator = ModularBigInteger.creatorForModulo(10)

        assertTrue {
            val a = creator.fromInt(-3)
            val aPow = a.pow(3)
            val javaBigIntPow = (-3).toBigInteger().toJavaBigInteger()
                .modPow(
                    3.toBigInteger().toJavaBigInteger(),
                    10.toBigInteger().toJavaBigInteger()
                )
            aPow.residue.toJavaBigInteger().compareTo(javaBigIntPow) == 0
        }

        assertTrue {
            val a = creator.fromInt(3)
            val aPow = a.pow(3)
            val javaBigIntPow = (3).toBigInteger().toJavaBigInteger()
                .modPow(
                    3.toBigInteger().toJavaBigInteger(),
                    10.toBigInteger().toJavaBigInteger()
                )
            aPow.residue.toJavaBigInteger().compareTo(javaBigIntPow) == 0
        }
    }
}
