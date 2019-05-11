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

package com.ionspin.kotlin.bignum.decimal

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-May-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalDivPowTest {

    @Test
    fun testIntegerDivision() {
        val dividend = 4.123.toBigDecimal()
        val divisor = 2.toBigDecimal()
        val (quotient, remainder) = dividend divrem divisor
        assertTrue{
            quotient == 2.toBigDecimal() && remainder == 0.123.toBigDecimal()
        }
    }

//    @Test //TODO in 0.3.0
//    fun testDecimalExponentiation() {
//        val exponent = 2.123.toBigDecimal()
//        val a = 2.toBigDecimal()
//        val result = a.pow(exponent)
//    }



}