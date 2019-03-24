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

import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
class BigDecimalJvmTest {

    @Test
    fun bla() {
        val mathContext = MathContext(60, RoundingMode.CEILING)
        val mathContext2 = MathContext(65, RoundingMode.CEILING)
        val mathContext3 = MathContext(0, RoundingMode.UNNECESSARY)
        val bigDecimalJvm = BigDecimal("1000000000000000000000000000000000000000000000000000000000000000000000000000002", mathContext)
        val bigDecimalJvm2 = BigDecimal("1000000000000000000000000000000000000000000000000000000000000000000000000000002", mathContext2)
        val result = bigDecimalJvm + bigDecimalJvm2
        val blares = bigDecimalJvm.multiply(BigDecimal.valueOf(2), mathContext3)
        val blares2 = blares.inc()
        println("BigDecimalJvm: $bigDecimalJvm")
    }
}