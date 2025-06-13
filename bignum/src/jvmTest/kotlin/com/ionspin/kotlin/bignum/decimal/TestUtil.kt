package com.ionspin.kotlin.bignum.decimal

import kotlin.random.Random
import kotlin.test.fail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking

fun multipleTestLauncher(test: (random: Random) -> Unit) {
    val dispatcher = newFixedThreadPoolContext(32, "Division and rounds")
    val scope = CoroutineScope(dispatcher)
    val seed = 1
    val random = Random(seed)
    val jobList: MutableList<Job> = mutableListOf()
    val modes = java.math.RoundingMode.values().filterNot { it == java.math.RoundingMode.UNNECESSARY }
    for (i in 1..1_000_000) {
        jobList.add(
            scope.launch {
                test(random)
            }
        )
    }
    runBlocking {
        jobList.forEach {
            if (it.isCancelled) {
                fail("Some of the tests failed")
            }
            it.join()
        }
    }
}
