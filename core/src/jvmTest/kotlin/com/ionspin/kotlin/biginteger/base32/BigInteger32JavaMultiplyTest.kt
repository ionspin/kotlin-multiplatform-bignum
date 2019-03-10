package com.ionspin.kotlin.biginteger.base32

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaMultiplyTest {
    @Test
    fun testMultiply() {
        assertTrue {
            val a = uintArrayOf(10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf(10U, 10U)
            val b = uintArrayOf(20U, 20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult

        }

        assertTrue {
            val a = uintArrayOf((0U - 1U), 10U)
            val b = uintArrayOf(20U)
            val c = BigInteger32Arithmetic.multiply(a, b)

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
                            multiplySingleTest(i.toUInt(), j.toUInt(), k.toUInt(), l.toUInt())
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
            multiplySingleTest(random.nextUInt(), random.nextUInt(), random.nextUInt())
        }

    }

    @Test
    fun randomMultiplyLotsOfElementsTest() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 150000
        println("Number of elements $numberOfElements")

        val lotOfElements = UIntArray(numberOfElements) {
            random.nextUInt()
        }
        multiplySingleTest(*lotOfElements)
    }

    fun multiplySingleTest(vararg elements: UInt) {
        assertTrue("Failed on ${elements.contentToString()}") {
            val time = elements.size > 100
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = elements.foldIndexed(UIntArray(1) { 1U }) { index, acc, uInt ->
                BigInteger32Arithmetic.multiply(acc, uInt)
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