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
import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Apr-2019
 */

@ExperimentalUnsignedTypes
class ModularInteger @ExperimentalUnsignedTypes constructor(val residue : BigInteger, val modulo : BigInteger) : BigNumber<ModularInteger> {
    override fun add(other: ModularInteger): ModularInteger {
        return ModularInteger((residue + other.residue) / modulo, modulo)
    }

    override fun subtract(other: ModularInteger): ModularInteger {
        return ModularInteger((residue - other.residue) / modulo, modulo)
    }

    override fun multiply(other: ModularInteger): ModularInteger {
        return ModularInteger((residue * other.residue) / modulo, modulo)
    }

    override fun divide(other: ModularInteger): ModularInteger {
        TODO()
    }

    override fun remainder(other: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun divideAndRemainder(other: ModularInteger): Pair<ModularInteger, ModularInteger> {
        TODO("not implemented yet")
    }

    fun compare(other: ModularInteger): Int {
        TODO("not implemented yet")
    }

    override fun isZero(): Boolean {
        TODO("not implemented yet")
    }

    override fun negate(): ModularInteger {
        TODO("not implemented yet")
    }

    override fun abs(): ModularInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun pow(exponent: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun signum(): Int {
        TODO("not implemented yet")
    }

    override fun numberOfDecimalDigits(): Long {
        TODO("not implemented yet")
    }

    override fun unaryMinus(): ModularInteger {
        TODO("not implemented yet")
    }

    override fun plus(other: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun minus(other: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(other: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun div(other: ModularInteger): ModularInteger {
        TODO("not implemented yet")
    }

    override fun rem(other: ModularInteger): ModularInteger {
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

    override fun plus(int: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun plus(long: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun plus(short: Short): ModularInteger {
        TODO("not implemented yet")
    }

    override fun plus(byte: Byte): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(int: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(long: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(short: Short): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(byte: Byte): ModularInteger {
        TODO("not implemented yet")
    }

    override fun times(char: Char): String {
        TODO("not implemented yet")
    }

    override fun minus(int: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun minus(long: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun minus(short: Short): ModularInteger {
        TODO("not implemented yet")
    }

    override fun minus(byte: Byte): ModularInteger {
        TODO("not implemented yet")
    }

    override fun div(int: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun div(long: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun div(short: Short): ModularInteger {
        TODO("not implemented yet")
    }

    override fun div(byte: Byte): ModularInteger {
        TODO("not implemented yet")
    }

    override fun rem(int: Int): ModularInteger {
        TODO("not implemented yet")
    }

    override fun rem(long: Long): ModularInteger {
        TODO("not implemented yet")
    }

    override fun rem(short: Short): ModularInteger {
        TODO("not implemented yet")
    }

    override fun rem(byte: Byte): ModularInteger {
        TODO("not implemented yet")
    }


}