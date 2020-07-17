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
 * on 15-Feb-2020
 */

class ReportedIssueReplicationTest {

    /**
     * Negative figures round gives incorrect result
     * https://github.com/ionspin/kotlin-multiplatform-bignum/issues/90
     */
    @Test
    fun github90NegativeRounding() {

        assertTrue {
            val a = (3.19).toBigDecimal() * 0.00001.toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (0.00).toBigDecimal()
        }

        assertTrue {
            val a = (0.005).toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (0.01).toBigDecimal()
        }

        assertTrue {
            val a = (-3.19).toBigDecimal() * 0.00001.toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (-0.00).toBigDecimal()
        }

        assertTrue {
            val a = (-0.005).toBigDecimal()
            val b = a.roundToDigitPosition(3, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)

            b == (-0.01).toBigDecimal()
        }
    }
}
