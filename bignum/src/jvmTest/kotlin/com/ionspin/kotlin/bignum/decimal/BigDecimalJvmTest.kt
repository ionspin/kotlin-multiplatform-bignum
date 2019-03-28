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

package com.ionspin.kotlin.bignum.decimal

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlinx.coroutines.*
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalJvmTest {

    val seed = 1
    val random = Random(seed)

    @Test
    fun whatIsScale() {
        var a = java.math.BigDecimal.valueOf(1, 0)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, 1)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, 2)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, 3)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, -1)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, -2)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(1, -3)
        println("A: $a")



        println ("-----------")

        a = java.math.BigDecimal.valueOf(12345678, 0)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, 1)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, 2)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, 3)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, -1)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, -2)
        println("A: $a")
        a = java.math.BigDecimal.valueOf(12345678, -3)
        println("A: $a")
    }


    @Test
    fun testCreation() {
        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(123L, 5)
            val javaBigDecimal = java.math.BigDecimal.valueOf(123, -3)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigDecimal = java.math.BigDecimal.valueOf(123, -2)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigDecimal = java.math.BigDecimal.valueOf(71, 3)

            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0 // <- Ignores java bigint scale difference
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(125L, -7)
            val javaBigDecimal = java.math.BigDecimal.valueOf(125, 9)
            val compar = bigDecimal.toJavaBigDecimal()
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, 15)
            val javaBigDecimal = java.math.BigDecimal.valueOf(71, -14)
            bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
        }

    }

    @Test
    fun testALotOfCreations() {
        val jobList : MutableList<Job> = mutableListOf()

        for (i in 0 .. Int.MAX_VALUE step 100001) {
            jobList.add(singleCreationTestLong(random.nextLong(), random.nextInt(5000)))
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    fun singleCreationTestLong(long : Long, exponent : Int) : Job {
        return GlobalScope.launch {
            assertTrue("Failed on $long $exponent") {
                val bigDecimal = BigDecimal.fromLongWithExponent(long, exponent)
                val javaBigDecimal = bigDecimal.toJavaBigDecimal()
                bigDecimal.toJavaBigDecimal().compareTo(javaBigDecimal) == 0
            }
        }

    }

    @Test
    fun testAddition() {

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, 2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, -1)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(7111, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(7111, 5)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125L, -7)
            val javaBigFirst = java.math.BigDecimal.valueOf(125, 9)

            val second = BigDecimal.fromLongWithExponent(71, 15)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, -14)

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125L, 15) // 15
            val javaBigFirst = java.math.BigDecimal.valueOf(125, -13 ) // -14

            val second = BigDecimal.fromLongWithExponent(71, -7) // -7
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 8) // 9

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0


        }
    }

    @Test
    fun debugAdditionTest() {
        val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("2577512651365090736", 10), 152.toBigInteger())
        val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("-6866244039176730022", 10), 7.toBigInteger())
        val result = first + second
        val firstJava = first.toJavaBigDecimal()
        val secondJava = second.toJavaBigDecimal()
        val resultJavaBigInt = firstJava + secondJava
        assertTrue {
            result.toJavaBigDecimal().compareTo(resultJavaBigInt) == 0
        }
    }

    @Test
    fun aLotOfAdditionTests() {
        val jobList : MutableList<Job> = mutableListOf()
        for (i in 0 .. Int.MAX_VALUE step 1_000_000) {
            val first = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextInt(200))
            val second = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextInt(200))
//            jobList.add(singleAdditionTest(i, first, second))
            singleAdditionTest(i, first, second)
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }


    fun singleAdditionTest(i : Int, first : BigDecimal, second : BigDecimal)  {

            assertTrue("Failed on \n" +
                    "val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString(\"${first.significand}\", 10), ${first.exponent}.toBigInteger())\n " +
                    "val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString(\"${second.significand}\", 10), ${second.exponent}.toBigInteger())") {
                println("Doing $i $first $second")
                val result = first + second
                val resultJavaBigInt = first.toJavaBigDecimal() + second.toJavaBigDecimal()
                val resultConverted = result.toJavaBigDecimal()
                println("Done conversion")
                val bool = resultConverted.compareTo(resultJavaBigInt) == 0
                println("Done $i $first $second $result")
                bool
        }
    }

    @Test
    fun testSubtraction() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first - second
            val javaBigResult = javaBigFirst - javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testMultiplication() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first * second
            val javaBigResult = javaBigFirst * javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }

    @Test
    fun testDivision() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = java.math.BigDecimal.valueOf(123, -2)

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 3)

            val result = first / second
            val javaBigResult = javaBigFirst / javaBigSecond

            result.toJavaBigDecimal() == javaBigResult


        }
    }
}