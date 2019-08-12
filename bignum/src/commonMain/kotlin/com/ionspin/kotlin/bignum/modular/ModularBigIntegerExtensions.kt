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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */


@ExperimentalUnsignedTypes
fun Long.toModularBigInteger(creator : BigNumber.Creator<ModularBigInteger>): ModularBigInteger {
    return creator.fromLong(this)
}


@ExperimentalUnsignedTypes
fun Int.toModularBigInteger(creator : BigNumber.Creator<ModularBigInteger>): ModularBigInteger {
    return creator.fromInt(this)
}


@ExperimentalUnsignedTypes
fun Short.toModularBigInteger(creator : BigNumber.Creator<ModularBigInteger>): ModularBigInteger {
    return creator.fromShort(this)
}


@ExperimentalUnsignedTypes
fun Byte.toModularBigInteger(creator : BigNumber.Creator<ModularBigInteger>): ModularBigInteger {
    return creator.fromByte(this)
}

@ExperimentalUnsignedTypes
fun String.toModularBigInteger(creator : BigNumber.Creator<ModularBigInteger>, base : Int = 10) : ModularBigInteger {
    return creator.parseString(this, base)
}

//
//  -------- Basic type arithmetic operation extensions -------
//

// --------- Addition -----------------

@ExperimentalUnsignedTypes
operator fun Long.plus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) + other
}

@ExperimentalUnsignedTypes
operator fun Int.plus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) + other
}

@ExperimentalUnsignedTypes
operator fun Short.plus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) + other
}

@ExperimentalUnsignedTypes
operator fun Byte.plus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) + other
}

// --------- Subtraction -----------------

@ExperimentalUnsignedTypes
operator fun Long.minus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) - other
}

@ExperimentalUnsignedTypes
operator fun Int.minus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) - other
}

@ExperimentalUnsignedTypes
operator fun Short.minus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) - other
}

@ExperimentalUnsignedTypes
operator fun Byte.minus(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) - other
}

// --------- Multiplication -----------------

@ExperimentalUnsignedTypes
operator fun Long.times(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) * other
}

@ExperimentalUnsignedTypes
operator fun Int.times(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) * other
}

@ExperimentalUnsignedTypes
operator fun Short.times(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) * other
}

@ExperimentalUnsignedTypes
operator fun Byte.times(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) * other
}


// --------- Division -----------------

@ExperimentalUnsignedTypes
operator fun Long.div(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) / other
}

@ExperimentalUnsignedTypes
operator fun Int.div(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) / other
}

@ExperimentalUnsignedTypes
operator fun Short.div(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) / other
}

@ExperimentalUnsignedTypes
operator fun Byte.div(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) / other
}

// --------- Remainder -----------------

@ExperimentalUnsignedTypes
operator fun Long.rem(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) % other
}

@ExperimentalUnsignedTypes
operator fun Int.rem(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) % other
}

@ExperimentalUnsignedTypes
operator fun Short.rem(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) % other
}

@ExperimentalUnsignedTypes
operator fun Byte.rem(other: ModularBigInteger) : ModularBigInteger {
    return this.toModularBigInteger(other.getCreator()) % other
}


