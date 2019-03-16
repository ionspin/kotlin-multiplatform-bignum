package com.ionspin.kotlin.biginteger

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Mar-3/16/19
 */
class ParsingAndToStringTest {
    val seed = 1
    val random = Random(1)

    @Test
    fun testParsing() {
        parsingSingleTest("1234" , 10)
    }

    fun parsingSingleTest(textNumber : String, base : Int) {
        assertTrue {
            val parsed = BigInteger.parseString(textNumber, base)
            val printed = parsed.toString(base)
            print(printed)


            textNumber == printed
        }
    }
}