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

import com.ionspin.kotlin.bignum.ByteArrayRepresentation
import com.ionspin.kotlin.bignum.Endianness
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2019
 */

class ByteArrayConversionTest {

    @Ignore // Travis can't run this test on JS for some reason but they pass locally.
    @Test
    fun testToAndFromByteArray() {
        assertTrue {
            val bigIntOriginal = BigInteger.fromULong(ULong.MAX_VALUE)
            val byteArray = bigIntOriginal.oldToTypedByteArray()
            val reconstructed = BigInteger.oldFromByteArray(byteArray)
            bigIntOriginal == reconstructed
        }

        assertTrue {
            val bigIntOriginal = BigInteger.fromLong(Long.MIN_VALUE)
            val byteArray = bigIntOriginal.oldToTypedByteArray()
            val reconstructed = BigInteger.oldFromByteArray(byteArray)
            bigIntOriginal.equals(reconstructed)
        }

        assertTrue {
            val bigIntOriginal = BigInteger.fromULong(ULong.MAX_VALUE) + BigInteger.fromULong(ULong.MAX_VALUE)
            val byteArray = bigIntOriginal.oldToTypedByteArray()
            val reconstructed = BigInteger.oldFromByteArray(byteArray)
            bigIntOriginal.equals(reconstructed)
        }
    }

