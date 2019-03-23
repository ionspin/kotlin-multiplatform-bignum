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
 * on 10-Mar-3/10/19
 */


@ExperimentalUnsignedTypes
fun Long.toBigInteger() : BigInteger {
    return BigInteger.fromLong(this)
}

@ExperimentalUnsignedTypes
fun Int.toBigInteger() : BigInteger {
    return BigInteger.fromInt(this)
}

@ExperimentalUnsignedTypes
fun Short.toBigInteger() : BigInteger {
    return BigInteger.fromShort(this)
}

@ExperimentalUnsignedTypes
fun Byte.toBigInteger() : BigInteger {
    return BigInteger.fromByte(this)
}

@ExperimentalUnsignedTypes
operator fun BigInteger.plus(int : Int) : BigInteger {
    return this.plus(BigInteger.fromInt(int))
}

@ExperimentalUnsignedTypes
operator fun BigInteger.plus(long : Long) : BigInteger {
    return this.plus(BigInteger.fromLong(long))
}

@ExperimentalUnsignedTypes
operator fun BigInteger.plus(short : Short) : BigInteger {
    return this.plus(BigInteger.fromShort(short))
}

@ExperimentalUnsignedTypes
operator fun BigInteger.plus(byte : Byte) : BigInteger {
    return this.plus(BigInteger.fromByte(byte))
}




