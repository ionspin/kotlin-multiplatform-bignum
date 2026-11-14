package com.ionspin.kotlin.bignum.decimal.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode

object HyperbolicCosineCalculator : SeriesCalculator() {
    override fun createTermSequence(x: BigDecimal, decimalMode: DecimalMode): Sequence<BigDecimal> {
        // 1, x^2, x^4, x^6, ...
        val powerSequence = createAllPowersSequence(x, decimalMode)
            .filterIndexed { i, _ -> i % 2 == 0 }

        // 1/0!, 1/2!, 1/4!, 1/6!, ...
        val factorSequence = createAllFactorialsSequence()
            .filterIndexed { i, _ -> i % 2 == 0 }
            .map { n -> BigDecimal.ONE.divide(n, decimalMode) }

        return powerSequence.zip(factorSequence) { a, b -> a.multiply(b, decimalMode) }
    }
}