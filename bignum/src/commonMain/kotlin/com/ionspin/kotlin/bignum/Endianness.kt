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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Sep-2019
 */
enum class Endianness {
    BIG, LITTLE
}

enum class ByteArrayRepresentation {
    /**
     * Usually represented as array of UInt or Int
     */
    FOUR_BYTE_NUMBER,

    /**
     * Usually represented as array of ULong or Long
     */
    EIGHT_BYTE_NUMBER,

    /**
     * An array of bytes, each byte represents a number in base 2^8
     */
    BYTE_STRING
}


