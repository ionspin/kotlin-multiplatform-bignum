package com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
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
 *
 * Serializes big integer into base 10 human readable format.
 */
@Serializer(forClass = BigInteger::class)
object HumanReadableSerializer : KSerializer<BigInteger> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toString(10))
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger.parseString(decoder.decodeString(), 10)
    }

}

val humanReadableBigIntegerSerializer = SerializersModule {
    contextual(BigInteger::class, HumanReadableSerializer)
}
