package com.ionspin.kotlin.biginteger

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
class BigInteger32JavaBitwiseTest {

    @Test
    fun shiftLeftTest() {
        shiftTest(32, 1U)
        shiftTest(32, 2U)
        shiftTest(32, 0U - 1U)
        shiftTest(35, 0U - 1U)
        shiftTest(64, 0U - 1U)
        shiftTest(75, 0U - 1U)
        shiftTest(5, 0U - 1U)
        shiftTest(237, 0U - 1U)
    }

    fun shiftTest(places : Int, vararg uints : UInt) {
        assertTrue ("Failed for $places and elements ${uints.contentToString()}") {
            val a = uintArrayOf(*uints)
            val result = BigInteger32Operations.shiftLeft(a, places).toJavaBigInteger()
            val bigIntResult = a.toJavaBigInteger() shl places
            result == bigIntResult
        }
    }
}