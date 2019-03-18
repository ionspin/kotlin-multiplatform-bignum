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

package com.ionspin.kotlin.biginteger.base63

import java.math.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
fun ULongArray.toJavaBigInteger(): BigInteger {
    return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
        acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 63))

    }
}

@UseExperimental(ExperimentalUnsignedTypes::class)
fun com.ionspin.kotlin.biginteger.BigInteger.toJavaBigInteger() : BigInteger {
    return (this.magnitude.toJavaBigInteger() * this.sign.toInt().toBigInteger())
}