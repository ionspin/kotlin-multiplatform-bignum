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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */


fun Long.toBigDecimal(exponent: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromLongWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromLong(this, decimalMode)
    }
}


fun Int.toBigDecimal(exponent: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromIntWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromInt(this, decimalMode)
    }
}


fun Short.toBigDecimal(exponent: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromShortWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromShort(this, decimalMode)
    }
}


fun Byte.toBigDecimal(exponent: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return if (exponent != null) {
        BigDecimal.fromByteWithExponent(this, exponent, decimalMode)
    } else {
        BigDecimal.fromByte(this, decimalMode)
    }
}


fun String.toBigDecimal(exponent: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.parseStringWithMode(this, decimalMode)
}


fun Float.toBigDecimal(decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromFloat(this, decimalMode)
}


fun Double.toBigDecimal(decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromDouble(this, decimalMode)
}

//
//  -------- Basic type arithmetic operation extensions -------
//

// --------- Addition -----------------


operator fun Long.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}


operator fun Int.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}


operator fun Short.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}


operator fun Byte.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}


operator fun Double.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}


operator fun Float.plus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() + other
}

// --------- Subtraction -----------------


operator fun Long.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}


operator fun Int.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}


operator fun Short.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}


operator fun Byte.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}


operator fun Double.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}


operator fun Float.minus(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() - other
}

// --------- Multiplication -----------------


operator fun Long.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}


operator fun Int.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}


operator fun Short.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}


operator fun Byte.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}


operator fun Double.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}


operator fun Float.times(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() * other
}

// --------- Division -----------------


operator fun Long.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}


operator fun Int.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}


operator fun Short.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}


operator fun Byte.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}


operator fun Double.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}


operator fun Float.div(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() / other
}

// --------- Remainder -----------------


operator fun Long.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}


operator fun Int.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}


operator fun Short.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}


operator fun Byte.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}


operator fun Double.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}


operator fun Float.rem(other: BigDecimal): BigDecimal {
    return this.toBigDecimal() % other
}
