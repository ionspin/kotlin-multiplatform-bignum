package com.ionspin.kotlin.bignum.integer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReportedIntegerIssueReplicationTests {

    @Test
    fun github324NegativeShiftTest() {
        assertEquals((-4194304).toBigInteger(), (-8388608).toBigInteger().shl(-1))
        assertEquals(8.toBigInteger(), 8388608.toBigInteger().shl(-20))
        assertEquals(8.toBigInteger(), 512.toBigInteger().shl(-6))
        assertEquals(28.toBigInteger(), 2078458800722539264L.toBigInteger().shl(-56))
        assertEquals(168.toBigInteger(), 42.toBigInteger().shr(-2))
        assertEquals(BigInteger.ZERO, 42.toBigInteger().shl(Int.MIN_VALUE))
    }

    @Test
    fun sqrtOfZeroAndNegativeTest() {
        assertEquals(BigInteger.ZERO, BigInteger.ZERO.sqrt())
        assertEquals(
            BigInteger.SqareRootAndRemainder(BigInteger.ZERO, BigInteger.ZERO),
            BigInteger.ZERO.sqrtAndRemainder()
        )
        assertFailsWith<ArithmeticException> { (-4).toBigInteger().sqrt() }
        assertFailsWith<ArithmeticException> { (-4).toBigInteger().sqrtAndRemainder() }
    }
}
