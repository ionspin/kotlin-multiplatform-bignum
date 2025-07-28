package com.ionspin.kotlin.bignum

import java.math.BigDecimal
import com.ionspin.kotlin.bignum.decimal.BigDecimal as KotlinBigDecimal

fun BigDecimal.toKotlinBigDecimal(): KotlinBigDecimal {
    return KotlinBigDecimal.parseString(this.toPlainString())
}
