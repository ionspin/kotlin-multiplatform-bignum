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
