package com.ionspin.kotlin.biginteger

import com.ionspin.kotlin.biginteger.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.biginteger.base63.BigInteger63Arithmetic

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
typealias WordArray = ULongArray
internal val chosenArithmetic = BigInteger63Arithmetic
