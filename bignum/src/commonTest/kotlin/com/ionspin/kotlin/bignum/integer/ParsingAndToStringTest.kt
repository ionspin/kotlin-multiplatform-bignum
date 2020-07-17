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

import com.ionspin.kotlin.bignum.toProperType
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-2019
 */

class ParsingAndToStringTest {
    val seed = 1
    val random = Random(1)

    @Test
    fun testParsing() {
        parsingSingleTest("1234", 10)
    }

    fun parsingSingleTest(textNumber: String, base: Int) {
        assertTrue {
            val parsed = BigInteger.parseString(textNumber, base)
            val printed = parsed.toString(base)
            print(printed)

            textNumber == printed
        }
    }

    @Test
    fun toStringTest() {
        val bigInt = ulongArrayOf(
            2357127997678045786UL,
            9223372036854775806UL,
            6618565566930092031UL,
            3482571707102756671UL,
            5561215897725336065UL,
            7121810967379087079UL,
            2244066548420960617UL,
            2014538293531722329UL,
            492133570377UL,
            0UL
        )
        val parsed = BigInteger.fromWordArray(bigInt.toProperType(), Sign.POSITIVE)
        parsed.toString()
    }
}
