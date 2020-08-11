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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */

enum class RoundingMode {
    /**
     * Towards negative infinity
     */
    FLOOR,
    /**
     * Towards positive infinity
     */
    CEILING,
    /**
     * Away from zero
     */
    AWAY_FROM_ZERO,
    /**
     * Towards zero
     */
    TOWARDS_ZERO,
    /**
     * Infinite decimalPrecision, and beyond
     */
    NONE,
    /**
     * Round towards nearest integer, using towards zero as tie breaker when significant digit being rounded is 5
     */
    ROUND_HALF_AWAY_FROM_ZERO,
    /**
     * Round towards nearest integer, using away from zero as tie breaker when significant digit being rounded is 5
     */
    ROUND_HALF_TOWARDS_ZERO,
    /**
     * Round towards nearest integer, using towards infinity as tie breaker when significant digit being rounded is 5
     */
    ROUND_HALF_CEILING,
    /**
     * Round towards nearest integer, using towards negative infinity as tie breaker when significant digit being rounded is 5
     */
    ROUND_HALF_FLOOR,
    // For future releases
//    /**
//     * Round towards nearest even integer
//     */
//    ROUND_HALF_TO_EVEN,
//    /**
//     * Round towards neares odd integer
//     */
//    ROUND_HALF_TO_ODD
}

/**
 * Decimal precision signifies how many digits will significand have. If decimal precision is 0 and RoundingMode is NONE
 * infinite precision is used
 */
data class DecimalMode(val decimalPrecision: Long = 0, val roundingMode: RoundingMode = RoundingMode.NONE) {

    val isPrecisionUnlimited = decimalPrecision == 0L

    init {
        if (decimalPrecision == 0L && roundingMode != RoundingMode.NONE) {
            throw ArithmeticException("Rounding mode with 0 digits precision.")
        }
    }

    companion object {
        /**
         * Default decimal mode, infinite precision, no rounding
         */
        val DEFAULT = DecimalMode()
    }
}
