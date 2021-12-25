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
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.math.absoluteValue
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

class ConversionTest {

    @Test
    fun testFromInt() {
        val one = BigInteger.fromInt(1)
        val negativeOne = BigInteger.fromInt(-1)
        val maxInt = BigInteger.fromInt(Int.MAX_VALUE)
        val minInt = BigInteger.fromInt(Int.MIN_VALUE)

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }

        assertTrue {
            maxInt.magnitude[0] == Int.MAX_VALUE.toLong().absoluteValue.toULong() &&
                    maxInt.sign == Sign.POSITIVE
        }
        assertTrue {
            minInt.magnitude[0] == Int.MIN_VALUE.toLong().absoluteValue.toULong() &&
                    minInt.sign == Sign.NEGATIVE
        }
    }

    @Test
    fun testFromLong() {
        val one = BigInteger.fromLong(1)
        val negativeOne = BigInteger.fromLong(-1)
        val maxLong = BigInteger.fromLong(Long.MAX_VALUE)
        val minLong = BigInteger.fromLong(Long.MIN_VALUE)

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }

        assertTrue {
            maxLong.magnitude[0] == Long.MAX_VALUE.absoluteValue.toULong() &&
                    maxLong.sign == Sign.POSITIVE
        }
        assertTrue {
            minLong.magnitude[0] == 0UL && minLong.magnitude[1] == 1UL
                    minLong.sign == Sign.NEGATIVE
        }
    }

    @Test
    fun testFromShort() {
        val one = BigInteger.fromShort(1)
        val negativeOne = BigInteger.fromShort(-1)
        val maxShort = BigInteger.fromShort(Short.MAX_VALUE)
        val minShort = BigInteger.fromShort(Short.MIN_VALUE)

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }

        assertTrue {
            maxShort.magnitude[0] == Short.MAX_VALUE.toLong().absoluteValue.toULong() &&
                    maxShort.sign == Sign.POSITIVE
        }
        assertTrue {
            minShort.magnitude[0] == Short.MIN_VALUE.toLong().absoluteValue.toULong() &&
                    minShort.sign == Sign.NEGATIVE
        }
    }

    @Test
    fun testFromByte() {
        val one = BigInteger.fromByte(1)
        val negativeOne = BigInteger.fromByte(-1)
        val maxByte = BigInteger.fromByte(Byte.MAX_VALUE)
        val minByte = BigInteger.fromByte(Byte.MIN_VALUE)

        assertTrue { one == BigInteger.ONE }
        assertTrue { negativeOne == -BigInteger.ONE }
        assertTrue {
            maxByte.magnitude[0] == Byte.MAX_VALUE.toLong().absoluteValue.toULong() &&
                    maxByte.sign == Sign.POSITIVE
        }
        assertTrue {
            minByte.magnitude[0] == Byte.MIN_VALUE.toLong().absoluteValue.toULong() &&
                    minByte.sign == Sign.NEGATIVE
        }
    }

    @Test
    fun testToFloat() {
        val stringTestCases = listOf(
            "-1", "0", "1",
            Long.MAX_VALUE.toString(), Long.MIN_VALUE.toString(),
            Long.MAX_VALUE.toString() + "123", Long.MIN_VALUE.toString() + "123"
        )

        stringTestCases.forEach {
            assertTrue {
                val bigInt = BigInteger.parseString(it)
                val float = it.toFloat()
                bigInt.floatValue() == float
            }
        }
    }

    @Test
    fun testToDouble() {
        val stringTestCases = listOf(
            "-1", "0", "1",
            Long.MAX_VALUE.toString(), Long.MIN_VALUE.toString(),
            Long.MAX_VALUE.toString() + "123", Long.MIN_VALUE.toString() + "123"
        )
        stringTestCases.forEach {
            assertTrue {
                val bigInt = BigInteger.parseString(it)
                val double = it.toDouble()
                bigInt.doubleValue() == double
            }
        }
    }

    @Ignore // Takes too long on Travis, so JS test fail (>2000ms). Karatsuba implementation slowed this down, so needs investigation
    @Test
    fun testDoubleValueExactFailure() {
        val stringTestCases = listOf(
//            Double.MAX_VALUE.toString() + "1" // This "1" affects the exponent
            Double.MAX_VALUE.toString()
        )

        stringTestCases.forEach {
            assertFailsWith<ArithmeticException> {
                println("Double value to parse: $it")
                val bigInt = BigInteger.parseString(it)
                bigInt.doubleValue(true)
            }
        }
    }

    @Test
    fun testBigIntegerFloatCreation() {
        val floatTestValueArray = arrayOf(
            1f, 0f, -1f,
            123E5f
        )
        floatTestValueArray.forEach {
            assertTrue {
                val bigInt = BigInteger.tryFromFloat(it)
                bigInt.floatValue() == it
            }
        }

        assertTrue {
            val bigInt = BigInteger.tryFromFloat(Float.MIN_VALUE)
            bigInt.floatValue() == 0f
        }
    }

    @Test
    fun testBigIntegerDoubleCreation() {
        val doubleTestValueArray = arrayOf(
            1.0, 0.0, -1.0,
            1.23E5
        )
        doubleTestValueArray.forEach {
            assertTrue {
                val bigInt = BigInteger.tryFromDouble(it)
                bigInt.doubleValue() == it
            }
        }

        assertTrue {
            val bigInt = BigInteger.tryFromDouble(Double.MIN_VALUE)
            bigInt.doubleValue() == 0.0
        }
    }

    @Test
    fun testPrimitiveConversionSignValues() {
        assertTrue {
            -1.toBigInteger().longValue() == -1L
        }
        assertTrue {
            -1.toBigInteger().intValue() == -1
        }
        assertTrue {
            -1.toBigInteger().shortValue() == -1
        }
        assertTrue {
            -1.toBigInteger().byteValue() == -1
        }

        assertTrue {
            1.toBigInteger().longValue() == 1L
        }
        assertTrue {
            1.toBigInteger().intValue() == 1
        }
        assertTrue {
            1.toBigInteger().shortValue() == 1.toShort()
        }
        assertTrue {
            1.toBigInteger().byteValue() == 1.toByte()
        }

        assertTrue {
            Long.MIN_VALUE.toBigInteger().longValue() == Long.MIN_VALUE
        }
        assertTrue {
            ULong.MAX_VALUE.toBigInteger().ulongValue() == ULong.MAX_VALUE
        }

        assertTrue {
            Int.MIN_VALUE.toBigInteger().intValue() == Int.MIN_VALUE
        }
        assertTrue {
            UInt.MAX_VALUE.toBigInteger().uintValue() == UInt.MAX_VALUE
        }

        assertTrue {
            Short.MIN_VALUE.toBigInteger().shortValue() == Short.MIN_VALUE
        }
        assertTrue {
            UShort.MAX_VALUE.toBigInteger().ushortValue() == UShort.MAX_VALUE
        }

        assertTrue {
            Byte.MIN_VALUE.toBigInteger().byteValue() == Byte.MIN_VALUE
        }
        assertTrue {
            UByte.MAX_VALUE.toBigInteger().ubyteValue() == UByte.MAX_VALUE
        }
    }
}
