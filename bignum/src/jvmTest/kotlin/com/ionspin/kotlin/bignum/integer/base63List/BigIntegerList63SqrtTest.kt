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

import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Apr-2019
 */
@ExperimentalUnsignedTypes
class BigIntegerList63SqrtTest {


    @Test
    fun testSpecificSqrt() {
        assertTrue {
            val seed = 1
            val random = Random(seed)
            val operand = ULongArray(9) { random.nextULong() shr 1 }
            val a = BigInteger63Arithmetic.sqrt(operand)
            println("Operand: ${BigInteger63Arithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger63Arithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger63Arithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger63Arithmetic.parseForBase(
                "7397044194494028975732055495441088126843300784390793504636322150624655374658058636588",
                10
            )
            val resultRem = BigInteger63Arithmetic.substract(
                operand,
                BigInteger63Arithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.contentEquals(a.first)
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val operand = BigInteger63Arithmetic.parseForBase("123456789", 10)
            val a = BigInteger63Arithmetic.sqrt(operand)
            println("Operand: ${BigInteger63Arithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger63Arithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger63Arithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger63Arithmetic.parseForBase(
                "11111",
                10
            )
            val resultRem = BigInteger63Arithmetic.substract(
                operand,
                BigInteger63Arithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.contentEquals(a.first)
        }
    }


    @Test
    fun testSpecificSqrtInt() {
        val seed = 1
        val random = Random(seed)
        val a = ULongArray(1) { 144U }
        val sqrt = BigInteger63Arithmetic.sqrtInt(a)
        assertTrue { sqrt[0] == 12UL }
    }

}