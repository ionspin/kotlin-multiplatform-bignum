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

package com.ionspin.kotlin.bignum.integer

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */

@ExperimentalUnsignedTypes
fun Long.toBigInteger(): BigInteger {
    return BigInteger.fromLong(this)
}

@ExperimentalUnsignedTypes
fun Int.toBigInteger(): BigInteger {
    return BigInteger.fromInt(this)
}

@ExperimentalUnsignedTypes
fun Short.toBigInteger(): BigInteger {
    return BigInteger.fromShort(this)
}

@ExperimentalUnsignedTypes
fun Byte.toBigInteger(): BigInteger {
    return BigInteger.fromByte(this)
}

@ExperimentalUnsignedTypes
fun String.toBigInteger(base: Int = 10): BigInteger {
    return BigInteger.parseString(this, base)
}

@ExperimentalUnsignedTypes
fun ULong.toBigInteger(): BigInteger {
    return BigInteger.fromULong(this)
}

@ExperimentalUnsignedTypes
fun UInt.toBigInteger(): BigInteger {
    return BigInteger.fromUInt(this)
}

@ExperimentalUnsignedTypes
fun UShort.toBigInteger(): BigInteger {
    return BigInteger.fromUShort(this)
}

@ExperimentalUnsignedTypes
fun UByte.toBigInteger(): BigInteger {
    return BigInteger.fromUByte(this)
}

//
//  -------- Basic type arithmetic operation extensions -------
//

// --------- Addition -----------------

@ExperimentalUnsignedTypes
operator fun Long.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun Int.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun Short.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun Byte.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun ULong.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun UInt.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun UShort.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

@ExperimentalUnsignedTypes
operator fun UByte.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

// --------- Subtraction -----------------

@ExperimentalUnsignedTypes
operator fun Long.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun Int.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun Short.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun Byte.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun ULong.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun UInt.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun UShort.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

@ExperimentalUnsignedTypes
operator fun UByte.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

// --------- Multiplication -----------------

@ExperimentalUnsignedTypes
operator fun Long.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun Int.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun Short.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun Byte.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun ULong.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun UInt.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun UShort.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

@ExperimentalUnsignedTypes
operator fun UByte.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

// --------- Division -----------------

@ExperimentalUnsignedTypes
operator fun Long.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun Int.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun Short.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun Byte.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun ULong.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun UInt.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun UShort.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

@ExperimentalUnsignedTypes
operator fun UByte.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

// --------- Remainder -----------------

@ExperimentalUnsignedTypes
operator fun Long.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun Int.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun Short.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun Byte.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun ULong.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun UInt.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun UShort.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}

@ExperimentalUnsignedTypes
operator fun UByte.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}
