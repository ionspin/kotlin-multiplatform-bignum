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

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking

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

    @Test
    fun randomTestDoubleDivisionAndRounding() {
        val dispatcher = newFixedThreadPoolContext(32, "Division and rounds")
        val scope = CoroutineScope(dispatcher)
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        val modes = java.math.RoundingMode.values().filterNot { it == java.math.RoundingMode.UNNECESSARY }
        for (i in 1..1_000_000) {
            jobList.add(
                scope.launch {
                    testDivisionAndRounding(random.nextDouble(), random.nextDouble(), random.nextInt(100), modes.random())
                }
            )
        }
        runBlocking {
            jobList.forEach {
                if (it.isCancelled) {
                    fail("Some of the tests failed")
                }
                it.join()
            }
        }
    }

    fun testDivisionAndRounding(a: Double, b: Double, scale: Int, jvmRoundingMode: java.math.RoundingMode) {
        val bigNumA = a.toBigDecimal()
        val bigNumB = b.toBigDecimal()
        val bigNumRoundingMode = jvmRoundingMode.toBigNumRoundingMode()
        val jvmA = bigNumA.toJavaBigDecimal()
        val jvmB = bigNumB.toJavaBigDecimal()
        val jvmResult = jvmA.divide(jvmB, scale, jvmRoundingMode)
        val bigNumResult = bigNumA.divide(bigNumB, DecimalMode(scale + 100L, bigNumRoundingMode, scale.toLong()))
        assertEquals(bigNumResult.toPlainString(), jvmResult.toPlainString())
    }

    fun RoundingMode.toJvmRoundingMode(): java.math.RoundingMode {
        return when (this) {
            RoundingMode.AWAY_FROM_ZERO -> java.math.RoundingMode.UP
            RoundingMode.TOWARDS_ZERO -> java.math.RoundingMode.DOWN
            RoundingMode.CEILING -> java.math.RoundingMode.CEILING
            RoundingMode.FLOOR -> java.math.RoundingMode.FLOOR
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO -> java.math.RoundingMode.HALF_UP
            RoundingMode.ROUND_HALF_TOWARDS_ZERO -> java.math.RoundingMode.HALF_DOWN
            RoundingMode.ROUND_HALF_TO_EVEN -> java.math.RoundingMode.HALF_EVEN
            RoundingMode.NONE -> java.math.RoundingMode.UNNECESSARY
            RoundingMode.ROUND_HALF_CEILING -> TODO()
            RoundingMode.ROUND_HALF_FLOOR -> TODO()
            RoundingMode.ROUND_HALF_TO_ODD -> TODO()
        }
    }

    fun java.math.RoundingMode.toBigNumRoundingMode(): RoundingMode {
        return when (this) {
            java.math.RoundingMode.UP -> RoundingMode.AWAY_FROM_ZERO
            java.math.RoundingMode.DOWN -> RoundingMode.TOWARDS_ZERO
            java.math.RoundingMode.CEILING -> RoundingMode.CEILING
            java.math.RoundingMode.FLOOR -> RoundingMode.FLOOR
            java.math.RoundingMode.HALF_UP -> RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
            java.math.RoundingMode.HALF_DOWN -> RoundingMode.ROUND_HALF_TOWARDS_ZERO
            java.math.RoundingMode.HALF_EVEN -> RoundingMode.ROUND_HALF_TO_EVEN
            java.math.RoundingMode.UNNECESSARY -> RoundingMode.NONE
        }
    }

    @Test
    fun testZeroRemainder() {
        val modes = java.math.RoundingMode.values().filterNot { it == java.math.RoundingMode.UNNECESSARY }
        for (mode in modes) {
            val a = "1.1000000000"
            val bignum = a.toBigDecimal(decimalMode = DecimalMode(20, mode.toBigNumRoundingMode(), 3))
            val jvm = java.math.BigDecimal(a).setScale(3, mode)
            assertEquals(bignum.toPlainString(), jvm.toPlainString())
        }
    }
}
