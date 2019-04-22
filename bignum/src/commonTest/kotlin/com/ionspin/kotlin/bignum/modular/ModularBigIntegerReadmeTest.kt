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

import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Apr-2019
 */
@ExperimentalUnsignedTypes
class ModularBigIntegerReadmeTest {

    @Test
    fun createModularBigInteger() {
        val creator = ModularBigInteger.creatorForModulo(100)
        val modularBigInteger = creator.fromLong(150)
        println("ModularBigInteger: ${modularBigInteger.toStringWithModulo()}")
        assertTrue {
            modularBigInteger.residue == 50.toBigInteger()
        }
    }
}