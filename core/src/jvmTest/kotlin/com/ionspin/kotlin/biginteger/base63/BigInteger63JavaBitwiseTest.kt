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

package com.ionspin.kotlin.biginteger.base63

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaBitwiseTest {

    @Test
    fun shiftLeftTest() {
        for (i in 1 .. Int.MAX_VALUE step 10000001) {
            println("$i")
            for (j in 1 .. Int.MAX_VALUE step 100000000) {
                for (k in 1 .. Int.MAX_VALUE step 100000001) {
                    for (l in 1 .. Int.MAX_VALUE step 100000000) {
                        GlobalScope.launch {
                            shiftLeftSingleTest(i, j.toULong(),k.toULong(),l.toULong())
                        }

                    }
                }
            }
        }
        shiftLeftSingleTest(32, 1U)
        shiftLeftSingleTest(32, 2U)
        shiftLeftSingleTest(32, 0UL - 1UL)
        shiftLeftSingleTest(35, 0UL - 1UL)
        shiftLeftSingleTest(35, 0UL - 1UL, 0UL, 1UL)
        shiftLeftSingleTest(64, 0UL - 1UL)
        shiftLeftSingleTest(75, 0UL - 1UL)
        shiftLeftSingleTest(5, 0UL - 1UL)
        shiftLeftSingleTest(237, 0UL - 1UL)
    }

    fun shiftLeftSingleTest(places : Int, vararg ulongs : ULong) {
        assertTrue ("Failed for $places and elements ${ulongs.contentToString()}") {
            val a = ulongArrayOf(*ulongs)
            val result = BigInteger63Arithmetic.shiftLeft(a, places)
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
                            shiftLeftSingleTest(i, j.toULong(),k.toULong(),l.toULong())
                        }

                    }
                }
            }
        }

        shiftRightSingleTest(32, 1UL)
        shiftRightSingleTest(32, 2UL)
        shiftRightSingleTest(32, 0UL - 1UL)
        shiftRightSingleTest(35, 0UL - 1UL)
        shiftRightSingleTest(64, 0UL - 1UL)
        shiftRightSingleTest(75, 0UL - 1UL)
        shiftRightSingleTest(5, 0UL - 1UL)
        shiftRightSingleTest(237, 0UL - 1UL)
    }

    fun shiftRightSingleTest(places : Int, vararg ulongs : ULong) {
        assertTrue ("Failed for $places and elements ${ulongs.contentToString()}") {
            val a = ulongArrayOf(*ulongs)
            val result = BigInteger63Arithmetic.shiftRight(a, places)
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = a.toJavaBigInteger() shr places
            convertedResult == bigIntResult
        }
    }
}