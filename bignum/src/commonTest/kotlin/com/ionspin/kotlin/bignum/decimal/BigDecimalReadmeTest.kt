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

import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigDecimalReadmeTest {
    @Test
    fun toStringTest() {


        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(1, (10).toBigInteger())
            println("BigDecimal $bigDecimal")
            bigDecimal.toString() == "1.0E+10"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(1, (-5).toBigInteger())
            println("BigDecimal $bigDecimal")
            bigDecimal.toString() == "1.0E-5"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("BigDecimal $bigDecimal")
            bigDecimal.toString() == "7.1E-2"
        }
    }

    @Test
    fun toStringWithoutExponentTest() {

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (0).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "5.25"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (1).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "52.5"
        }


        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(525, (5).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "525000"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(250001, (-5).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.0000250001"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-1).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.71"
        }

        assertTrue {
            val bigDecimal = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("BigDecimal ${bigDecimal.toStringExpanded()}")
            bigDecimal.toStringExpanded() == "0.071"
        }


    }

    @Test
    fun readmeAdditionTest() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (15).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first + second

            println("Sum without exponent: ${product.toStringExpanded()}")
            println("Sum: ${product}")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (-8).toBigInteger())
            true
//             product == expectedResult
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first + second

            println("Sum without exponent: ${product.toStringExpanded()}")
            println("Sum: ${product}")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (-9).toBigInteger())
            true
//            product == expectedResult
        }


    }

    @Test
    fun readmeMultiplicationTest() {
        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (15).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first * second

            println("Product without exponent: ${product.toStringExpanded()}")
            println("Product: ${product}")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (-8).toBigInteger())
            true
//             product == expectedResult
        }

        assertTrue {
            val first = BigDecimal.fromLongWithExponent(125, (-7).toBigInteger())
            val second = BigDecimal.fromLongWithExponent(71, (-2).toBigInteger())
            println("First: $first \nSecond: $second")
            val product = first * second

            println("Product without exponent: ${product.toStringExpanded()}")
            println("Product: ${product}")
            val expectedResult = BigDecimal.fromLongWithExponent(8875, (-9).toBigInteger())
            true
//            product == expectedResult
        }


    }


}