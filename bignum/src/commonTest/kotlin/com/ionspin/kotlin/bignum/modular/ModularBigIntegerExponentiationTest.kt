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

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 28-Jul-2019
 */

class ModularBigIntegerExponentiationTest {
    @Test
    fun testExponentiation() {
        val base = 5
        val modulus = 100
        val exponent = 3
        val creator = ModularBigInteger.creatorForModulo(modulus)
        val result = creator.fromInt(base).pow(exponent)
        assertEquals(result, creator.fromInt(25))
    }
}
