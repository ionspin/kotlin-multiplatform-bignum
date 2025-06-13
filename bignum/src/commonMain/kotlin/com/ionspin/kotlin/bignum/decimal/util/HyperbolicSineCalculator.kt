package com.ionspin.kotlin.bignum.decimal.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode

object HyperbolicSineCalculator : SeriesCalculator() {
    override fun createTermSequence(x: BigDecimal, decimalMode: DecimalMode): Sequence<BigDecimal> {
        // x, x^3, x^5, x^7, ...
        val powerSequence = createAllPowersSequence(x, decimalMode)
            .filterIndexed { i, _ -> i % 2 != 0 }

        // 1/1!, 1/3!, 1/5!, 1/7!, ...
        val factorSequence = createAllFactorialsSequence()
            .filterIndexed { i, _ -> i % 2 != 0 }
            .map { n -> BigDecimal.ONE.divide(n, decimalMode) }

        return powerSequence.zip(factorSequence) { a, b -> a.multiply(b, decimalMode) }
    }
}