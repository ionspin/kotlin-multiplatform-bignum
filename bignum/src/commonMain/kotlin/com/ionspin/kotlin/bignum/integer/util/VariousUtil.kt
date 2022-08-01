/*
 * Copyright (c) 2020. Ugljesa Jovanovic
 */

package com.ionspin.kotlin.bignum.integer.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
fun Array<UByte>.hexColumsPrint(chunk: Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun UByteArray.hexColumsPrint(chunk: Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

infix operator fun Char.times(count: Int): String {
    val stringBuilder = StringBuilder()
    for (i in 0 until count) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}

infix operator fun Char.times(count: Long): String {
    val stringBuilder = StringBuilder()
    for (i in 0 until count) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}
