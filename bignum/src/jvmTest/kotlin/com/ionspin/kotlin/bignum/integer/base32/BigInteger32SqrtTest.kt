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

package com.ionspin.kotlin.bignum.integer.base32

import org.junit.Ignore
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Apr-2019
 */
@ExperimentalUnsignedTypes
class BigInteger32SqrtTest {

    @Ignore("Need to improve basic divide algo before using it in sqrt")
    @Test
    fun testSpecificSqrt() {
        assertTrue {
            val operand = BigInteger32Arithmetic.parseForBase(
                "547162628152978179694572006333394526730648083587307585845689465425919940113687046992147981" +
                    "70196777058540761287227012319014433594622337082294496524616700872104031016223448",
                10
            )
            val a = BigInteger32Arithmetic.sqrt(operand)
            println("Operand: ${BigInteger32Arithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger32Arithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger32Arithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger32Arithmetic.parseForBase(
                "7397044194494028975732055495441088126843300784390793504636322150624655374658058636588",
                10
            )
            val resultRem = BigInteger32Arithmetic.subtract(
                operand,
                BigInteger32Arithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.contentEquals(a.first) && resultRem.contentEquals(a.second)
        }

        assertTrue {
            val operand = BigInteger32Arithmetic.parseForBase("123456789", 10)
            val a = BigInteger32Arithmetic.sqrt(operand)
            println("Operand: ${BigInteger32Arithmetic.toString(operand, 10)}")
            println("Sqrt: ${BigInteger32Arithmetic.toString(a.first, 10)}")
            println("Remainder: ${BigInteger32Arithmetic.toString(a.second, 10)}")
            val resultSqrt = BigInteger32Arithmetic.parseForBase(
                "11111",
                10
            )
            val resultRem = BigInteger32Arithmetic.subtract(
                operand,
                BigInteger32Arithmetic.multiply(resultSqrt, resultSqrt)
            )

            resultSqrt.contentEquals(a.first)
        }
    }

    @Test
    fun testSpecificSqrtInt() {
        val seed = 1
        val random = Random(seed)
        val a = UIntArray(1) { 144U }
        val sqrt = BigInteger32Arithmetic.sqrtInt(a)
        assertTrue { sqrt[0] == 12U }
    }
}