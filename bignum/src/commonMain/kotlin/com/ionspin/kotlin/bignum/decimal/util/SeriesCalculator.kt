package com.ionspin.kotlin.bignum.decimal.util

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode

/**
 * Utility abstract class for implementing infinite series summations.
 *
 * Subclasses must ensure that the series actually converges (at least for
 * all values you plan to calculate.)
 */
abstract class SeriesCalculator {
    fun calculate(x: BigDecimal, decimalMode: DecimalMode): BigDecimal {
        val higherPrecisionMode = decimalMode.copy(decimalPrecision = (decimalMode.decimalPrecision * 1.1).toLong() + 2)
        val epsilon = BigDecimal.ONE.moveDecimalPoint(-higherPrecisionMode.decimalPrecision)

        return createTermSequence(x, higherPrecisionMode)
            .takeWhile { step -> step.abs() > epsilon }
            .fold(BigDecimal.ZERO) { acc, step -> acc.add(step) }
            .roundSignificand(decimalMode)
    }

    /**
     * Implemented by subclasses to create an appropriate sequence of terms for the series.
     */
    abstract fun createTermSequence(x: BigDecimal, decimalMode: DecimalMode): Sequence<BigDecimal>

    /**
     * Utility function for subclasses to use which produces a sequence of all powers of `x`,
     * starting at 0.
     */
    protected fun createAllPowersSequence(x: BigDecimal, decimalMode: DecimalMode): Sequence<BigDecimal> {
        return generateSequence(BigDecimal.ONE) { n -> n.multiply(x, decimalMode) }
    }

    /**
     * Utility function for subclasses to use which produces a sequence of all factorials,
     * starting at 0! = 1, then 1! = 1, 2! = 2, 3! = 6, etc.
     */
    protected fun createAllFactorialsSequence(): Sequence<BigDecimal> {
        return generateSequence(1) { n -> n + 1 }
            .runningFold(BigDecimal.ONE) { acc, n -> acc * n }
    }
}