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
     * Round towards nearest integer, using away from zero as tie breaker when significant digit being rounded is 5
     */
    ROUND_HALF_AWAY_FROM_ZERO,
    /**
     * Round towards nearest integer, using towards zero as tie breaker when significant digit being rounded is 5
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
    /**
     * Round towards nearest even integer
     */
    ROUND_HALF_TO_EVEN,
    /**
     * Round towards neares odd integer
     */
    ROUND_HALF_TO_ODD
}

/**
 * Decimal precision signifies how many digits will significand have. If decimal precision is 0 and RoundingMode is NONE
 * infinite precision is used.
 * @param decimalPrecision max number of digits allowed. Default 0 is unlimited precision.
 * @param roundingMode default RoundingMode.NONE is used with unlimited precision and no specified scale.
 * Otherwise specify mode that is used for rounding when decimalPrecision is exceeded, or when scale is in use.
 * @param scale is number of digits to the right of the decimal point.
 * When this is specified, a RoundingMode that is not RoundingMode.NONE is also required.
 * Scale cannot be greater than precision - 1.
 * If left to default = null, no scale will be used. Rounding and decimalPrecision apply.
 * Negative scale numbers are not supported.
 * Using scale will increase the precision to required number of digits.
 */
data class DecimalMode(
    val decimalPrecision: Long = 0,
    val roundingMode: RoundingMode = RoundingMode.NONE,
    val scale: Long = -1
) {

    val isPrecisionUnlimited = decimalPrecision == 0L
    val usingScale = scale >= 0

    init {
        if (decimalPrecision == 0L && roundingMode != RoundingMode.NONE) {
            throw ArithmeticException("Rounding mode with 0 digits precision.")
        }
        if (scale < -1) {
            throw ArithmeticException("Negative Scale is unsupported.")
        }
        if (usingScale && roundingMode == RoundingMode.NONE) {
            throw ArithmeticException("Scale of $scale digits to the right of the decimal requires a RoundingMode that is not NONE.")
        }
    }

    companion object {
        /**
         * Default decimal mode, infinite precision, no rounding
         */
        val DEFAULT = DecimalMode()

        /**
         * Example mode useful for US currency support with unlimited dollars, and no fractions of cents.
         * Note that prices, interest rate calculations, and lots of other usages of currency require
         * fractions of cents for accuracy, which requires a larger scale.
         * Arbitrarily chose really large precision
         */
        val US_CURRENCY = DecimalMode(30, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO, 2)
    }
}
