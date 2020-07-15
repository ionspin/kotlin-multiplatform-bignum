/*
 * Copyright (c) 2020. Ugljesa Jovanovic
 */

package com.ionspin.kotlin.bignum.integer.util

import com.ionspin.kotlin.bignum.integer.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic

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

/**
 * Increment a bytestring Big Endian/Big Endian array
 */
fun increment(byteString: UByteArray) : UByteArray {
    val firstLessThan255 = byteString.indexOfLast { it < 0xFFU }
    return if (firstLessThan255 != -1) {
        val copy = byteString.copyOf()
        copy[firstLessThan255]++
        copy
    } else {
        ubyteArrayOf(1U) + byteString
    }
}

/**
 * Increment a 2^64 base uint Big Endian/Big Endian array
 */
fun increment(array: UIntArray) : UIntArray {
    val firstLessThan255 = array.indexOfLast { it < 0xFFFFFFFFU }
    return if (firstLessThan255 != -1) {
        val copy = array.copyOf()
        copy[firstLessThan255]++
        copy
    } else {
        uintArrayOf(1U) + array
    }
}

/**
 * Increment a 2^64 base ulong Big Endian/Big Endian array
 */
fun increment(array: ULongArray) : ULongArray {
    val firstLessThan255 = array.indexOfLast { it < 0xFFFFFFFFFFFFFFFFU }
    return if (firstLessThan255 != -1) {
        val copy = array.copyOf()
        copy[firstLessThan255]++
        copy
    } else {
        ulongArrayOf(1U) + array
    }
}

fun invert(array: ULongArray) : ULongArray {
    val bitLengthOfMostSignificant = BigInteger63Arithmetic.bitLength(array[0])
    val roundedBitLength = ((bitLengthOfMostSignificant + 8 - 1) / 8) * 8
    val inverted = array.map { it.inv() }.toULongArray()
    inverted[0] = (0xFFFFFFFFFFFFFFFFUL shl roundedBitLength).inv() and inverted[0]
    return inverted
}

fun invert(array: UIntArray) : UIntArray {
    val bitLengthOfMostSignificant = BigInteger32Arithmetic.bitLength(array[0])
    val roundedBitLength = ((bitLengthOfMostSignificant + 8 - 1) / 8) * 8
    val inverted = array.map { it.inv() }.toUIntArray()
    inverted[0] = (0xFFFFFFFFU shl roundedBitLength).inv() and inverted[0]
    return inverted
}

