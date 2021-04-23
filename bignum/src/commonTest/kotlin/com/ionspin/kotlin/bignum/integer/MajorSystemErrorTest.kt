package com.ionspin.kotlin.bignum.integer

import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * Procrastinating
 */
class MajorSystemErrorTest {
    private fun youThink(block: () -> Boolean) = assertFalse(block = block)

    private infix fun BigInteger.makes(second: BigInteger): Boolean = this == second
    private infix fun BigInteger.plus(second: BigInteger): BigInteger = this.plus(second)

    private val ONE = BigInteger.ONE
    private val SEVENSEVENSEVEN = 777.toBigInteger()
    private val TWO = BigInteger.TWO

    @Test
    fun canISeeAMajorSystemErrorInYou() {
            youThink {
                ONE plus SEVENSEVENSEVEN makes TWO
            }
        }
}
