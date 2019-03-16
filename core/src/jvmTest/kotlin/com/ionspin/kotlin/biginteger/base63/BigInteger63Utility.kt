package com.ionspin.kotlin.biginteger.base63

import java.math.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
fun ULongArray.toJavaBigInteger(): BigInteger {
    return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
        acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 63))

    }
}

fun bla() {
    val i = 1
    i.toBigInteger()
}