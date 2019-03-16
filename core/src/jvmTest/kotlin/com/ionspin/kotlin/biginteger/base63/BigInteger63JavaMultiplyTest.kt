package com.ionspin.kotlin.biginteger.base63

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaMultiplyTest {
    @Test
    fun testMultiply() {
        assertTrue {
            val a = ulongArrayOf(10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = ulongArrayOf(10U, 10U)
            val b = ulongArrayOf(20U, 20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = ulongArrayOf((0UL - 1UL), 10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        for (i in 1..Int.MAX_VALUE step 10000001) {
            println("$i")
            for (j in 1..Int.MAX_VALUE step 100000000) {
                for (k in 1..Int.MAX_VALUE step 100000001) {
                    for (l in 1..Int.MAX_VALUE step 100000000) {
                        GlobalScope.launch {
                            multiplySingleTest(i.toULong(), j.toULong(), k.toULong(), l.toULong())
                        }
                    }
                }
            }
        }


    }

    @Test
    fun randomMultiplyTest() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
            multiplySingleTest(random.nextULong(), random.nextULong(), random.nextULong())
        }

    }

    @Test
    fun randomMultiplyLotsOfElementsTest() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 150000
        println("Number of elements $numberOfElements")

        val lotOfElements = ULongArray(numberOfElements) {
            random.nextULong()
        }
        multiplySingleTest(*lotOfElements)
    }

    @Test
    fun preciseMultiplyTest() {
        multiplySingleTest(ulongArrayOf(3751237528UL, 9223372035661198284UL, 7440555637UL, 0UL, 2UL, 0UL, 2UL), ulongArrayOf(1UL, 1UL))
    }

    fun multiplySingleTest(first : ULongArray, second : ULongArray) {
        assertTrue("Failed on ${first.contentToString()} ${second.contentToString()}") {

            val result = BigInteger63Arithmetic.multiply(first, second)


            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
            bigIntResult == convertedResult
        }
    }

    fun multiplySingleTest(vararg elements: ULong) {
        assertTrue("Failed on ${elements.contentToString()}") {
            val time = elements.size > 100
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = elements.foldIndexed(ULongArray(1) { 1U }) { index, acc, uLong ->
                BigInteger63Arithmetic.multiply(acc, uLong)
            }
            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = elements.foldIndexed(BigInteger.ONE) { index, acc, uInt ->
                acc * BigInteger(uInt.toString(), 10)
            }
            if (time) {
                println("Result ${convertedResult}")
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
            }

            bigIntResult == convertedResult
        }
    }
}