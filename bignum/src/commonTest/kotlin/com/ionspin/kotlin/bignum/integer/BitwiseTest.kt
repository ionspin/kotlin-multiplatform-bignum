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

package com.ionspin.kotlin.bignum.integer.arithmetic

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

class BitwiseTest {

    @Test
    fun testNegate() {
        val bigInt = BigInteger.fromInt(123)
        val negatedBigInt = -bigInt
        val expectedBigInt = BigInteger.fromInt(-123)

        assertTrue { negatedBigInt == expectedBigInt }
    }

    @Test
    fun numberOfDigitsTest() {
        val bigInteger = BigInteger.parseString("123456789012345678901234567890123456", 10)
        val numberOfDigits = bigInteger.numberOfDecimalDigits()
        val expectedDigits = 36L
        assertTrue { numberOfDigits == expectedDigits }
    }

    @Test
    fun numberOfDigitsBigTest() {
        var bigInteger = BigInteger.ONE
        for (i in 1..200L) {
            val numberOfDigits = bigInteger.numberOfDecimalDigits()
            bigInteger = bigInteger * 10
            assertTrue { numberOfDigits == i }
        }
    }

    @Test
    fun testBitAt() {
        assertTrue {
            val bigInt = 2.toBigInteger()
            bigInt.bitAt(0) == false && bigInt.bitAt(1) == true
        }
        assertTrue {
            val bigInt = Long.MAX_VALUE.toBigInteger() + 1
            (bigInt.bitAt(62) == false) && (bigInt.bitAt(63) == true)
        }
        assertTrue {
            val bigInt = ULong.MAX_VALUE.toBigInteger() + 1
            bigInt.bitAt(63) == false && bigInt.bitAt(64) == true
        }

        assertTrue {
            val bigInt = Long.MAX_VALUE.toBigInteger()
            (0..62L).fold(true) { acc, i ->
                acc and bigInt.bitAt(i)
            }
        }
    }

    @Test
    fun setBitAtTest() {
        assertTrue {
            val bigInt = BigInteger.fromInt(4)
            val result = bigInt.setBitAt(1, true)
            result == 6.toBigInteger()
        }

        assertTrue {
            val bigInt = BigInteger.fromInt(6)
            val result = bigInt.setBitAt(1, false)
            result == 4.toBigInteger()
        }

        assertTrue {
            val bigInt = BigInteger.fromLong(Long.MAX_VALUE)
            val result = bigInt.setBitAt(32, false)
            result == (Long.MAX_VALUE.toBigInteger() - 2.toBigInteger().pow(32))
        }

        assertTrue {
            val bigInt = BigInteger.fromLong(Long.MAX_VALUE) - 2.toBigInteger().pow(32)
            val result = bigInt.setBitAt(32, true)
            result == (Long.MAX_VALUE.toBigInteger())
        }
    }

    @Test
    fun bitLengthTestSimple() {
        assertTrue {
            val bigInt = BigInteger.fromInt(0)
            val result = bigInt.bitLength()
            result == 0
        }

        assertTrue {
            val bigInt = BigInteger.fromInt(1)
            val result = bigInt.bitLength()
            result == 1
        }

        assertTrue {
            val bigInt = BigInteger.fromInt(2)
            val result = bigInt.bitLength()
            result == 2
        }

        assertTrue {
            val bigInt = BigInteger.fromInt(4)
            val result = bigInt.bitLength()
            result == 3
        }
    }

    @Test
    fun bitLengthTest() {
        for (i in 0..4096) {
            val bigInt = BigInteger.ONE.shl(i)
            val bitLength = bigInt.bitLength()
            assertEquals(i + 1, bitLength)
        }
    }

    @Test
    fun github290OrWithZeroTest() {
        assertEquals(
            BigInteger.parseString("11"),
            BigInteger.parseString("0").or(BigInteger.parseString("11"))
        )
        assertEquals(
            BigInteger.parseString("11"),
            BigInteger.parseString("11").or(BigInteger.parseString("0"))
        )
        assertEquals(
            BigInteger.ZERO,
            BigInteger.parseString("0").or(BigInteger.parseString("0"))
        )
    }

    @Test
    fun twosComplementBitwiseTest() {
        val cases = listOf(
            listOf("-1", "11", "11", "-1", "-12"),
            listOf("-12", "10", "0", "-2", "-2"),
            listOf("12", "-10", "4", "-2", "-6"),
            listOf("-12", "-10", "-12", "-10", "2"),
            listOf("12", "10", "8", "14", "6"),
            listOf("0", "-10", "0", "-10", "-10"),
            listOf("-12", "0", "0", "-12", "-12"),
            listOf("0", "0", "0", "0", "0"),
            listOf("-1180591620717411303424", "18446744073709551621", "0", "-1162144876643701751803", "-1162144876643701751803"),
            listOf("1180591620717411303427", "-18446744073709551616", "1180591620717411303424", "-18446744073709551613", "-1199038364791120855037"),
            listOf("-1180591620717411303425", "-18446744073709551623", "-1199038364791120855047", "-1", "1199038364791120855046")
        )
        cases.forEach { (first, second, expectedAnd, expectedOr, expectedXor) ->
            val a = BigInteger.parseString(first)
            val b = BigInteger.parseString(second)
            assertEquals(BigInteger.parseString(expectedAnd), a andTwosComplement b)
            assertEquals(BigInteger.parseString(expectedOr), a orTwosComplement b)
            assertEquals(BigInteger.parseString(expectedXor), a xorTwosComplement b)
        }
    }

    @Test
    fun bitwiseSignHandlingTest() {
        val cases = listOf(
            listOf("12", "10", "8", "14", "6"),
            listOf("-12", "10", "8", "-14", "-6"),
            listOf("12", "-10", "8", "-14", "-6"),
            listOf("-12", "-10", "-8", "-14", "6"),
            listOf("-5", "5", "5", "-5", "0"),
            listOf("5", "-5", "5", "-5", "0"),
            listOf("-5", "-5", "-5", "-5", "0"),
            listOf("0", "-10", "0", "-10", "-10"),
            listOf("-12", "0", "0", "-12", "-12"),
            listOf("0", "0", "0", "0", "0"),
            listOf("-4", "3", "0", "-7", "-7"),
            listOf("-1180591620717411303425", "18446744073709551623", "1", "-1199038364791120855047", "-1199038364791120855046")
        )
        cases.forEach { (first, second, expectedAnd, expectedOr, expectedXor) ->
            val a = BigInteger.parseString(first)
            val b = BigInteger.parseString(second)
            assertEquals(BigInteger.parseString(expectedAnd), a and b)
            assertEquals(BigInteger.parseString(expectedOr), a or b)
            assertEquals(BigInteger.parseString(expectedXor), a xor b)
            assertEquals(a and b, b and a)
            assertEquals(a or b, b or a)
            assertEquals(a xor b, b xor a)
            assertEquals(BigInteger.parseString(expectedAnd).getSign(), (a and b).getSign())
            assertEquals(BigInteger.parseString(expectedOr).getSign(), (a or b).getSign())
            assertEquals(BigInteger.parseString(expectedXor).getSign(), (a xor b).getSign())
        }
    }
}
