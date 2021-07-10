package com.ionspin.kotlin.bignum.serialization.kotlinx

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 11-Jul-2021
 */
class QuickSetupTest {

    val json = Json {
        serializersModule = arrayBasedSerializerModule // or humanReadableSerializerModule
    }

    @Serializable
    data class SomeDataHolder(@Contextual val bigInteger: BigInteger, @Contextual val bigDecimal: BigDecimal)

    @Test
    fun serializeAndDeserialize() {
        val bigInt = BigInteger.parseString("12345678901234567890")
        val bigDecimal = BigDecimal.parseString("1.234E-200")
        val someData = SomeDataHolder(bigInt, bigDecimal)
        val serialized = json.encodeToString(someData)
        println(serialized)
        val deserialized = json.decodeFromString<SomeDataHolder>(serialized)
        assertEquals(someData, deserialized)
    }
}
