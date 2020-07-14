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


fun Long.toBigInteger(): BigInteger {
    return BigInteger.fromLong(this)
}


fun Int.toBigInteger(): BigInteger {
    return BigInteger.fromInt(this)
}


fun Short.toBigInteger(): BigInteger {
    return BigInteger.fromShort(this)
}


fun Byte.toBigInteger(): BigInteger {
    return BigInteger.fromByte(this)
}


fun String.toBigInteger(base: Int = 10): BigInteger {
    return BigInteger.parseString(this, base)
}


fun ULong.toBigInteger(): BigInteger {
    return BigInteger.fromULong(this)
}


fun UInt.toBigInteger(): BigInteger {
    return BigInteger.fromUInt(this)
}


fun UShort.toBigInteger(): BigInteger {
    return BigInteger.fromUShort(this)
}


fun UByte.toBigInteger(): BigInteger {
    return BigInteger.fromUByte(this)
}

//
//  -------- Basic type arithmetic operation extensions -------
//

// --------- Addition -----------------


operator fun Long.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun Int.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun Short.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun Byte.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun ULong.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun UInt.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun UShort.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}


operator fun UByte.plus(other: BigInteger): BigInteger {
    return this.toBigInteger() + other
}

// --------- Subtraction -----------------


operator fun Long.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun Int.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun Short.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun Byte.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun ULong.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun UInt.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun UShort.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}


operator fun UByte.minus(other: BigInteger): BigInteger {
    return this.toBigInteger() - other
}

// --------- Multiplication -----------------


operator fun Long.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun Int.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun Short.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun Byte.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun ULong.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun UInt.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun UShort.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}


operator fun UByte.times(other: BigInteger): BigInteger {
    return this.toBigInteger() * other
}

// --------- Division -----------------


operator fun Long.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun Int.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun Short.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun Byte.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun ULong.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun UInt.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun UShort.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}


operator fun UByte.div(other: BigInteger): BigInteger {
    return this.toBigInteger() / other
}

// --------- Remainder -----------------


operator fun Long.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun Int.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun Short.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun Byte.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun ULong.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun UInt.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun UShort.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}


operator fun UByte.rem(other: BigInteger): BigInteger {
    return this.toBigInteger() % other
}
