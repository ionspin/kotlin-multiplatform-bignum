package com.ionspin.kotlin.biginteger.base32

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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