package com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger

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
 * on 04-Jul-2021
 */
class ArraySerializationTest {

    @Test
    fun testSerialization() {
        run {
            val testBigInteger = BigInteger.parseString("1000000000000000000000000000002000000000000000000000000000003")
            val json = Json {
                serializersModule = arrayBigIntegerSerializer
            }
            val serialized = json.encodeToString(testBigInteger)
            println(serialized)
            val deserialized = json.decodeFromString<BigInteger>(serialized)
            assertEquals(testBigInteger, deserialized)
        }
        run {
            val testBigInteger = BigInteger.parseString("-1000000000000000000000000000002000000000000000000000000000003")
            val json = Json {
                serializersModule = arrayBigIntegerSerializer
            }
            val serialized = json.encodeToString(testBigInteger)
            println(serialized)
            val deserialized = json.decodeFromString<BigInteger>(serialized)
            assertEquals(testBigInteger, deserialized)
        }

    }

    @Serializable
    data class BigIntSerializtionTest(@Contextual val a : BigInteger, @Contextual val b : BigInteger)

    @Test
    fun testSomething() {
        val a = BigInteger.parseString("-1000000000000000000000000000002000000000000000000000000000003")
        val b = BigInteger.parseString("1000000000000000000000000000002000000000000000000000000000003")
        val testObject = BigIntSerializtionTest(a, b)
        val json = Json {
            serializersModule = arrayBigIntegerSerializer
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)

    }
}
