package com.ionspin.kotlin.biginteger.base32

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaBitwiseTest {

    @Test
    fun shiftLeftTest() {
        for (i in 1 .. Int.MAX_VALUE step 10000001) {
            println("$i")
            for (j in 1 .. Int.MAX_VALUE step 100000000) {
                for (k in 1 .. Int.MAX_VALUE step 100000001) {
                    for (l in 1 .. Int.MAX_VALUE step 100000000) {
                        GlobalScope.launch {
                            shiftLeftSingleTest(i, j.toUInt(),k.toUInt(),l.toUInt())
                        }

                    }
                }
            }
        }
        shiftLeftSingleTest(32, 1U)
        shiftLeftSingleTest(32, 2U)
        shiftLeftSingleTest(32, 0U - 1U)
        shiftLeftSingleTest(35, 0U - 1U)
        shiftLeftSingleTest(35, 0U - 1U, 0U, 1U)
        shiftLeftSingleTest(64, 0U - 1U)
        shiftLeftSingleTest(75, 0U - 1U)
        shiftLeftSingleTest(5, 0U - 1U)
        shiftLeftSingleTest(237, 0U - 1U)
    }

    fun shiftLeftSingleTest(places : Int, vararg uints : UInt) {
        assertTrue ("Failed for $places and elements ${uints.contentToString()}") {
            val a = uintArrayOf(*uints)
            val result = BigInteger32Arithmetic.shiftLeft(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = a.toJavaBigInteger() shl places
            convertedResult == bigIntResult
        }
    }

    @Test
    fun shiftRightSingleTest() {
        for (i in 1 .. Int.MAX_VALUE step 10000001) {
            println("$i")
            for (j in 1 .. Int.MAX_VALUE step 100000000) {
                for (k in 1 .. Int.MAX_VALUE step 100000001) {
                    for (l in 1 .. Int.MAX_VALUE step 100000000) {
                        GlobalScope.launch {
                            shiftLeftSingleTest(i, j.toUInt(),k.toUInt(),l.toUInt())
                        }

                    }
                }
            }
        }

        shiftRightSingleTest(32, 1U)
        shiftRightSingleTest(32, 2U)
        shiftRightSingleTest(32, 0U - 1U)
        shiftRightSingleTest(35, 0U - 1U)
        shiftRightSingleTest(64, 0U - 1U)
        shiftRightSingleTest(75, 0U - 1U)
        shiftRightSingleTest(5, 0U - 1U)
        shiftRightSingleTest(237, 0U - 1U)
    }

    @Test
    fun debugTest() {
        shiftRightSingleTest(5, 4294967295U)
    }

    fun shiftRightSingleTest(places : Int, vararg uints : UInt) {
        assertTrue ("Failed for $places and elements ${uints.contentToString()}") {
            val a = uintArrayOf(*uints)
            val result = BigInteger32Arithmetic.shiftRight(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = a.toJavaBigInteger() shr places
            convertedResult == bigIntResult
        }
    }
}