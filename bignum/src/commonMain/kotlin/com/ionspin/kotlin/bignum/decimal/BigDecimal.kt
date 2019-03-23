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

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-3/23/19
 */

@ExperimentalUnsignedTypes
class BigDecimal(val significand : BigInteger, val exponent : BigInteger, decimalMode: DecimalMode = DecimalMode()) {

    companion object {

    }

    val isExponentLong = exponent.numberOfWords == 0
    val longExponent = exponent.magnitude[0]

    fun plus() : BigDecimal {
        TODO()
    }
    fun minus() : BigDecimal {
        TODO()
    }
    fun multiply(other : BigDecimal, decimalMode: DecimalMode = DecimalMode()) : BigDecimal {
        val newExponent = exponent * other.exponent
        val newSignificand = this.significand * other.significand
        return if (decimalMode.roundingMode != RoundingMode.NONE) {
            BigDecimal(newSignificand, newExponent, decimalMode)
        } else {
            BigDecimal(newSignificand, newExponent)
        }

    }


    fun div() : BigDecimal {
        TODO()
    }
    fun mod() : BigDecimal {
        TODO()
    }
    fun rem() : BigDecimal {
        TODO()
    }
    fun unaryMinus() : BigDecimal {
        TODO()
    }
    fun inc() : BigDecimal {
        TODO()
    }
    fun dec() : BigDecimal {
        TODO()
    }

    fun abs() : BigDecimal {
        TODO()
    }

    fun negate() : BigDecimal {
        TODO()
    }

    fun pow() : BigDecimal {
        TODO()
    }

    private fun round() : BigDecimal {
        TODO()
    }






}