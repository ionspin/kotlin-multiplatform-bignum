package com.ionspin.kotlin.bignum.integer

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import platform.posix.pthread_self

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 07-Mar-2021
 */
@ExperimentalCoroutinesApi
class ConcurrencyTest {

    /**
     * Sanity test that we are in multi-threaded coroutines env (...-native-mt)
     */
    @Test
    fun testDifferentThreads() = runTest {
        val differentThreadScope = CoroutineScope(newSingleThreadContext("DifferentThread"))
        val testTid = pthread_self()
        println("Test thread id $testTid")
        val differentThreadAsync = differentThreadScope.async {
            val differentTid = pthread_self()
            println("Different thread id $differentTid")
            differentTid
        }
        val different = differentThreadAsync.await()
        println("Test tid $testTid, different tid $different")
        assertTrue { testTid != different }
    }

    @Test
    fun testSharedAccess() = runTest {
        val differentThreadScope = CoroutineScope(newSingleThreadContext("DifferentThread"))
        val originalBigInt = ULong.MAX_VALUE.toBigInteger()
        val ubyteArray = originalBigInt.toUByteArray()
        val reconstructedAsync = differentThreadScope.async {
            BigInteger.fromUByteArray(ubyteArray, Sign.POSITIVE)
        }
        val reconstructedBigInt = reconstructedAsync.await()
        assertTrue { reconstructedBigInt == originalBigInt }
    }
}
