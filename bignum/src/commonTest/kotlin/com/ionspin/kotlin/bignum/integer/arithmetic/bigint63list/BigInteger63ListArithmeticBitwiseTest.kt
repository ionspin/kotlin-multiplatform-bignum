 /*
  *    Copyright 2025 Branislav Sindjelic
  *
  *    Licensed under the Apache License, Version 2.0 (the "License");
  *    you may not use this file except in compliance with the License.
  *    You may obtain a copy of the License at
  *
  *        http://www.apache.org/licenses/LICENSE-2.0
  *
  *    Unless required by applicable law or agreed to in writing, software
  *    distributed under the License is distributed on an "AS IS" BASIS,
  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *    See the License for the specific language governing permissions and
  *    limitations under the License.
  *
  */

package com.ionspin.kotlin.bignum.integer.arithmetic.bigint63list

import com.ionspin.kotlin.bignum.integer.base63.BigInteger63LinkedListArithmetic
import kotlin.test.Test
import kotlin.test.assertTrue

class BigInteger63ListArithmeticBitwiseTest {
    @Test
    fun testIsEven() {
        val firstArray = listOf(0UL, 64UL, 0UL, 3UL, 2UL, 1UL)
        val number = BigInteger63LinkedListArithmetic.add(firstArray, listOf(2UL))
        assertTrue {
            BigInteger63LinkedListArithmetic.isEven(number)
        }

        val secondArray = listOf(1UL, 64UL, 0UL, 2UL)
        val number2 = BigInteger63LinkedListArithmetic.add(secondArray, listOf(2UL))
        assertTrue {
            BigInteger63LinkedListArithmetic.isEven(number2).not()
        }
    }
}
