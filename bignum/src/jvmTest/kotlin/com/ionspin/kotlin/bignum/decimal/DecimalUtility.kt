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

import com.ionspin.kotlin.bignum.integer.base63.toJavaBigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
fun BigDecimal.toJavaBigDecimal() : java.math.BigDecimal {
//    println("Expanded ${this.toStringExpanded()}")
    if (exponent > 0) {
    return java.math.BigDecimal(this.significand.toJavaBigInteger(), (this.significand.numberOfDigits() - this.exponent.magnitude[0].toInt() - 1))
    } else {
        return java.math.BigDecimal(this.significand.toJavaBigInteger(), (this.significand.numberOfDigits() + this.exponent.magnitude[0].toInt() - 1) )
    }
//    return java.math.BigDecimal(this.toStringExpanded())
}