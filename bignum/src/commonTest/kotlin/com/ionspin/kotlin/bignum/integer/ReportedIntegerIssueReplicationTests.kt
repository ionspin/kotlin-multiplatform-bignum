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

    @Test
    fun modInverseOfNegativeTest() {
        assertEquals(6.toBigInteger(), (-1).toBigInteger().modInverse(7.toBigInteger()))
        assertEquals(6.toBigInteger(), (-8).toBigInteger().modInverse(7.toBigInteger()))
        assertEquals(2.toBigInteger(), (-3).toBigInteger().modInverse(7.toBigInteger()))
        for (modulo in listOf(2, 3, 7, 9, 16, 35, 97)) {
            for (a in -200..200) {
                val value = a.toBigInteger()
                val bigModulo = modulo.toBigInteger()
                if (value.gcd(bigModulo) != BigInteger.ONE) continue
                val inverse = value.modInverse(bigModulo)
                assertEquals(BigInteger.ONE, (value * inverse).mod(bigModulo), "$a mod $modulo")
                assertEquals(true, inverse >= 0 && inverse < bigModulo, "$a mod $modulo range")
            }
        }
    }

    @Test
    fun zeroToThePowerOfZeroTest() {
        assertEquals(BigInteger.ONE, BigInteger.ZERO.pow(0))
        assertEquals(BigInteger.ONE, BigInteger.ZERO.pow(0L))
        assertEquals(BigInteger.ONE, BigInteger.ZERO.pow(BigInteger.ZERO))
        assertEquals(BigInteger.ZERO, BigInteger.ZERO.pow(1))
        assertEquals(BigInteger.ZERO, BigInteger.ZERO.pow(5))
        assertEquals(BigInteger.ONE, 7.toBigInteger().pow(0))
        assertEquals(BigInteger.ONE, (-7).toBigInteger().pow(0))
    }
}
