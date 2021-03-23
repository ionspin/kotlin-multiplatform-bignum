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

/**
 * Create BigDecimal from BigInteger significand and supplied exponent.
 * Example:
 *      1234L.toBigDecimalUsingSignificandAndExponent(2) produces 1.234E2
 *
 */
fun Long.toBigDecimalUsingSignificandAndExponent(exponent: Long, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromLongWithExponent(this, exponent, decimalMode)
}

/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      1234L.toBigDecimal() produces 1.234E3
 *      1234L.toBigDecimal(exponentModifier = 2) produces 1.234E5 (original exponent was 3 and modifier adds 2)
 */
fun Long.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromLong(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}

/**
 * Create BigDecimal from BigInteger significand and supplied exponent.
 * Example:
 *      1234L.toBigDecimalUsingSignificandAndExponent(2) produces 1.234E2
 *
 */
fun Int.toBigDecimalUsingSignificandAndExponent(exponent: Long, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromIntWithExponent(this, exponent, decimalMode)
}

/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      1234.toBigDecimal() produces 1.234E3
 *      1234.toBigDecimal(exponentModifier = 2) produces 1.234E5 (original exponent was 3 and modifier adds 2)
 */
fun Int.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromInt(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}

/**
 * Create BigDecimal from BigInteger significand and supplied exponent.
 * Example:
 *      123.toShort().toBigDecimalUsingSignificandAndExponent(2) produces 1.23E2
 *
 */
fun Short.toBigDecimalUsingSignificandAndExponent(exponent: Long, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromShortWithExponent(this, exponent, decimalMode)
}
/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      123.toShort().toBigDecimal() produces 1.23E2
 *      123.toShort().toBigDecimal(exponentModifier = 2) produces 1.23E4 (original exponent was 2 and modifier adds 2)
 */
fun Short.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromShort(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}

/**
 * Create BigDecimal from BigInteger significand and supplied exponent.
 * Example:
 *      12.toByte().toBigDecimalUsingSignificandAndExponent(2) produces 1.2E2
 *
 */
fun Byte.toBigDecimalUsingSignificandAndExponent(exponent: Long, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromByteWithExponent(this, exponent, decimalMode)
}
/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      12.toByte().toBigDecimal() produces 1.2E1
 *      12.toByte().toBigDecimal(exponentModifier = 2) produces 1.2E3 (original exponent was 1 and modifier adds 2)
 */
fun Byte.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromByte(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}
/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      "1234".toBigDecimal() produces 1.234E3
 *      "1234".toBigDecimal(exponentModifier = 2) produces 1.234E5 (original exponent was 3 and modifier adds 2)
 */
fun String.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.parseStringWithMode(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}
/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      1234F.toBigDecimal() produces 1.234E3
 *      1234F.toBigDecimal(exponentModifier = 2) produces 1.234E5 (original exponent was 3 and modifier adds 2)
 */
fun Float.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromFloat(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
}
/**
 * Converts a number to big decimal, optionally modifies the exponent and provides decimal mode.
 * Example:
 *      1234.0.toBigDecimal() produces 1.234E3
 *      1234.0.toBigDecimal(exponentModifier = 2) produces 1.234E5 (original exponent was 3 and modifier adds 2)
 */
fun Double.toBigDecimal(exponentModifier: Long? = null, decimalMode: DecimalMode? = null): BigDecimal {
    return BigDecimal.fromDouble(this, decimalMode).moveDecimalPoint(exponentModifier ?: 0)
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
