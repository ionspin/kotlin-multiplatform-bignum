/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ionspin.kotlin.bignum.modular

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.ByteArraySerializable
import com.ionspin.kotlin.bignum.CommonBigNumberOperations
import com.ionspin.kotlin.bignum.ModularQuotientAndRemainder
import com.ionspin.kotlin.bignum.NarrowingOperations
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

/**
 * Implementation of operations on modular integer
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Apr-2019
 */

class ModularBigInteger private constructor(
    signedResidue: BigInteger,
    val modulus: BigInteger,
    private val creator: BigNumber.Creator<ModularBigInteger>
) : BigNumber<ModularBigInteger>,
    CommonBigNumberOperations<ModularBigInteger>,
    NarrowingOperations<ModularBigInteger>,
    ByteArraySerializable {

    val residue = when (signedResidue.sign) {
        Sign.POSITIVE -> signedResidue
        Sign.NEGATIVE -> signedResidue + modulus
        Sign.ZERO -> BigInteger.ZERO
    }

    init {
        if (modulus.sign == Sign.NEGATIVE) {
            throw ArithmeticException("Modulus must be a positive number")
        }
    }

    companion object {
        fun creatorForModulo(modulo: ULong): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromULong(modulo))

        fun creatorForModulo(modulo: UInt): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromUInt(modulo))

        fun creatorForModulo(modulo: UShort): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromUShort(modulo))

        fun creatorForModulo(modulo: UByte): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromUByte(modulo))

        fun creatorForModulo(modulo: Long): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromLong(modulo))

        fun creatorForModulo(modulo: Int): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromInt(modulo))

        fun creatorForModulo(modulo: Short): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromShort(modulo))

        fun creatorForModulo(modulo: Byte): BigNumber.Creator<ModularBigInteger> =
            creatorForModulo(BigInteger.fromByte(modulo))

        fun creatorForModulo(modulo: BigInteger): BigNumber.Creator<ModularBigInteger> {
            return object : BigNumber.Creator<ModularBigInteger> {
                override val ZERO = ModularBigInteger(BigInteger.ZERO, modulo, this)
                override val ONE = ModularBigInteger(BigInteger.ONE, modulo, this)
                override val TWO = ModularBigInteger(BigInteger.TWO, modulo, this)
                override val TEN = ModularBigInteger(BigInteger.TEN, modulo, this)

                override fun fromBigInteger(bigInteger: BigInteger): ModularBigInteger {
                    return ModularBigInteger(bigInteger.prep(), modulo, this)
                }

                override fun parseString(string: String, base: Int): ModularBigInteger {
                    return ModularBigInteger(BigInteger.parseString(string, base).prep(), modulo, this)
                }

                override fun fromULong(uLong: ULong): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromULong(uLong).prep(), modulo, this)
                }

                override fun fromUInt(uInt: UInt): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUInt(uInt).prep(), modulo, this)
                }

                override fun fromUShort(uShort: UShort): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUShort(uShort).prep(), modulo, this)
                }

                override fun fromUByte(uByte: UByte): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUByte(uByte).prep(), modulo, this)
                }

                override fun fromLong(long: Long): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromLong(long).prep(), modulo, this)
                }

                override fun fromInt(int: Int): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromInt(int).prep(), modulo, this)
                }

                override fun fromShort(short: Short): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromShort(short).prep(), modulo, this)
                }

                override fun fromByte(byte: Byte): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromByte(byte).prep(), modulo, this)
                }

                override fun tryFromFloat(float: Float, exactRequired: Boolean): ModularBigInteger {
                    return ModularBigInteger(BigInteger.tryFromFloat(float, exactRequired).prep(), modulo, this)
                }

                override fun tryFromDouble(double: Double, exactRequired: Boolean): ModularBigInteger {
                    return ModularBigInteger(BigInteger.tryFromDouble(double, exactRequired).prep(), modulo, this)
                }

                private fun BigInteger.prep(): BigInteger {
                    val result = this % modulo
                    return when (result.sign) {
                        Sign.POSITIVE -> result
                        Sign.NEGATIVE -> result + modulo
                        Sign.ZERO -> BigInteger.ZERO
                    }
                }
            }
        }
    }

    override fun getCreator(): BigNumber.Creator<ModularBigInteger> {
        return creator
    }

    override fun getInstance(): ModularBigInteger {
        return this
    }

    private fun assertSameModulo(other: ModularBigInteger) {
        if (this.modulus != other.modulus) {
            throw RuntimeException("Different moduli! This $modulus\n Other ${other.modulus}")
        }
    }

    override fun add(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue + other.residue) % modulus, modulus, creator)
    }

    override fun subtract(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue - other.residue) % modulus, modulus, creator)
    }

    override fun multiply(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue * other.residue) % modulus, modulus, creator)
    }

    override fun divide(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        val modInverse = other.residue.modInverse(modulus)
        val result = (modInverse * residue) % modulus
        return ModularBigInteger(result, modulus, creator)
    }

    override fun remainder(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        checkIfDivisible(other)
        val remainder = this.residue % other.residue
        val result = remainder % modulus
        return ModularBigInteger(result, modulus, creator)
    }

    override fun divideAndRemainder(other: ModularBigInteger): Pair<ModularBigInteger, ModularBigInteger> {
        assertSameModulo(other)
        checkIfDivisible(other)
        val quotientAndRemainder = this.residue divrem other.residue
        val quotient = quotientAndRemainder.quotient % modulus
        val remainder = quotientAndRemainder.remainder % modulus
        return Pair(ModularBigInteger(quotient, modulus, creator), ModularBigInteger(remainder, modulus, creator))
    }

    fun inverse(): ModularBigInteger {
        val inverse = residue.modInverse(modulus)
        return ModularBigInteger(inverse, modulus, creator)
    }

    fun compare(other: ModularBigInteger): Int {
        assertSameModulo(other)
        return this.residue.compareTo(other.residue)
    }

    override fun isZero(): Boolean {
        return residue.isZero()
    }

    override fun negate(): ModularBigInteger {
        return ModularBigInteger(-residue, modulus, creator)
    }

    override fun abs(): ModularBigInteger {
        return this
    }

    fun pow(exponent: ModularBigInteger): ModularBigInteger {
        return pow(exponent.residue)
    }

    fun pow(exponent: BigInteger): ModularBigInteger {
        var e = exponent
        return if (this.modulus == BigInteger.ONE) {
            creator.ZERO
        } else {
            var residue = BigInteger.ONE
            var base = this.residue
            while (e > 0) {
                if (e % 2 == BigInteger.ONE) {
                    residue = (residue * base) % modulus
                }
                e = e shr 1
                base = base.pow(2) % modulus
            }
            ModularBigInteger(residue, modulus, creator)
        }
    }

    override fun pow(exponent: Long): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent) % modulus, modulus, creator)
    }

    override fun pow(exponent: Int): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent) % modulus, modulus, creator)
    }

    override fun signum(): Int {
        return residue.signum()
    }

    override fun numberOfDecimalDigits(): Long {
        return residue.numberOfDecimalDigits()
    }

    override fun unaryMinus(): ModularBigInteger {
        return negate()
    }

    override fun secureOverwrite() {
        residue.secureOverwrite()
    }

    override fun rem(other: ModularBigInteger): ModularBigInteger {
        return remainder(other)
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is ModularBigInteger -> compare(other)
            is BigInteger -> residue.compare(other)
            is Long -> compare(creator.fromLong(other))
            is Int -> compare(creator.fromInt(other))
            is Short -> compare(creator.fromShort(other))
            is Byte -> compare(creator.fromByte(other))
            else -> throw RuntimeException("Invalid comparison type for BigInteger: $other")
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) {
            false
        } else {
            compareTo(other) == 0
        }
    }

    override fun hashCode(): Int {
        return residue.hashCode()
    }

    override fun toString(): String {
        return residue.toString()
    }

    override fun toString(base: Int): String {
        return residue.toString(base)
    }

    fun toStringWithModulo(base: Int = 10): String {
        return residue.toString(base) + " mod " + modulus.toString(base)
    }

    infix fun divrem(other: ModularBigInteger): ModularQuotientAndRemainder {
        val result = divideAndRemainder(other)
        return ModularQuotientAndRemainder(result.first, result.second)
    }

    fun toBigInteger(): BigInteger {
        return residue
    }

    private fun checkIfDivisible(other: ModularBigInteger) {
        if (other.residue.gcd(modulus) != BigInteger.ONE) {
            throw ArithmeticException("BigInteger is not invertible. Operand and modulus are not relatively prime (coprime)")
        }
    }

    internal fun checkIfDivisibleBoolean(first: ModularBigInteger, second: ModularBigInteger) =
        second.residue.gcd(first.modulus) == BigInteger.ONE

    override fun intValue(exactRequired: Boolean): Int {
        if (exactRequired && residue > Int.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to int and provide exact value")
        }
        return residue.intValue(exactRequired)
    }

    override fun longValue(exactRequired: Boolean): Long {
        if (exactRequired && (residue > Long.MAX_VALUE.toUInt())) {
            throw ArithmeticException("Cannot convert to long and provide exact value")
        }
        return residue.longValue(exactRequired)
    }

    override fun byteValue(exactRequired: Boolean): Byte {
        if (exactRequired && residue > Byte.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to byte and provide exact value")
        }
        return residue.byteValue(exactRequired)
    }

    override fun shortValue(exactRequired: Boolean): Short {
        if (exactRequired && residue > Short.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to short and provide exact value")
        }
        return residue.shortValue(exactRequired)
    }

    override fun uintValue(exactRequired: Boolean): UInt {
        if (exactRequired && residue > UInt.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned int and provide exact value")
        }
        return residue.uintValue(exactRequired)
    }

    override fun ulongValue(exactRequired: Boolean): ULong {
        if (exactRequired && (residue > ULong.MAX_VALUE.toUInt())) {
            throw ArithmeticException("Cannot convert to unsigned long and provide exact value")
        }
        return residue.ulongValue(exactRequired)
    }

    override fun ubyteValue(exactRequired: Boolean): UByte {
        if (exactRequired && residue > UByte.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned byte and provide exact value")
        }
        return residue.ubyteValue(exactRequired)
    }

    override fun ushortValue(exactRequired: Boolean): UShort {
        if (exactRequired && residue > UShort.MAX_VALUE.toUInt()) {
            throw ArithmeticException("Cannot convert to unsigned short and provide exact value")
        }
        return residue.ushortValue(exactRequired)
    }

    override fun floatValue(exactRequired: Boolean): Float {
        return residue.floatValue()
    }

    override fun doubleValue(exactRequired: Boolean): Double {
        return residue.doubleValue()
    }

    override fun toUByteArray(): UByteArray {
        return residue.toUByteArray()
    }

    override fun toByteArray(): ByteArray {
        return residue.toByteArray()
    }
}
