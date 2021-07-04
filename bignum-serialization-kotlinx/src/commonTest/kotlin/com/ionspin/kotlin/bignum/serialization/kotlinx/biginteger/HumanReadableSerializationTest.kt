package com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.humanReadableBigIntegerSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Jul-2021
 */
class HumanReadableSerializationTest {

    @Test
    fun testSerialization() {
        val testBigInteger = BigInteger.parseString("1000000000000000000000000000002000000000000000000000000000003")
        val json = Json {
            serializersModule = humanReadableBigIntegerSerializer
        }
        val serialized = json.encodeToString(testBigInteger)
        println(serialized)
        val deserialized = json.decodeFromString<BigInteger>(serialized)
        assertEquals(testBigInteger, deserialized)
    }

}
