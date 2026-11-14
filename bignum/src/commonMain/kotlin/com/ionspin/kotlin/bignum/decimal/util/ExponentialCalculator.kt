package com.ionspin.kotlin.bignum.decimal.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode

object ExponentialCalculator : SeriesCalculator() {
    override fun createTermSequence(x: BigDecimal, decimalMode: DecimalMode): Sequence<BigDecimal> {
        // 1, x, x^2, x^3, ...
        val powerSequence = createAllPowersSequence(x, decimalMode)

        // 1/0!, 1/1!, 1/2!, 1/3!, ...
        val factorSequence = createAllFactorialsSequence()
            .map { n -> BigDecimal.ONE.divide(n, decimalMode) }

        return powerSequence.zip(factorSequence) { a, b -> a.multiply(b, decimalMode) }
    }
}