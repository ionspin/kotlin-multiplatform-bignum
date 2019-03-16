package com.ionspin.kotlin.biginteger.base63

import com.ionspin.kotlin.biginteger.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.biginteger.base32.toJavaBigInteger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger63DivisionTest {

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
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
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
    fun randomDivisionMultiWordTest() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
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

    }

    @Test
    fun randomDivisionMultiWordTest2() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 99) {
            if ((i % 100000) in 1..100) {
                println(i)
            }
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
                val d = BigInteger32Arithmetic.divide(
                    BigInteger63Arithmetic.convertTo32BitRepresentation(a),
                    BigInteger63Arithmetic.convertTo32BitRepresentation(b)
                    )

                val bi64quotient = c.first.toJavaBigInteger()
                val bi64remainder = c.second.toJavaBigInteger()

                val bigIntQuotient = a.toJavaBigInteger() / b.toJavaBigInteger()
                val bigIntRemainder = a.toJavaBigInteger() % b.toJavaBigInteger()

                val bi32quotient = d.first.toJavaBigInteger()
                val bi32remainder = d.second.toJavaBigInteger()

//                println("Dividend: ${BigInteger63Arithmetic.toString(a, 10)}")
//                println("Divisor: ${BigInteger63Arithmetic.toString(b, 10)}")
//
//                println("Dividend BigInt representation: ${a.toJavaBigInteger().toString(10)}")
//                println("Divisor BigInt representation: ${b.toJavaBigInteger().toString(10)}")
//
//                println("bi64quotient    : ${bi64quotient.toString(2)}")
//                println("bi64remainder   : ${bi64remainder.toString(2)}")
//                println("bigIntQuotient  : ${bigIntQuotient.toString(2)}")
//                println("bigIntRemainder : ${bigIntRemainder.toString(2)}")
//                println("bi32quotient    : ${bi32quotient.toString(2)}")
//                println("bi32remainder   : ${bi32remainder.toString(2)}")
//                println("bi64quotient    : ${bi64quotient.toString()}")
//                println("bi64remainder   : ${bi64remainder.toString()}")
//                println("bigIntQuotient  : ${bigIntQuotient.toString()}")
//                println("bigIntRemainder : ${bigIntRemainder.toString()}")
//                println("bi32quotient    : ${bi32quotient.toString()}")
//                println("bi32remainder   : ${bi32remainder.toString()}")


                bi64quotient == bigIntQuotient && bi64remainder == bigIntRemainder
            } catch (e : Throwable) {
                e.printStackTrace()
                false
            }






        }
    }

}