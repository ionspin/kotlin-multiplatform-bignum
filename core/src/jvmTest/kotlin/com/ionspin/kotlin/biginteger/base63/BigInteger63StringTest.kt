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

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
class BigInteger64StringTest {

    @Test
    fun testToString() {
        val a = ulongArrayOf(1UL, 1UL)
        toStringSingleTest(a)
    }



    fun toStringSingleTest(ulongArray : ULongArray) {
        assertTrue {
            val result = BigInteger63Arithmetic.toString(ulongArray, 10)
            val bigIntResult = ulongArray.toJavaBigInteger().toString()
            println("Result $result \nBigInt $bigIntResult")
            result == bigIntResult
        }


    }
}