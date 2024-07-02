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

package com.ionspin.kotlin.bignum.integer.integer

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 01-Nov-2019
 */
class BigIntegerBitwiseOperations {
    @Test
    fun xorWithZero() {
        val operand = BigInteger.parseString("11110000", 2)
        val mask = BigInteger.ZERO
        val xorResult = operand xor mask
        println("Xor result: ${xorResult.toString(2)}")

        val expectedResult = operand

        assertEquals(expectedResult, xorResult)
        assertEquals(expectedResult, mask xor operand)
    }

    @Test
    fun xorBiggerThanLongMaxWithZero() {
        val operand = BigInteger.parseString("9223372036854775808", 10)
        val mask = BigInteger.ZERO

        val expectedResult = operand

        assertEquals(expectedResult, operand xor mask)
        assertEquals(expectedResult, mask xor operand)
    }
}
