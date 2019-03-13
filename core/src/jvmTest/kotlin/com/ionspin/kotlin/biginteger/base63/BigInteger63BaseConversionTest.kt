package com.ionspin.kotlin.biginteger.base63

import com.ionspin.kotlin.biginteger.base32.toJavaBigInteger
import com.ionspin.kotlin.biginteger.base63.BigInteger63Arithmetic.baseMask
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
class BigInteger63BaseConversionTest {

    @Test
    fun test63To64Conversion() {
        assertTrue {
            val a = ulongArrayOf(1UL, 1UL, 1UL)
            val b = BigInteger63Arithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()


            aBigInt == bBigInt
        }

        assertTrue {
            val a = ulongArrayOf(0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 0UL - 1UL, 1UL, 1UL)
            val b = BigInteger63Arithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()


            aBigInt == bBigInt
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val a = ULongArray(321) { random.nextULong() and baseMask }
            val b = BigInteger63Arithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()


            aBigInt == bBigInt
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val a = ULongArray(129) { (0UL - 1UL) shr 1 }
            val b = BigInteger63Arithmetic.convertTo64BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.fromBase64toJavaBigInteger()


            aBigInt == bBigInt
        }
    }

    @Test
    fun test63To32Conversion() {
        assertTrue {
            val a = ulongArrayOf(1UL, 1UL, 1UL)
            val b = BigInteger63Arithmetic.convertTo32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()


            aBigInt == bBigInt
        }
    }

    @Test
    fun test32To63Conversion() {
        assertTrue {
            val a = uintArrayOf(1U, 0U - 1U, 0U, 0U - 1U)
            val b = BigInteger63Arithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()


            aBigInt == bBigInt
        }

        assertTrue {
            val a = uintArrayOf(1U, 0U - 1U, 0U, 0U - 1U, 1U)
            val b = BigInteger63Arithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()


            aBigInt == bBigInt
        }

        assertTrue {
            val seed = 1
            val random = Random(seed)
            val a = UIntArray(352) { random.nextUInt() }
            val b = BigInteger63Arithmetic.convertFrom32BitRepresentation(a)
            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()


            aBigInt == bBigInt
        }
    }

    private fun ULongArray.fromBase64toJavaBigInteger(): BigInteger {
        return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
            acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 64))

        }
    }
}