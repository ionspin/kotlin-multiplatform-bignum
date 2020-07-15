/*
 * Copyright (c) 2020. Ugljesa Jovanovic
 */

package com.ionspin.kotlin.bignum.integer.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2020
 */

/**
 * end not inclusive
 */
fun mirrorBytes(source: UByteArray, start: Int, end: Int, target: UByteArray, targetStart: Int) {
    val length = end - start
    for (i in 0 until length) {
        target[targetStart + length - i - 1] = source[start + i]
    }
}

// UInt
fun UInt.toBigEndianUByteArray() : UByteArray {
    return UByteArray (4) {
        ((this shr (24 - (it * 8))) and 0xFFU).toUByte()
    }
}

fun UInt.toLittleEndianUByteArray() : UByteArray {
    return UByteArray (4) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}

//ULong

fun ULong.toBigEndianUByteArray() : UByteArray {
    return UByteArray (8) {
        ((this shr (56 - (it * 8))) and 0xFFU).toUByte()
    }
}

fun UByteArray.fromBigEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) {
            index, acc, uByte -> acc or (uByte.toULong() shl (56 - (index * 8)))
    }
    return ulong
}

fun ULong.toLittleEndianUByteArray() :UByteArray {
    return UByteArray (8) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}

fun UByteArray.fromLittleEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("More than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) { index, acc, uByte -> acc or (uByte.toULong() shl (index * 8))}
    return ulong
}

