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

import com.ionspin.kotlin.bignum.integer.base32.toJavaBigInteger
import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic
import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic.baseMask
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

class BigInteger63ListJavaBaseConversionTest() {

    @Test
    fun test63To64Conversion() {
        assertTrue {
            val a = listOf(1UL, 1UL, 1UL)
            val b = BigInteger63LinkedListArithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()

            aBigInt == bBigInt
        }

        assertTrue {
            val a = listOf(0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 1UL, 1UL)
            val b = BigInteger63LinkedListArithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()

            aBigInt == bBigInt
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val a = List<ULong>(321) { random.nextULong() and baseMask }
            val b = BigInteger63LinkedListArithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()

            aBigInt == bBigInt
        }

        assertTrue {
            val a = List<ULong>(129) { (0UL - 1UL) shr 1 }
            val b = BigInteger63LinkedListArithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()

            aBigInt == bBigInt
        }
    }

    @Test
    fun test63To32Conversion() {
        assertTrue {
            val a = listOf(1UL, 1UL, 1UL)
            val b = BigInteger63LinkedListArithmetic.convertTo32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()

            aBigInt == bBigInt
        }
    }

    @Test
    fun test32To63Conversion() {
        assertTrue {
            val a = uintArrayOf(1U, 0U - 1U, 0U, 0U - 1U)
            val b = BigInteger63LinkedListArithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()

            aBigInt == bBigInt
        }

        assertTrue {
            val a = uintArrayOf(1U, 0U - 1U, 0U, 0U - 1U, 1U)
            val b = BigInteger63LinkedListArithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()

            aBigInt == bBigInt
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val a = UIntArray(352) { random.nextUInt() }
            val b = BigInteger63LinkedListArithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()

            aBigInt == bBigInt
        }
    }

    private fun List<ULong>.fromBase64toJavaBigInteger(): BigInteger {
        return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
            acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 64))
        }
    }
}
