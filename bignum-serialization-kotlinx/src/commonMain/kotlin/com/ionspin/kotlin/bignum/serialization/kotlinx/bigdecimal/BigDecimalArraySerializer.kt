package com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerArraySerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.SerializersModule

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Jul-2021
 */
object BigDecimalArraySerializer : KSerializer<BigDecimal> {



    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BigDecimalArray") {
        element("significand", BigIntegerArraySerializer.descriptor)
        element<Long>("exponent")
//        element("decimalMode", isOptional = true) TODO
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, BigIntegerArraySerializer, value.significand)
            encodeLongElement(descriptor, 1, value.exponent)
        }
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return decoder.decodeStructure(descriptor) {
            val significand = decodeSerializableElement(descriptor, 0, BigIntegerArraySerializer)
            val exponent = decodeLongElement(descriptor, 1)
            BigDecimal.fromBigIntegerWithExponent(significand, exponent)
        }
    }


}

val bigDecimalArraySerializer = SerializersModule {
    contextual(BigDecimal::class, BigDecimalArraySerializer)
}
