package com.ionspin.kotlin.bignum.decimal

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class BigDecimalRemainderTest {

    @Test
    fun multipleRemainderTest() {
        multipleTestLauncher(::remainderTest)
    }

    fun remainderTest(random: Random) {
        val next = random.nextInt(100)
        val next2 = random.nextInt(100)
        val number1 = StringBuilder()
        val number2 = StringBuilder()

        for (j in 0..next) {
            val int = random.nextInt(10)
            number1.append(int)
        }

        for (j in 0..next2) {
            val int = random.nextInt(10)
            number2.append(int)
        }

        val sign1 = random.nextBoolean()
        val sign2 = random.nextBoolean()

        val pointPlace1 = random.nextInt(number1.length.coerceAtLeast(1))
        number1.insert(pointPlace1, ".")
        number1.run {
            if (sign1) {
                insert(0, "-")
            }
        }
        val pointPlace2 = random.nextInt(number2.length.coerceAtLeast(1))
        number2.insert(pointPlace2, ".")
        number2.run {
            if (sign2) {
                insert(0, "-")
            }
        }
        val a = number1.toString().toBigDecimal()
        val b = number2.toString().toBigDecimal()
        if (b != BigDecimal.ZERO) {
            val javaBigDecimalRemainder = a.toJavaBigDecimal() % b.toJavaBigDecimal()
            val kotlinBigDecimalRemainder = (a % b).toJavaBigDecimal()
            assertTrue {
                javaBigDecimalRemainder.compareTo(kotlinBigDecimalRemainder) == 0
            }
        }
    }
}
