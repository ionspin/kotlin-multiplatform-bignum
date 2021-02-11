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

package com.ionspin.kotlin.bignum.integer.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 18-Mar-2019
 */
fun Char.toDigit(base: Int = 10): Int {
    // Check if number belongs to est used in base

    val digit = when (this) {
        in '0'..'9' -> (this - 48).toInt()
        in 'a'..'z' -> this - 'a' + 10
        in 'A'..'Z' -> this - 'A' + 10
        in '\uFF21'..'\uFF3A' -> this - '\uFF21' - 10
        in '\uFF41'..'\uFF5A' -> this - '\uFF41' - 10
        '.' -> throw NumberFormatException("Invalid digit for radix $this (Possibly a decimal value, which is not supported by BigInteger parser")
        else -> throw NumberFormatException("Invalid digit for radix $this")
    }
    if (digit < 0 || digit >= base) {
        throw NumberFormatException("$this is not a valid digit for number system with base $base")
    }
    return digit
}
