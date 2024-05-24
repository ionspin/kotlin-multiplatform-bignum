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
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Apr-2019
 */

class ModularBigIntegerAddition {
    @Test
    fun testAddition() {
        val creator = ModularBigInteger.creatorForModulo(10)
        val a = creator.fromInt(5)
        val b = creator.fromInt(7)
        val expected = creator.fromInt(2)
        testSingleAddition(a, b, expected)
    }

    fun testSingleAddition(a: ModularBigInteger, b: ModularBigInteger, expected: ModularBigInteger) {
        assertTrue {
            val c = a + b
            c == expected
        }
    }

    @Test
    fun testInverse() {
        val creator = ModularBigInteger.creatorForModulo(13)
        val a = creator.fromInt(7)
        assertTrue { -a == creator.fromInt(6) }
        assertTrue { a.negate() + a == creator.ZERO }
    }
}