    @Test
    fun toUByteArrayPositive() {
        assertTrue {
            val expected = ubyteArrayOf(
                0x00U, 0x11U, 0x22U, 0x33U, 0x44U, 0x55U, 0x66U, 0x77U, 0x88U, 0x99U, 0xAAU, 0xBBU, 0xCCU, 0xDDU, 0xEEU, 0xFFU
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray()
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0xFFU, 0xEEU, 0xDDU, 0xCCU, 0xBBU, 0xAAU, 0x99U, 0x88U, 0x77U, 0x66U, 0x55U, 0x44U, 0x33U, 0x22U, 0x11U, 0x00U
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(endianness = Endianness.LITTLE)
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0x00U, 0x11U, 0x22U, 0x33U, 0x44U, 0x55U, 0x66U, 0x77U, 0x88U, 0x99U, 0xAAU, 0xBBU, 0xCCU, 0xDDU, 0xEEU, 0xFFU
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.EIGHT_BYTE_NUMBER,
                endianness = Endianness.BIG,
                twosComplement = false
            )
            byteArray.contentEquals(expected)
        }
        assertTrue {
            val expected = ubyteArrayOf(
                0x77U, 0x66U, 0x55U, 0x44U, 0x33U, 0x22U, 0x11U, 0x00U, 0xFFU, 0xEEU, 0xDDU, 0xCCU, 0xBBU, 0xAAU, 0x99U, 0x88U
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.EIGHT_BYTE_NUMBER,
                endianness = Endianness.LITTLE,
                twosComplement = false
            )
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0x00U, 0x11U, 0x22U, 0x33U, 0x44U, 0x55U, 0x66U, 0x77U, 0x88U, 0x99U, 0xAAU, 0xBBU, 0xCCU, 0xDDU, 0xEEU, 0xFFU
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.FOUR_BYTE_NUMBER,
                endianness = Endianness.BIG,
                twosComplement = false
            )
            byteArray.contentEquals(expected)
        }
        assertTrue {
            val expected = ubyteArrayOf(
                0x33U, 0x22U, 0x11U, 0x00U, 0x77U, 0x66U, 0x55U, 0x44U, 0xBBU, 0xAAU, 0x99U, 0x88U, 0xFFU, 0xEEU, 0xDDU, 0xCCU
            )
            val bigIntOriginal = BigInteger.parseString("00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.FOUR_BYTE_NUMBER,
                endianness = Endianness.LITTLE,
                twosComplement = false
            )
            byteArray.contentEquals(expected)
        }
    }

    @Test
    fun toUByteArrayNegative() {
        assertTrue {
            val expected = ubyteArrayOf(
                0xEEU, 0xDDU, 0xCCU, 0xBBU, 0xAAU, 0x99U, 0x88U, 0x77U, 0x66U, 0x55U, 0x44U, 0x33U, 0x22U, 0x11U, 0x01U
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(twosComplement = true)
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0x01U, 0x11U, 0x22U, 0x33U, 0x44U, 0x55U, 0x66U, 0x77U, 0x88U, 0x99U, 0xAAU, 0xBBU, 0xCCU, 0xDDU, 0xEEU
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(endianness = Endianness.LITTLE, twosComplement = true)
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0xEEU, 0xDDU, 0xCCU, 0xBBU, 0xAAU, 0x99U, 0x88U, 0x77U, 0x66U, 0x55U, 0x44U, 0x33U, 0x22U, 0x11U, 0x01U
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.EIGHT_BYTE_NUMBER,
                endianness = Endianness.BIG,
                twosComplement = true
            )
            byteArray.contentEquals(expected)
        }
        assertTrue {
            val expected = ubyteArrayOf(
                0x77U, 0x66U, 0x55U, 0x44U, 0x33U, 0x22U, 0x11U, 0x01U, 0xEEU, 0xDDU, 0xCCU, 0xBBU, 0xAAU, 0x99U, 0x88U,
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.EIGHT_BYTE_NUMBER,
                endianness = Endianness.LITTLE,
                twosComplement = true
            )
            byteArray.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                0x00U, 0x11U, 0x22U, 0x33U, 0x44U, 0x55U, 0x66U, 0x77U, 0x88U, 0x99U, 0xAAU, 0xBBU, 0xCCU, 0xDDU, 0xEEU, 0xFFU
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.FOUR_BYTE_NUMBER,
                endianness = Endianness.BIG,
                twosComplement = true
            )
            byteArray.contentEquals(expected)
        }
        assertTrue {
            val expected = ubyteArrayOf(
                0x33U, 0x22U, 0x11U, 0x00U, 0x77U, 0x66U, 0x55U, 0x44U, 0xBBU, 0xAAU, 0x99U, 0x88U, 0xFFU, 0xEEU, 0xDDU, 0xCCU
            )
            val bigIntOriginal = BigInteger.parseString("-00112233445566778899AABBCCDDEEFF", 16)
            val byteArray = bigIntOriginal.toUByteArray(
                byteArrayRepresentation = ByteArrayRepresentation.FOUR_BYTE_NUMBER,
                endianness = Endianness.LITTLE,
                twosComplement = true
            )
            byteArray.contentEquals(expected)
        }
    }

    @Test
    fun fromUByteArray() {
        assertTrue {
            val uByteArray = "19191919191919191919191919191919".chunked(2).map { it.toUByte(16) }.toTypedArray()
            val bigInt = BigInteger.oldFromUByteArray(
                uByteArray,
                Endianness.BIG
            )
            val reconstructed = bigInt.oldToTypedUByteArray()
            uByteArray.contentEquals(reconstructed)
        }
    }

    @Test
    fun specificToUByteArrayTest() {
        val bigInteger = BigInteger.parseString("01234567", 16)
        val expectedBigEndian = ubyteArrayOf(
            0x01U, 0x23U, 0x45U, 0x67U
        )
        val expectedLittleEndian = ubyteArrayOf(
            0x67U, 0x45U, 0x23U, 0x01U
        )
        assertTrue {
            val littleResult = bigInteger.oldToUByteArray(Endianness.LITTLE)
            littleResult.contentEquals(expectedLittleEndian)
        }
        assertTrue {
            val bigResult = bigInteger.oldToUByteArray(Endianness.BIG)
            bigResult.contentEquals(expectedBigEndian)
        }
    }

    @Test
    fun specificFromUbyteArrayTest() {
        val expected = BigInteger.parseString("01234567012345670123", 16)
        val inputBigEndian = ubyteArrayOf(
            0x01U, 0x23U, 0x45U, 0x67U, 0x01U, 0x23U, 0x45U, 0x67U, 0x01U, 0x23U
        )
        val inputLittleEndian = ubyteArrayOf(
            0x67U, 0x45U, 0x23U, 0x01U, 0x67U, 0x45U, 0x23U, 0x01U, 0x23U, 0x01U
        )
        // assertTrue {
        //     val littleResult = BigInteger.fromUByteArray(inputLittleEndian)
        //     littleResult == expected
        // }
        assertTrue {
            val bigResult = BigInteger.oldFromUByteArray(inputBigEndian)
            bigResult == expected
        }
    }
}
