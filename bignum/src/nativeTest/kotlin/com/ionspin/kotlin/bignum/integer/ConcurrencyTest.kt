package com.ionspin.kotlin.bignum.integer

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
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
    fun testDifferentThreads() = runBlocking(Dispatchers.Default) {
        val mainScope = MainScope()
        val differentThreadScope = CoroutineScope(newSingleThreadContext("DifferentThread"))
        val testTid = pthread_self()
        println("Test thread id $testTid")
        val mainScopeAsync = mainScope.async {
            val mainTid = pthread_self()
            println("Main thread id $mainTid")
            mainTid
        }
        val differentThreadAsync = differentThreadScope.async {
            val differentTid = pthread_self()
            println("Different thread id $differentTid")
            differentTid
        }
        val main = mainScopeAsync.await()
        val different = differentThreadAsync.await()
        println("Main $main, different $different")
        assertTrue { main != different }
    }

    @Test
    fun testSharedAccess() = runBlocking(Dispatchers.Default) {
        val mainScope = MainScope()
        val differentThreadScope = CoroutineScope(newSingleThreadContext("DifferentThread"))
        val originalBigInt = ULong.MAX_VALUE.toBigInteger()
        val ubyteArrayAsync = mainScope.async {
            originalBigInt.toUByteArray()
        }
        val reconstructedAsync = differentThreadScope.async {
            BigInteger.fromUByteArray(ubyteArrayAsync.await(), Sign.POSITIVE)
        }
        val reconstructedBigInt = reconstructedAsync.await()
        assertTrue { reconstructedBigInt == originalBigInt }
    }
}
