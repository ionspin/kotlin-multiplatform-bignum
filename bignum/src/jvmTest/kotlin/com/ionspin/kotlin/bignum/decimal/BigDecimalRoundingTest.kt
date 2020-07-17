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
 * on 30-Mar-2019
 */

class BigDecimalRoundingTest {
    @Test
    fun testRoundingToFloor() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }

    @Test
    fun testRoundingToCeiling() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
    }

    @Test
    fun testRoundingTowardsZero() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
    }

    @Test
    fun testRoundingAwayFromZero() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }

    //
    //
    //
    // --------------------------------- Rounding to nearest int -----------------------------
    //
    //
    //

    @Test
    fun testRoundingHalfToFloor() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_FLOOR))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }

    @Test
    fun testRoundingHalfToCeiling() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_CEILING))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }

    @Test
    fun testRoundingHalfTowardsZero() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_TOWARDS_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }

    @Test
    fun testRoundingHalfAwayFromZero() {

        // Positive
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(1)
            rounded == expected
        }
        // Around zero
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.8)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(0.2)
            rounded == expected
        }

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-2, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-5, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.5)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-8, -1)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromDouble(-0.8)
            rounded == expected
        }

        // Negative

        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-12, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-1)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-15, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
        assertTrue {
            val unrounded = BigDecimal.fromIntWithExponent(-18, 0)
            val rounded = unrounded.roundSignificand(DecimalMode(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            val expected = BigDecimal.fromIntAsSignificand(-2)
            rounded == expected
        }
    }
}
