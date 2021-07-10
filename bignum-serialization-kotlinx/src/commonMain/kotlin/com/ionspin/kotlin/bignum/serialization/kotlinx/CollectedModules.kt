package com.ionspin.kotlin.bignum.serialization.kotlinx

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal.BigDecimalArraySerializer
import com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal.BigDecimalHumanReadableSerializer
import com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal.DecimalModeSerializer
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerArraySerializer
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerHumanReadableSerializer
import kotlinx.serialization.modules.SerializersModule

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 11-Jul-2021
 */
val humanReadableSerializerModule = SerializersModule {
    contextual(BigInteger::class, BigIntegerHumanReadableSerializer)
    contextual(BigDecimal::class, BigDecimalHumanReadableSerializer)
    contextual(DecimalMode::class, DecimalModeSerializer)
}

val arrayBasedSerializerModule = SerializersModule {
    contextual(BigInteger::class, BigIntegerArraySerializer)
    contextual(BigDecimal::class, BigDecimalArraySerializer)
    contextual(DecimalMode::class, DecimalModeSerializer)
}
