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

package com.ionspin.kotlin.bignum.modular

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Apr-2019
 */

class ModularBigIntegerVariousTests {
    @Test
    fun testCreatorConstantsAreReduced() {
        val modSeven = ModularBigInteger.creatorForModulo(7)
        assertEquals(BigInteger.fromInt(3), modSeven.TEN.residue)
        assertEquals(modSeven.fromInt(3), modSeven.TEN)
        assertEquals(BigInteger.TWO, modSeven.TWO.residue)

        val modTwo = ModularBigInteger.creatorForModulo(2)
        assertEquals(BigInteger.ZERO, modTwo.TWO.residue)
        assertEquals(BigInteger.ZERO, modTwo.TEN.residue)
        assertEquals(BigInteger.ONE, modTwo.ONE.residue)

        assertEquals(BigInteger.ZERO, ModularBigInteger.creatorForModulo(1).ONE.residue)
        assertEquals(BigInteger.TEN, ModularBigInteger.creatorForModulo(11).TEN.residue)
    }

    @Test
    fun testExactLongNarrowing() {
        val creator = ModularBigInteger.creatorForModulo(BigInteger.ONE.shl(70))
        val twoTo32 = creator.fromBigInteger(BigInteger.ONE.shl(32))
        val twoTo63 = creator.fromBigInteger(BigInteger.ONE.shl(63))
        val twoTo64 = creator.fromBigInteger(BigInteger.ONE.shl(64))

        assertEquals(4294967296L, twoTo32.longValue(true))
        assertEquals(4294967296uL, twoTo32.ulongValue(true))
        assertEquals(Long.MAX_VALUE, creator.fromLong(Long.MAX_VALUE).longValue(true))
        assertEquals(9223372036854775808uL, twoTo63.ulongValue(true))
        assertEquals(ULong.MAX_VALUE, creator.fromULong(ULong.MAX_VALUE).ulongValue(true))

        assertFailsWith<ArithmeticException> { twoTo63.longValue(true) }
        assertFailsWith<ArithmeticException> { twoTo64.ulongValue(true) }
    }

    @Test
    fun testExponentiationEdgeCases() {
        val modSeven = ModularBigInteger.creatorForModulo(7)
        assertEquals(modSeven.fromInt(4), modSeven.fromInt(3).pow(-2))
        assertEquals(modSeven.fromInt(4), modSeven.fromInt(3).pow(-2L))
        assertEquals(modSeven.fromInt(4), modSeven.fromInt(3).pow(BigInteger.fromInt(-2)))
        assertEquals(modSeven.ONE, modSeven.fromInt(3).pow(0))

        val modEight = ModularBigInteger.creatorForModulo(8)
        assertFailsWith<ArithmeticException> { modEight.fromInt(2).pow(-1) }

        val modPrime = ModularBigInteger.creatorForModulo(1000000007)
        assertEquals(modPrime.fromInt(264444359), modPrime.fromInt(2).pow(1000000000000000L))
    }

    @Test
    fun testEqualsDoesNotThrow() {
        val three = ModularBigInteger.creatorForModulo(7).fromInt(3)
        assertFalse(three == ModularBigInteger.creatorForModulo(11).fromInt(3))
        assertFalse(three.equals("3"))
        assertEquals(three, ModularBigInteger.creatorForModulo(7).fromInt(10))
    }
}
