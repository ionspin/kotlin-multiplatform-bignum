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

package com.ionspin.kotlin.bignum.integer

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Mar-2019
 */

class BigIntegerReadmeTest {

    @Test
    fun `Test_readme_addition_sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)
        val sum = a + b
        println("Sum: $sum")

        val expectedResult = BigInteger.parseString("9223372039002259454", 10)
        assertTrue { sum == expectedResult }
    }

    @Test
    fun `Test_readme_subtraction_sample`() {
        val a = BigInteger.fromLong(Long.MIN_VALUE)
        val b = BigInteger.fromLong(Long.MAX_VALUE)

        val difference = a - b
        println("Difference: $difference")

        val expectedResult = BigInteger.parseString("-18446744073709551615", 10)
        assertTrue { difference == expectedResult }
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
    fun `Test_readme_remainder_sample_2`() {
        val a = BigInteger.parseString("40223423789827791298722074994617538382438097461777180133170684331165292089790704903067467860892991215732837372700266699497738234157333700417919496666318783")
        val b = BigInteger.parseString("115792089237316195423570985008687907853269984665640564039457584007913129639319")
        val r = a%b
        assertTrue { r == BigInteger.fromInt(9) }
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
    fun `Test_readme_xor_sample`() {
        val operand = BigInteger.parseString("11110000", 2)
        val mask = BigInteger.parseString("00111100", 2)
        val xorResult = operand xor mask
        println("Xor result: ${xorResult.toString(2)}")

        val expectedResult = BigInteger.parseString("11001100", 2)

        assertTrue { xorResult == expectedResult }
    }

    @Test
    fun `Test_readme_or_sample`() {
        val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger.parseString("00000000FFFF0000000000", 16)
        val orResult = operand or mask
        println("Or result: ${orResult.toString(16)}")

        val expectedResult = BigInteger.parseString("FFFFFFFFFFFF0000000000", 16)

        assertTrue { orResult == expectedResult }
    }

    @Test
    fun `Test_readme_and_sample`() {
        val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
        val mask = BigInteger.parseString("00000000FFFF0000000000", 16)
        val andResult = operand and mask
        println("And result: ${andResult.toString(16)}")

        val expectedResult = BigInteger.parseString("00000000FF000000000000", 16)

        assertTrue { andResult == expectedResult }
    }

    @Test
    fun `Test_readme_inv_sample`() {
        val operand = BigInteger.parseString("11110000", 2)
        val invResult = operand.not()
        println("Inv result: ${invResult.toString(2)}")

        val expectedResult = BigInteger.parseString("00001111", 2)

        assertTrue { invResult == expectedResult }
    }

    @Test
    fun `Test_readme_negate_sample`() {
        val a = (-5).toBigInteger()
        val negated = a.negate()
        println("Negated: $negated")

        val expectedResult = 5.toBigInteger()

        assertTrue { negated == expectedResult }
    }

    @Test
    fun `Test_readme_abs_sample`() {
        val a = (-5).toBigInteger()
        val negated = a.abs()
        println("Absolute value: $negated")

        val expectedResult = 5.toBigInteger()

        assertTrue { negated == expectedResult }
    }

    @Test
    fun `Test_readme_big_integer_extensions`() {
        assertTrue {
            val bigint = "123456789012345678901234567890".toBigInteger()
            val expected = BigInteger.parseString("123456789012345678901234567890", 10)
            bigint == expected
        }

        assertTrue {
            val bigint = 1234567890123456L.toBigInteger()
            val expected = BigInteger.parseString("1234567890123456", 10)
            bigint == expected
        }

        assertTrue {
            val bigint = 1234.toShort().toBigInteger()
            val expected = BigInteger.parseString("1234", 10)
            bigint == expected
        }

        assertTrue {
            val bigint = 12.toByte().toBigInteger()
            val expected = BigInteger.parseString("12", 10)
            bigint == expected
        }

        assertFailsWith(NumberFormatException::class) {
            val parsed = BigInteger.parseString("a", 10)
        }

        assertFailsWith(NumberFormatException::class) {
            val parsed = BigInteger.parseString("Z", 35)
        }

        assertFailsWith(NumberFormatException::class) {
            val parsed = BigInteger.parseString("A", 37)
        }

        assertFailsWith(NumberFormatException::class) {
            val parsed = BigInteger.parseString("A", 1)
        }
    }

    @Test
    fun testToModularBigInteger() {
        val a = 100_002.toBigInteger()
        val modularA = a.toModularBigInteger(500.toBigInteger())
        println("ModularBigInteger: ${modularA.toStringWithModulo()}")
        assertTrue { modularA.compareTo(2.toBigInteger()) == 0 }
    }
}
