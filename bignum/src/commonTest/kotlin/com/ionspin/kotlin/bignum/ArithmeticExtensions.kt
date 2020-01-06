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

package com.ionspin.kotlin.bignum

import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 */
// TODO this breaks Kotln Native at the moment, since we are only releasing Array version, we'll hardcode it
// Need to find a better solution than removal of zeroes at init time.
@ExperimentalUnsignedTypes
fun ULongArray.removeLeadingZeroes(): ULongArray {
    // if ((TypeHelper.instance as Any) is ULongArray) {
        return BigInteger63Arithmetic.removeLeadingZeros(this) // as WordArray
    // }
    // if ((TypeHelper.instance as Any) is List<*>) {
    //     return BigInteger63LinkedListArithmetic.removeLeadingZeros(this as List<ULong>) as WordArray
    // }
    // throw RuntimeException("Invalid WordArray type")
}
