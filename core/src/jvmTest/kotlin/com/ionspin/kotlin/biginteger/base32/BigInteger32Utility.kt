package com.ionspin.kotlin.biginteger.base32

import java.math.BigInteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
fun UIntArray.toJavaBigInteger(): BigInteger {
    return this.foldIndexed(BigInteger.valueOf(0)) { index, acc, digit ->
        acc.or(BigInteger(digit.toString(), 10).shiftLeft((index) * 32))

    }
}