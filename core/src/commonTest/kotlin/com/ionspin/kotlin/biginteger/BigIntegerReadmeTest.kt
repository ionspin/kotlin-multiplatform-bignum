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

import kotlin.math.absoluteValue
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
    fun `Test readme addition sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val sum = a + b
        println("Sum: $sum")
    }

    @Test
    fun `Test readme subtraction sample`() {
        val a = BigInteger.fromLong(Long.MIN_VALUE)
        val b = BigInteger.fromLong(Long.MIN_VALUE)

        val difference = a - b
        println("Difference: $difference")
    }

    @Test
    fun `Test readme multiplication sample2`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromLong(Long.MIN_VALUE)

        val product = a * b

        println("Product: $product")
        val expectedResult = BigInteger.parseString("-85070591730234615856620279821087277056", 10)

        assertTrue { product == expectedResult }
    }

    @Test
    fun `Test readme division sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val quotient = dividend / divisor
        println("Quotient: $quotient")
        assertTrue { quotient == BigInteger.fromInt(1) }
    }

    @Test
    fun `Test readme remainder sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val remainder = dividend % divisor
        println("Remainder: $remainder")
        assertTrue { remainder == BigInteger.fromInt(Int.MAX_VALUE) }
    }

    @Test
    fun `Test readme division and remainder sample`() {
        val a = BigInteger.fromLong(Long.MAX_VALUE)
        val b = BigInteger.fromInt(Int.MAX_VALUE)

        val dividend = a + b
        val divisor = BigInteger.fromLong(Long.MAX_VALUE)

        val quotientAndRemainder = dividend divrem divisor

        println("Quotient: ${quotientAndRemainder.quotient} \nRemainder: ${quotientAndRemainder.remainder}")
        assertTrue { quotientAndRemainder.quotient == BigInteger.fromInt(1) }
        assertTrue { quotientAndRemainder.remainder == BigInteger.fromInt(Int.MAX_VALUE) }
    }

}