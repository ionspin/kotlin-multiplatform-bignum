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
import java.math.MathContext
import kotlin.random.Random
import kotlin.test.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */

class BigDecimalJvmTest {

    val seed = 1
    val random = Random(seed)

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
        val jobList: MutableList<Job> = mutableListOf()

        for (i in 0..Int.MAX_VALUE step 100001) {
            jobList.add(singleCreationTestLong(random.nextLong(), random.nextLong(5000)))
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    fun singleCreationTestLong(long: Long, exponent: Long): Job {
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
            val javaBigFirst = java.math.BigDecimal.valueOf(125, -13) // -14

            val second = BigDecimal.fromLongWithExponent(71, -7) // -7
            val javaBigSecond = java.math.BigDecimal.valueOf(71, 8) // 9

            val result = first + second
            val javaBigResult = javaBigFirst + javaBigSecond

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }
    }

    @Test
    fun debugAdditionTest() {
        val first =
            BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("-71946752722652910", 10), 192)
        val second =
            BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("7834199654291277674", 10), 193)
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
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..Int.MAX_VALUE step 100_000) {
            val first = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            val second = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            jobList.add(singleAdditionTest(i, first, second))
            singleAdditionTest(i, first, second)
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun singleAdditionTest(i: Int, first: BigDecimal, second: BigDecimal): Job {
        return GlobalScope.launch {
            assertTrue(
                "Failed on \n" +
                    "val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${first.significand}\", 10), ${first.exponent}.toBigInteger())\n " +
                    "val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${second.significand}\", 10), ${second.exponent}.toBigInteger())"
            ) {
                val result = first + second
                val resultJavaBigInt = first.toJavaBigDecimal() + second.toJavaBigDecimal()
                val resultConverted = result.toJavaBigDecimal()
                resultConverted.compareTo(resultJavaBigInt) == 0
            }
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
    fun aLotOfSubtractionTests() {
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..Int.MAX_VALUE step 100_000) {
            val first = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            val second = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            jobList.add(singleSubtractionTest(i, first, second))
            singleAdditionTest(i, first, second)
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun singleSubtractionTest(i: Int, first: BigDecimal, second: BigDecimal): Job {
        return GlobalScope.launch {
            assertTrue(
                "Failed on \n" +
                    "val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${first.significand}\", 10), ${first.exponent}.toBigInteger())\n " +
                    "val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${second.significand}\", 10), ${second.exponent}.toBigInteger())"
            ) {
                val result = first - second
                val resultJavaBigInt = first.toJavaBigDecimal() - second.toJavaBigDecimal()
                val resultConverted = result.toJavaBigDecimal()
                resultConverted.compareTo(resultJavaBigInt) == 0
            }
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
    fun testDebugMultiplication() {
        val first =
            BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("-884653051988182590", 10), 124)
        val second =
            BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("686869704857289531", 10), 51)
        val result = first * second
        val firstJava = first.toJavaBigDecimal()
        val secondJava = second.toJavaBigDecimal()
        val resultJavaBigInt = firstJava * secondJava
        val resultConverted = result.toJavaBigDecimal()

        assertTrue {
            resultConverted.compareTo(resultJavaBigInt) == 0
        }
    }

    @Test
    fun aLotOfMultiplicationTests() {
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..Int.MAX_VALUE step 100_000) {
            val first = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            val second = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            jobList.add(singleMultiplicationTest(i, first, second))
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun singleMultiplicationTest(i: Int, first: BigDecimal, second: BigDecimal): Job {
        return GlobalScope.launch {
            assertTrue(
                "Failed on \n" +
                    "val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${first.significand}\", 10), ${first.exponent}.toBigInteger())\n " +
                    "val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${second.significand}\", 10), ${second.exponent}.toBigInteger())"
            ) {
                val result = first * second
                val resultJavaBigInt = first.toJavaBigDecimal() * second.toJavaBigDecimal()
                val resultConverted = result.toJavaBigDecimal()
                resultConverted.compareTo(resultJavaBigInt) == 0
            }
        }
    }

    @Test
    fun testDivision() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = first.toJavaBigDecimal()

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = second.toJavaBigDecimal()

            val result = first.divide(second, DecimalMode(99, RoundingMode.AWAY_FROM_ZERO))
            val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(99, java.math.RoundingMode.UP))

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(1L, 0)
            val javaBigFirst = first.toJavaBigDecimal()

            val second = BigDecimal.fromLongWithExponent(3L, 0)
            val javaBigSecond = second.toJavaBigDecimal()

            val result = first.divide(second, DecimalMode(99, RoundingMode.AWAY_FROM_ZERO))
            val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(99, java.math.RoundingMode.UP))

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(123L, 4)
            val javaBigFirst = first.toJavaBigDecimal()

            val second = BigDecimal.fromLongWithExponent(71, -2)
            val javaBigSecond = second.toJavaBigDecimal()

            val result = first.divide(second, DecimalMode(123, RoundingMode.AWAY_FROM_ZERO))
            val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(123, java.math.RoundingMode.UP))

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(1L, 0)
            val javaBigFirst = first.toJavaBigDecimal()

            val second = BigDecimal.fromLongWithExponent(3L, 0)
            val javaBigSecond = second.toJavaBigDecimal()

            val result = first.divide(second, DecimalMode(401, RoundingMode.AWAY_FROM_ZERO))
            val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(401, java.math.RoundingMode.UP))

            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }
    }

    @Test
    fun debugTestDivision() {
//        val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode("-884653051988182590", 10), 124.toBigInteger())
//        val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode("686869704857289531", 10), 51.toBigInteger())
//        val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode("-2573868278004278171", 10), 86.toBigInteger())
//        val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode("7343078399229486119", 10), 16.toBigInteger())
//        val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("-7823836971981477152", 10), 167.toBigInteger())
//        val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseString("-1241920988109618346", 10), 118.toBigInteger())
        val first = 4.123.toBigDecimal()
        val second = 2.0.toBigDecimal()

        val javaBigFirst = first.toJavaBigDecimal()

        val javaBigSecond = second.toJavaBigDecimal()

        val result = first.divide(second, DecimalMode(1, RoundingMode.FLOOR))
        val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(1, java.math.RoundingMode.FLOOR))
        assertTrue {
            result.toJavaBigDecimal().compareTo(javaBigResult) == 0
        }
    }

    @Test
    fun aLotOfDivisionTests() {
        val jobList: MutableList<Job> = mutableListOf()
        for (i in 0..Int.MAX_VALUE step 100_001) {
            val first = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            val second = BigDecimal.fromLongWithExponent(random.nextLong(), random.nextLong(200))
            jobList.add(singleDivisionTest(first, second))
//            singleDivisionTest(first, second)
        }

        runBlocking {
            jobList.forEach {
                it.join()
            }
        }
    }

    private fun singleDivisionTest(first: BigDecimal, second: BigDecimal): Job {
        return GlobalScope.launch {
            assertTrue(
                "Failed on \n" +
                    "val first = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${first.significand}\", 10), ${first.exponent}.toBigInteger())\n " +
                    "val second = BigDecimal.fromBigIntegerWithExponent(BigInteger.parseStringWithMode(\"${second.significand}\", 10), ${second.exponent}.toBigInteger())"
            ) {
                val javaBigFirst = first.toJavaBigDecimal()

                val javaBigSecond = second.toJavaBigDecimal()

                val result = first.divide(second, DecimalMode(401, RoundingMode.AWAY_FROM_ZERO))
                val javaBigResult = javaBigFirst.divide(javaBigSecond, MathContext(401, java.math.RoundingMode.UP))

                result.toJavaBigDecimal().compareTo(javaBigResult) == 0
            }
        }
    }
}
