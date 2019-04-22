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
import com.ionspin.kotlin.bignum.CommonBigNumberOperations
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

/**
 * Implementation of operations on modular integer
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Apr-2019
 */

@ExperimentalUnsignedTypes
class ModularBigInteger @ExperimentalUnsignedTypes constructor(
    val residue : BigInteger,
    val modulo : BigInteger,
    private val creator: BigNumber.Creator<ModularBigInteger>) : BigNumber<ModularBigInteger>,
        CommonBigNumberOperations<ModularBigInteger>
{
    companion object {
        fun creatorForModulo(modulo: BigInteger) : BigNumber.Creator<ModularBigInteger> {
            return object : BigNumber.Creator<ModularBigInteger> {
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

                private fun BigInteger.prep() : BigInteger {
                    val result = this % modulo
                    return when (result.sign) {
                        Sign.POSITIVE -> this % modulo
                        Sign.NEGATIVE -> (this % modulo) + modulo
                        Sign.ZERO -> this
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

    private fun assertSameModulo(other : ModularBigInteger) {
        if (this.modulo != other.modulo) {
            throw RuntimeException("Different moduli! This $modulo\n Other ${other.modulo}")
        }
    }

    override fun add(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue + other.residue) / modulo, modulo, creator)
    }

    override fun subtract(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue - other.residue) / modulo, modulo, creator)
    }

    override fun multiply(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        return ModularBigInteger((residue * other.residue) / modulo, modulo, creator)
    }

    override fun divide(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        val quotient = this.residue / other.residue
        val result = quotient % modulo
        return ModularBigInteger(result, modulo, creator)
    }

    override fun remainder(other: ModularBigInteger): ModularBigInteger {
        assertSameModulo(other)
        val remainder = this.residue % other.residue
        val result = remainder % modulo
        return ModularBigInteger(result, modulo, creator)
    }

    override fun divideAndRemainder(other: ModularBigInteger): Pair<ModularBigInteger, ModularBigInteger> {
        assertSameModulo(other)
        val quotientAndRemainder = this.residue divrem  other.residue
        val quotient = quotientAndRemainder.quotient % modulo
        val remainder = quotientAndRemainder.remainder % modulo
        return Pair(ModularBigInteger(quotient, modulo, creator), ModularBigInteger(remainder, modulo, creator)
    }

    fun compare(other: ModularBigInteger): Int {
        assertSameModulo(other)
        return this.residue.compareTo(other.residue)
    }

    override fun isZero(): Boolean {
        return residue.isZero()
    }

    /**
     *
     */
    // TODO Is this really a negation of a modular number? It's true that it will be congruent for the module, and it's
    // also true that sign will be opposite, but still, all of our modular representations are 0 < a < modulo, so it doesn't
    // really make sense
    override fun negate(): ModularBigInteger {
        return when (residue.sign) {
            Sign.ZERO -> this
            Sign.POSITIVE -> ModularBigInteger(residue - modulo, modulo, creator)
            Sign.NEGATIVE -> ModularBigInteger(residue + modulo, modulo, creator)
        }

    }

    override fun abs(): ModularBigInteger {
        return this
    }

    override fun pow(exponent: ModularBigInteger): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent.residue) % modulo, modulo, creator)
    }

    fun pow(exponent: BigInteger): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent) % modulo, modulo, creator)
    }

    override fun pow(exponent: Long): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent) % modulo, modulo, creator)
    }

    override fun pow(exponent: Int): ModularBigInteger {
        return ModularBigInteger(residue.pow(exponent) % modulo, modulo, creator)
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
            else -> throw RuntimeException("Invalid comparison type for BigInteger: ${other::class.simpleName}")
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) {
            false
        } else {
            compareTo(other) == 0
        }

    }

    override fun toString(): String {
        return residue.toString()
    }

    override fun toString(base: Int): String {
        return residue.toString(base)
    }

    fun toStringWithModulo(base : Int) : String {
        return residue.toString(base) + " mod " + modulo.toString(base)
    }




}