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
 * on 09-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaDivisionTest {

    @Test
    fun testDivision() {
        assertTrue {
            val a = ulongArrayOf(40U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder

        }

        assertTrue {
            val a = ulongArrayOf(20U, 20U)
            val b = ulongArrayOf(10U, 10U)
            val c = BigInteger63Arithmetic.divide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

            val bla = 1L
            bla.toBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
        }
    }

    @Test
    fun `Test division with only one word`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 3001) {
            val a = random.nextULong() shr 1
            val b = random.nextULong() shr 1
            if (a > b) {
                divisionSingleTest(ulongArrayOf(a), ulongArrayOf(b))
            } else {
                divisionSingleTest(ulongArrayOf(b), ulongArrayOf(a))
            }

        }

    }

    @Test
    fun `Test two word divided by two words`() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            val b = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            GlobalScope.launch {
                if (BigInteger63Arithmetic.compare(a, b) > 0) {
                    divisionSingleTest(a, b)
                } else {
                    divisionSingleTest(b, a)
                }
            }


        }
        runBlocking {
            jobList.forEach { it.join() }
        }

    }

    @Test
    fun `Test four words divided by two words`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 3001) {

            val a = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1, random.nextULong() shr 1, random.nextULong() shr 1)
            val b = ulongArrayOf(random.nextULong() shr 1, random.nextULong() shr 1)
            GlobalScope.launch {
                try {
                    if (BigInteger63Arithmetic.compare(a, b) > 0) {
                        divisionSingleTest(a, b)
                    } else {
                        divisionSingleTest(b, a)
                    }
                } catch (e : Throwable) {
                    println("Failed on ulongArrayOf(${a.joinToString(separator = ",") { it.toString() + "U" }}), " +
                            "ulongArrayOf(${b.joinToString(separator = ",") { it.toString() + "U" }})")
                    e.printStackTrace()
                }
            }

        }

    }



    @Test
    fun preciseDebugTest() {
        divisionSingleTest(ulongArrayOf(7011262718134162982U,165064388400841479U,8071396034697521068U,3707335022938319120U), ulongArrayOf(189041424779232614U,1430782222387740366U))
//        divisionSingleTest(ulongArrayOf(3449361588UL,1278830002UL,3123489057UL,3720277819UL, 1UL, 1UL, 1UL, 1UL), ulongArrayOf(1UL, 1UL))

    }

    fun divisionSingleTest(dividend : ULongArray, divisor : ULongArray) {
        assertTrue("Failed on ulongArrayOf(${dividend.joinToString(separator = ",") { it.toString() + "U" }}), " +
                "ulongArrayOf(${divisor.joinToString(separator = ",") { it.toString() + "U" }})") {
            val a = dividend
            val b = divisor
            try {
                val c = BigInteger63Arithmetic.divide(a, b)

                val bi64quotient = c.first.toJavaBigInteger()
                val bi64remainder = c.second.toJavaBigInteger()

                val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
                val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

                bi64quotient == bigIntQuotient && bi64remainder == bigIntRemainder
            } catch (e : Throwable) {
                e.printStackTrace()
                false
            }






        }
    }

}