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

package com.ionspin.kotlin.biginteger

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Mar-3/17/19
 */
@ExperimentalUnsignedTypes
class BigIntegerReadmeTest {


    @Test
    fun `Test_readme_addition_sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val sum = a + b
        println("Sum: $sum")
    }

    @Test
    fun `Test_readme_subtraction_sample`() {
        val a = BigInteger.fromLong(Long.MIN_VALUE)
        val b = BigInteger.fromLong(Long.MIN_VALUE)

        val difference = a - b
        println("Difference: $difference")
    }

    @Test
    fun `Test_readme_multiplication_sample2`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromLong(Long.MIN_VALUE)

        val product = a * b

        println("Product: $product")
        val expectedResult = BigInteger.parseString("-85070591730234615856620279821087277056")

        assertTrue { product == expectedResult }
    }

    @Test
    fun `Test_readme_division_sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val quotient = dividend / divisor
        println("Quotient: $quotient")
        assertTrue { quotient == BigInteger.fromInt(1) }
    }

    @Test
    fun `Test_readme_remainder_sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val remainder = dividend % divisor
        println("Remainder: $remainder")
        assertTrue { remainder == BigInteger.fromInt(Int.MAX_VALUE) }
    }

    @Test
    fun `Test_readme_division_and_remainder_sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val quotientAndRemainder = dividend divrem divisor

        println("Quotient: ${quotientAndRemainder.quotient} \nRemainder: ${quotientAndRemainder.remainder}")
        assertTrue { quotientAndRemainder.quotient == BigInteger.fromInt(1) }
        assertTrue { quotientAndRemainder.remainder == BigInteger.fromInt(Int.MAX_VALUE) }
    }

    @Test
    fun `Test_readme_shift_left_sample`() {
        val a = BigInteger.fromByte(1)

        val shifted = a shl 215
        println("Shifted: $shifted")
        val expectedResult = BigInteger.parseString("52656145834278593348959013841835216159447547700274555627155488768")
        assertTrue { shifted == expectedResult }
    }

    @Test
    fun `Test_readme_shift_right_sample`() {
        val a = BigInteger.parseString("100000000000000000000000000000000", 10)

        val shifted = a shr 90
        println("Shifted: $shifted")
        val expectedResult = BigInteger.parseString("80779")
        assertTrue { expectedResult == shifted }
    }

    @Test
    fun `Test_readme_xor_sample`(){
        val operand = BigInteger.parseString("11110000", 2)
        val mask =    BigInteger.parseString("00111100", 2)
        val xorResult = operand xor mask
        println("Xor result: ${xorResult.toString(2)}")

        val expectedResult = BigInteger.parseString("11001100", 2)

        assertTrue { xorResult == expectedResult }


    }

    @Test
    fun `Test_readme_or_sample`(){
        val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
        val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
        val orResult = operand or mask
        println("Or result: ${orResult.toString(16)}")

        val expectedResult = BigInteger.parseString("FFFFFFFFFFFF0000000000", 16)

        assertTrue { orResult == expectedResult }


    }

    @Test
    fun `Test_readme_and_sample`(){
        val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
        val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
        val andResult = operand and mask
        println("And result: ${andResult.toString(16)}")

        val expectedResult = BigInteger.parseString("00000000FF000000000000", 16)

        assertTrue { andResult == expectedResult }


    }

    @Test
    fun `Test_readme_inv_sample`(){
        val operand = BigInteger.parseString("11110000", 2)
        val invResult = operand.invPrecise()
        println("Inv result: ${invResult.toString(2)}")

        val expectedResult = BigInteger.parseString("00001111", 2)

        assertTrue { invResult == expectedResult }


    }

}