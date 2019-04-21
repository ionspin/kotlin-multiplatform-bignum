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

/**
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
        fun creatorForModule(modulo: BigInteger) : BigNumber.Creator<ModularBigInteger> {
            return object : BigNumber.Creator<ModularBigInteger> {
                override fun parseString(string: String, base: Int): ModularBigInteger {
                    return ModularBigInteger(BigInteger.parseString(string, base), modulo, this)
                }

                override fun fromULong(uLong: ULong): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromULong(uLong), modulo, this)
                }

                override fun fromUInt(uInt: UInt): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUInt(uInt), modulo, this)
                }

                override fun fromUShort(uShort: UShort): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUShort(uShort), modulo, this)
                }

                override fun fromUByte(uByte: UByte): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromUByte(uByte), modulo, this)
                }

                override fun fromLong(long: Long): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromLong(long), modulo, this)
                }

                override fun fromInt(int: Int): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromInt(int), modulo, this)
                }

                override fun fromShort(short: Short): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromShort(short), modulo, this)
                }

                override fun fromByte(byte: Byte): ModularBigInteger {
                    return ModularBigInteger(BigInteger.fromByte(byte), modulo, this)
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

    override fun add(other: ModularBigInteger): ModularBigInteger {
        return ModularBigInteger((residue + other.residue) / modulo, modulo, creator)
    }

    override fun subtract(other: ModularBigInteger): ModularBigInteger {
        return ModularBigInteger((residue - other.residue) / modulo, modulo, creator)
    }

    override fun multiply(other: ModularBigInteger): ModularBigInteger {
        return ModularBigInteger((residue * other.residue) / modulo, modulo, creator)
    }

    override fun divide(other: ModularBigInteger): ModularBigInteger {
        TODO()
    }

    override fun remainder(other: ModularBigInteger): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun divideAndRemainder(other: ModularBigInteger): Pair<ModularBigInteger, ModularBigInteger> {
        TODO("not implemented yet")
    }

    fun compare(other: ModularBigInteger): Int {
        TODO("not implemented yet")
    }

    override fun isZero(): Boolean {
        return residue.isZero()
    }

    override fun negate(): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun abs(): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: ModularBigInteger): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: Long): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: Int): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun signum(): Int {
        TODO("not implemented yet")
    }

    override fun numberOfDecimalDigits(): Long {
        TODO("not implemented yet")
    }

    override fun unaryMinus(): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun rem(other: ModularBigInteger): ModularBigInteger {
        TODO("not implemented yet")
    }

    override fun compareTo(other: Any): Int {
        TODO("not implemented yet")
    }

    override fun equals(other: Any?): Boolean {
        TODO("not implemented yet")
    }

    override fun toString(): String {
        TODO("not implemented yet")
    }

    override fun toString(base: Int): String {
        TODO("not implemented yet")
    }




}