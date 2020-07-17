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

package com.ionspin.kotlin.bignum.integer.base63List

import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Apr-2019
 */

class BigIntegerList63SqrtTest() {

    @Test
    fun testSpecificSqrt() {
        assertTrue {
            val seed = 1
            val random = Random(seed)
            val operand = List<ULong>(9) { random.nextULong() shr 1 }
            val a = BigInteger63LinkedListArithmetic.sqrt(operand)
            println("Operand: ${BigInteger63LinkedListArithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger63LinkedListArithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger63LinkedListArithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger63LinkedListArithmetic.parseForBase(
                "7397044194494028975732055495441088126843300784390793504636322150624655374658058636588",
                10
            )
            val resultRem = BigInteger63LinkedListArithmetic.subtract(
                operand,
                BigInteger63LinkedListArithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.equals(a.first)
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val operand = BigInteger63LinkedListArithmetic.parseForBase("123456789", 10)
            val a = BigInteger63LinkedListArithmetic.sqrt(operand)
            println("Operand: ${BigInteger63LinkedListArithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger63LinkedListArithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger63LinkedListArithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger63LinkedListArithmetic.parseForBase(
                "11111",
                10
            )
            val resultRem = BigInteger63LinkedListArithmetic.subtract(
                operand,
                BigInteger63LinkedListArithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.equals(a.first)
        }
    }

    @Test
    fun testSpecificSqrtInt() {
        val seed = 1
        val random = Random(seed)
        val a = List<ULong>(1) { 144U }
        val sqrt = BigInteger63LinkedListArithmetic.sqrtInt(a)
        assertTrue { sqrt[0] == 12UL }
    }
}
