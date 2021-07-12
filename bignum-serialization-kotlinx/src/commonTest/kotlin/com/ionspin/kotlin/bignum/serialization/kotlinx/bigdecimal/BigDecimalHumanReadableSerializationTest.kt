package com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
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
 * on 04-Jul-2021
 */
class BigDecimalHumanReadableSerializationTest {

    @Test
    fun testSerialization() {
        val testBigDecimal = BigDecimal.parseString("1.000000000020000000000300000000004")
        val json = Json {
            serializersModule = bigDecimalHumanReadableSerializerModule
        }
        val serialized = json.encodeToString(testBigDecimal)
        println(serialized)
        val deserialized = json.decodeFromString<BigDecimal>(serialized)
        assertEquals(testBigDecimal, deserialized)
    }

    @Serializable
    data class BigDecimalHumanReadableTestData(@Contextual val a : BigDecimal, @Contextual val b : BigDecimal)

    @Test
    fun testSomething() {
        val a = BigDecimal.parseString("1.000000000020000000000300000000004")
        val b = BigDecimal.parseString("-1.000000000020000000000300000000004")
        val testObject = BigDecimalHumanReadableTestData(a, b)
        val json = Json {
            serializersModule = bigDecimalHumanReadableSerializerModule
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)

    }

}
