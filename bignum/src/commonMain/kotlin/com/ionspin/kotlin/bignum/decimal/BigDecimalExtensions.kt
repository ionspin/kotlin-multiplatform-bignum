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

package com.ionspin.kotlin.bignum.decimal

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */


@ExperimentalUnsignedTypes
fun Long.toBigDecimal(exponent : BigInteger? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromLongWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromLongAsSignificand(this, decimalMode)
    }
}


@ExperimentalUnsignedTypes
fun Int.toBigDecimal(exponent : BigInteger? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromIntWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromIntAsSignificand(this, decimalMode)
    }
}


@ExperimentalUnsignedTypes
fun Short.toBigDecimal(exponent : BigInteger? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromShortWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromShortAsSignificand(this, decimalMode)
    }
}


@ExperimentalUnsignedTypes
fun Byte.toBigDecimal(exponent : BigInteger? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromByteWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromByteAsSignificand(this, decimalMode)
    }
}

@ExperimentalUnsignedTypes
fun String.toBigDecimal(exponent : BigInteger? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.parseStringWithMode(this, decimalMode)
    }

@ExperimentalUnsignedTypes
fun Float.toBigDecimal(decimalMode: DecimalMode? = null) : BigDecimal {
    return BigDecimal.fromFloat(this, decimalMode)
}

@ExperimentalUnsignedTypes
fun Double.toBigDecimal(decimalMode: DecimalMode? = null) : BigDecimal {
    return BigDecimal.fromDouble(this, decimalMode)
}

//
//  -------- Basic type arithmetic operation extensions -------
//

// --------- Addition -----------------

@ExperimentalUnsignedTypes
operator fun Long.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

@ExperimentalUnsignedTypes
operator fun Int.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

@ExperimentalUnsignedTypes
operator fun Short.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

@ExperimentalUnsignedTypes
operator fun Byte.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

@ExperimentalUnsignedTypes
operator fun Double.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

@ExperimentalUnsignedTypes
operator fun Float.plus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() + other
}

// --------- Subtraction -----------------

@ExperimentalUnsignedTypes
operator fun Long.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

@ExperimentalUnsignedTypes
operator fun Int.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

@ExperimentalUnsignedTypes
operator fun Short.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

@ExperimentalUnsignedTypes
operator fun Byte.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

@ExperimentalUnsignedTypes
operator fun Double.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

@ExperimentalUnsignedTypes
operator fun Float.minus(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() - other
}

// --------- Multiplication -----------------

@ExperimentalUnsignedTypes
operator fun Long.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}

@ExperimentalUnsignedTypes
operator fun Int.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}

@ExperimentalUnsignedTypes
operator fun Short.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}

@ExperimentalUnsignedTypes
operator fun Byte.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}

@ExperimentalUnsignedTypes
operator fun Double.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}

@ExperimentalUnsignedTypes
operator fun Float.times(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() * other
}


// --------- Division -----------------

@ExperimentalUnsignedTypes
operator fun Long.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

@ExperimentalUnsignedTypes
operator fun Int.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

@ExperimentalUnsignedTypes
operator fun Short.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

@ExperimentalUnsignedTypes
operator fun Byte.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

@ExperimentalUnsignedTypes
operator fun Double.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

@ExperimentalUnsignedTypes
operator fun Float.div(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() / other
}

// --------- Remainder -----------------

@ExperimentalUnsignedTypes
operator fun Long.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}

@ExperimentalUnsignedTypes
operator fun Int.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}

@ExperimentalUnsignedTypes
operator fun Short.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}

@ExperimentalUnsignedTypes
operator fun Byte.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}

@ExperimentalUnsignedTypes
operator fun Double.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}

@ExperimentalUnsignedTypes
operator fun Float.rem(other: BigDecimal) : BigDecimal {
    return this.toBigDecimal() % other
}




