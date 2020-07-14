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
fun fromBigEndianToLittleEndianInPlace(source: UByteArray, start: Int, end: Int, target: UByteArray, targetStart: Int) {
    val length = end - start
    for (i in 0 until length) {
        target[targetStart + length - i - 1] = source[start + i]
    }
}
/**
 * end not inclusive
 */
fun fromLittleEndianToBigEndianInPlace(source: UByteArray, start: Int, end: Int, target: UByteArray, targetStart: Int) {
    val length = end - start
    for (i in 0 until length) {
        target[targetStart + i] = source[start + length - i - 1]
    }
}
