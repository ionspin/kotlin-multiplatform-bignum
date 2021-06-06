package com.ionspin.kotlin.bignum.serializer

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object BigIntegerSerializer : KSerializer<BigInteger> {
    private val serializer = PairSerializer(String.serializer(), ByteArraySerializer())
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: BigInteger) {
        serializer.serialize(encoder, value.sign.name to value.toByteArray())
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        val (signString, byteArray) = serializer.deserialize(decoder)
        val sign = Sign.valueOf(signString)
        return BigInteger.fromByteArray(byteArray, sign)
    }
}