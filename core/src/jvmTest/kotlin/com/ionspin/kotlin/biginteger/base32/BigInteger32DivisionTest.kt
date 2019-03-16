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

package com.ionspin.kotlin.biginteger.base32

import com.ionspin.kotlin.biginteger.base63.toJavaBigInteger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32DivisionTest {

    @Test
    fun testDivision() {
        assertTrue {
            val a = uintArrayOf(40U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.basicDivide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder

        }

        assertTrue {
            val a = uintArrayOf(20U, 20U)
            val b = uintArrayOf(10U, 10U)
            val c = BigInteger32Arithmetic.basicDivide(a, b)

            val quotientBigInt = c.first.toJavaBigInteger()
            val remainderBigInt = c.second.toJavaBigInteger()

            val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
            val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()


            quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
        }
    }

    @Test
    fun `Test division with only one word`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = random.nextUInt()
            val b = random.nextUInt()
            if (a > b) {
                divisionSingleTest(uintArrayOf(a), uintArrayOf(b))
            } else {
                divisionSingleTest(uintArrayOf(b), uintArrayOf(a))
            }

        }

    }

    @Test
    fun `Test division with zero`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = random.nextUInt()
            val b = 0U
            if (a > b) {
                divisionSingleTest(uintArrayOf(a), uintArrayOf(b))
            } else {
                divisionSingleTest(uintArrayOf(b), uintArrayOf(a))
            }

        }

    }

    @Test
    fun `Test division by one`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = random.nextUInt()
            val b = 1U
            if (a > b) {
                divisionSingleTest(uintArrayOf(a), uintArrayOf(b))
            } else {
                divisionSingleTest(uintArrayOf(b), uintArrayOf(a))
            }

        }

    }

    @Test
    fun randomDivisionMultiWordTest() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = uintArrayOf(random.nextUInt(), random.nextUInt())
            val b = uintArrayOf(random.nextUInt(), random.nextUInt())
            GlobalScope.launch {
                if (BigInteger32Arithmetic.compare(a, b) > 0) {
                    divisionSingleTest(a, b)
                } else {
                    divisionSingleTest(b, a)
                }
            }


        }

    }

    @Test
    fun randomDivisionMultiWordTest2() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt())
            val b = uintArrayOf(random.nextUInt(), random.nextUInt())
            GlobalScope.launch {
                if (BigInteger32Arithmetic.compare(a, b) > 0) {
                    divisionSingleTest(a, b)
                } else {
                    divisionSingleTest(b, a)
                }
            }

        }

    }

    @Test
    fun randomDivisionMultiWordTest4() {
        val seed = 1
        val random = Random(seed)


        var jobList : List<Job> = emptyList()
        for (i in 5_000 downTo 10 step 3) {
            val a = UIntArray(i) { random.nextUInt() }
            var randomDivisorSize = random.nextInt(i - 1)
            if (randomDivisorSize == 0) {
                randomDivisorSize = 1
            }
            val b = UIntArray(randomDivisorSize) { random.nextUInt() }
            jobList += GlobalScope.launch {
                println("Division: $i words / $randomDivisorSize words")
                    divisionSingleTest(a, b)
            }
        }
        runBlocking {
            jobList.forEach { it.join() }
        }




    }


    @Test
    fun randomDivisionMultiWordTest3() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            val a = uintArrayOf(
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt(),
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt(),
                random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt()
            )
            val b = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt())
            GlobalScope.launch {
                if (BigInteger32Arithmetic.compare(a, b) > 0) {
                    divisionSingleTest(a, b)
                } else {
                    divisionSingleTest(b, a)
                }
            }

        }

    }

//    @Test
//    fun randomDivisionLongWordTest2() {
//        val seed = 1
//        val random = Random(seed)
//        println("Preparing dividend")
//        generateSequence {  }
//        for (i in 1..Int.MAX_VALUE step 99) {
//            if ((i % 100000) in 1..100) {
//                println(i)
//            }
//            val a = uintArrayOf(random.nextUInt(), random.nextUInt(), random.nextUInt(), random.nextUInt())
//            val b = uintArrayOf(random.nextUInt(), random.nextUInt())
//
//
//        }
//
//    }

    @Test
    fun preciseDebugTest() {

        divisionSingleTest(uintArrayOf(3449361588U,1278830002U,3123489057U,3720277819U), uintArrayOf(486484208U,2780187700U))
    }

    fun divisionSingleTest(dividend : UIntArray, divisor : UIntArray) {
        assertTrue("Failed on uintArrayOf(${dividend.joinToString(separator = ",") { it.toString() + "U" }}), " +
                "uintArrayOf(${divisor.joinToString(separator = ",") { it.toString() + "U" }})") {
            val a = dividend
            val b = divisor
            try {
                val c = BigInteger32Arithmetic.basicDivide(a, b)

                val quotientBigInt = c.first.toJavaBigInteger()
                val remainderBigInt = c.second.toJavaBigInteger()

                val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
                val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

                quotientBigInt == bigIntQuotient && remainderBigInt == bigIntRemainder
            } catch (e : Throwable) {
                e.printStackTrace()
                false
            }






        }
    }
}