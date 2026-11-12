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

    @Test
    fun remainderSignFollowsDividendTest() {
        assertEquals(1.toBigInteger(), 7.toBigInteger() % 2.toBigInteger())
        assertEquals(1.toBigInteger(), 7.toBigInteger() % (-2).toBigInteger())
        assertEquals((-1).toBigInteger(), (-7).toBigInteger() % 2.toBigInteger())
        assertEquals((-1).toBigInteger(), (-7).toBigInteger() % (-2).toBigInteger())
        assertEquals(BigInteger.ZERO, (-8).toBigInteger() % (-2).toBigInteger())
        assertEquals(BigInteger.ZERO, BigInteger.ZERO % (-2).toBigInteger())
        for (a in -20..20) {
            for (b in listOf(-7, -3, -1, 1, 3, 7)) {
                assertEquals((a % b).toBigInteger(), a.toBigInteger() % b.toBigInteger(), "$a % $b")
                assertEquals(
                    (a.toBigInteger() divrem b.toBigInteger()).remainder,
                    a.toBigInteger() % b.toBigInteger(),
                    "$a divrem $b"
                )
            }
        }
    }
}
