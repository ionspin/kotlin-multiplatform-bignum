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

package com.ionspin.kotlin.bignum.integer.base63.array

import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.compareTo
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.div
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.minus
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.plus
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.rem
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.shl
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.shr
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic.times

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 05-Jan-2020
 */
// Signed operations TODO evaluate if we really want to do this to support Toom-Cook

data class SignedULongArray(val unsignedValue: ULongArray, val sign: Boolean)

private fun signedAdd(first: SignedULongArray, second: SignedULongArray) = if (first.sign xor second.sign) {
    if (first.unsignedValue > second.unsignedValue) {
        SignedULongArray(
            first.unsignedValue - second.unsignedValue,
            first.sign
        )
    } else {
        SignedULongArray(
            second.unsignedValue - first.unsignedValue,
            second.sign
        )
    }
} else {
    // Same sign
    SignedULongArray(
        first.unsignedValue + second.unsignedValue,
        first.sign
    )
}

val SIGNED_POSITIVE_TWO =
    SignedULongArray(BigInteger63Arithmetic.TWO, true)

private fun signedSubtract(first: SignedULongArray, second: SignedULongArray) =
    signedAdd(first, second.copy(sign = !second.sign))

private fun signedMultiply(first: SignedULongArray, second: SignedULongArray) =
    SignedULongArray(
        first.unsignedValue * second.unsignedValue,
        !(first.sign xor second.sign)
    )

private fun signedDivide(first: SignedULongArray, second: SignedULongArray) =
    SignedULongArray(
        first.unsignedValue / second.unsignedValue,
        !(first.sign xor second.sign)
    )

private fun signedRemainder(first: SignedULongArray, second: SignedULongArray) =
    SignedULongArray(
        first.unsignedValue % second.unsignedValue,
        !(first.sign xor second.sign)
    )

internal operator fun SignedULongArray.plus(other: SignedULongArray): SignedULongArray {
    return signedAdd(this, other)
}

internal operator fun SignedULongArray.minus(other: SignedULongArray): SignedULongArray {
    return signedSubtract(this, other)
}

internal operator fun SignedULongArray.times(other: SignedULongArray): SignedULongArray {
    return signedMultiply(this, other)
}

internal operator fun SignedULongArray.div(other: SignedULongArray): SignedULongArray {
    return signedDivide(this, other)
}

internal operator fun SignedULongArray.rem(other: SignedULongArray): SignedULongArray {
    return signedRemainder(this, other)
}

internal infix fun SignedULongArray.shr(places: Int) =
    SignedULongArray(unsignedValue shr places, sign)

internal infix fun SignedULongArray.shl(places: Int) =
    SignedULongArray(unsignedValue shl places, sign)

internal infix fun SignedULongArray.and(operand: ULongArray) =
    SignedULongArray(
        BigInteger63Arithmetic.and(
            unsignedValue,
            operand
        ), sign
    )

// End of signed operations