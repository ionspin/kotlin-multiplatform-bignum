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

import com.ionspin.kotlin.bignum.Endianness
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2019
 */
@ExperimentalUnsignedTypes
class ByteArrayConversionTest {

    @Ignore // Travis can't run this test on JS for some reason but they pass on local machine.
    @Test
    fun testToAndFromByteArray() {
        assertTrue {
            val bigIntOriginal = BigInteger.fromULong(ULong.MAX_VALUE)
            val byteArray = bigIntOriginal.toByteArray()
            val reconstructed = BigInteger.fromByteArray(byteArray)
            bigIntOriginal == reconstructed
        }

        assertTrue {
            val bigIntOriginal = BigInteger.fromLong(Long.MIN_VALUE)
            val byteArray = bigIntOriginal.toByteArray()
            val reconstructed = BigInteger.fromByteArray(byteArray)
            bigIntOriginal.equals(reconstructed)
        }

        assertTrue {
            val bigIntOriginal = BigInteger.fromULong(ULong.MAX_VALUE) + BigInteger.fromULong(ULong.MAX_VALUE)
            val byteArray = bigIntOriginal.toByteArray()
            val reconstructed = BigInteger.fromByteArray(byteArray)
            bigIntOriginal.equals(reconstructed)
        }
    }

    @Test
    fun fromUByteArray() {
        assertTrue {
            val uByteArray = "19191919191919191919191919191919".chunked(2).map { it.toUByte(16) }.toTypedArray()
            val bigInt = BigInteger.fromUByteArray(
                uByteArray,
                Endianness.BIG
            )
            val reconstructed = bigInt.toUByteArray()
            uByteArray.contentEquals(reconstructed)
        }
    }
}