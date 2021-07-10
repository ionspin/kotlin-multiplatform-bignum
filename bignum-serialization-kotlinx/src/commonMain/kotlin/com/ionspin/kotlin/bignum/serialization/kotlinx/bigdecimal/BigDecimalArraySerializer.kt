package com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerArraySerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
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
        element("decimalMode", DecimalModeSerializer.descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, BigIntegerArraySerializer, value.significand)
            encodeLongElement(descriptor, 1, value.exponent)
            value.decimalMode?.let {
                encodeSerializableElement(descriptor, 2, DecimalModeSerializer, value.decimalMode!!)
            }

        }
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return decoder.decodeStructure(descriptor) {
            var significand = BigInteger.ZERO
            var exponent = 0L
            var decimalMode : DecimalMode? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> significand = decodeSerializableElement(descriptor, 0, BigIntegerArraySerializer)
                    1 -> exponent = decodeLongElement(descriptor, 1)
                    2 -> decimalMode = decodeSerializableElement(descriptor, 2, DecimalModeSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            BigDecimal.fromBigIntegerWithExponent(significand, exponent, decimalMode)
        }
    }


}

object DecimalModeSerializer : KSerializer<DecimalMode> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DecimalMode") {
        element<Long>("decimalPrecision")
        element<String>("roundingMode")
        element<Long>("scale")
    }

    override fun serialize(encoder: Encoder, value: DecimalMode) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.decimalPrecision)
            encodeStringElement(descriptor, 1, value.roundingMode.name)
            encodeLongElement(descriptor, 2, value.scale)
        }
    }

    override fun deserialize(decoder: Decoder): DecimalMode {
        return decoder.decodeStructure(descriptor) {
            var decimalPrecision = -1L
            var roundingModeString = ""
            var scale = -1L
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> decimalPrecision = decodeLongElement(descriptor, 0)
                    1 -> roundingModeString = decodeStringElement(descriptor, 1)
                    2 -> scale = decodeLongElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            DecimalMode(decimalPrecision, RoundingMode.valueOf(roundingModeString), scale)
        }
    }


}

val bigDecimalArraySerializer = SerializersModule {
    contextual(BigDecimal::class, BigDecimalArraySerializer)
    contextual(DecimalMode::class, DecimalModeSerializer)
}
