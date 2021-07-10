package com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Jul-2021
 */
object BigDecimalHumanReadableSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString(10))
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal.parseString(decoder.decodeString(), 10)
    }

}

val bigDecimalHumanReadableSerializerModule = SerializersModule {
    contextual(BigDecimal::class, BigDecimalHumanReadableSerializer)
    contextual(DecimalMode::class, DecimalModeSerializer)
}
