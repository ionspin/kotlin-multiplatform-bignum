package com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.arrayBigIntegerSerializer
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
class BigDecimalArraySerializationTest {

    @Test
    fun testSerialization() {
        val testBigDecimal = BigDecimal.parseString("1.000000000020000000000300000000004")
        val json = Json {
            serializersModule = bigDecimalArraySerializer
        }
        val serialized = json.encodeToString(testBigDecimal)
        println(serialized)
        val deserialized = json.decodeFromString<BigDecimal>(serialized)
        assertEquals(testBigDecimal, deserialized)
    }
}
