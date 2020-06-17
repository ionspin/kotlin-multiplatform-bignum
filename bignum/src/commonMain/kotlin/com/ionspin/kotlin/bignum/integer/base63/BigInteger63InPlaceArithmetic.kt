/*
 *    Copyright 2019 Ugljesa Jovanovic
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

package com.ionspin.kotlin.bignum.integer.base63

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigIntegerArithmetic
import com.ionspin.kotlin.bignum.integer.Quadruple
import com.ionspin.kotlin.bignum.integer.Sextuple
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.bignum.integer.util.toDigit
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-2019
 *
 * Word order is big endian
 */

@ExperimentalUnsignedTypes
internal object BigInteger63InPlaceArithmetic : BigIntegerArithmetic {
    override val _emitLongArray: LongArray = longArrayOf()
    override val ZERO: ULongArray = ulongArrayOf(0u)
    override val ONE: ULongArray = ulongArrayOf(1u)
    override val TWO: ULongArray = ulongArrayOf(2u)
    override val TEN: ULongArray = ulongArrayOf(10UL)

    val reciprocalOf3In2ToThePowerOf63 = ulongArrayOf(3074457345618258603U)
    override val basePowerOfTwo: Int = 63
    val wordSizeInBits = 63

    val baseMask: ULong = 0x7FFFFFFFFFFFFFFFUL
    val baseMaskArray: ULongArray = ulongArrayOf(0x7FFFFFFFFFFFFFFFUL)

    val lowMask = 0x00000000FFFFFFFFUL
    val highMask = 0x7FFFFFFF00000000UL
    val overflowMask = 0x8000000000000000UL

    const val karatsubaThreshold = 120
    const val toomCookThreshold = 15_000

    const val debugOperandSize = true

    override fun numberOfLeadingZerosInAWord(value: ULong): Int {
        var x = value
        var y: ULong
        var n = 63

        y = x shr 32
        if (y != 0UL) {
            n = n - 32
            x = y
        }
        y = x shr 16
        if (y != 0UL) {
            n = n - 16
            x = y
        }
        y = x shr 8
        if (y != 0UL) {
            n = n - 8
            x = y
        }
        y = x shr 4
        if (y != 0UL) {
            n = n - 4
            x = y
        }
        y = x shr 2
        if (y != 0UL) {
            n = n - 2
            x = y
        }
        y = x shr 1
        if (y != 0UL) {
            return n - 2
        }

        return n - x.toInt()
    }

    fun numberOfTrailingZerosInAWord(value: ULong): Int {
        var x = value
        var y: ULong
        var n = 63

        y = (x shl 32) and baseMask
        if (y != 0UL) {
            n -= 32
            x = y
        }
        y = (x shl 16) and baseMask
        if (y != 0UL) {
            n -= 16
            x = y
        }
        y = (x shl 8) and baseMask
        if (y != 0UL) {
            n = n - 8
            x = y
        }
        y = (x shl 4) and baseMask
        if (y != 0UL) {
            n = n - 4
            x = y
        }
        y = (x shl 2) and baseMask
        if (y != 0UL) {
            n = n - 2
            x = y
        }
        y = (x shl 1) and baseMask
        if (y != 0UL) {
            return n - 2
        }
        return n - x.toInt()
    }

    override fun bitLength(value: ULongArray): Int {
        if (value.contentEquals(ZERO)) {
            return 0
        }
        val start = value.size - countLeadingZeroWords(value) - 1
        val mostSignificant = value[start]
        return bitLength(mostSignificant) + (start) * 63
    }

    fun bitLength(value: ULong): Int {
        return 63 - numberOfLeadingZerosInAWord(value)
    }

    fun trailingZeroBits(value: ULong): Int {
        return numberOfTrailingZerosInAWord(value)
    }

    override fun trailingZeroBits(value: ULongArray): Int {
        if (value.contentEquals(ZERO)) {
            return 0
        }
        val zeroWordsCount = value.takeWhile { it == 0UL }.count()
        if (zeroWordsCount == value.size) {
            return 0
        }
        return trailingZeroBits(value[zeroWordsCount]) + (zeroWordsCount * 63)
    }

    fun removeLeadingZeros(bigInteger: ULongArray): ULongArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0UL } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            // Array is equal to zero, so we return array with zero elements
            return ZERO
        }
        if (bigInteger.size == firstEmpty) {
            return bigInteger
        }
        // println("RLZ original array : ${bigInteger.size} contains: ${bigInteger.size - firstEmpty} zeros")
        return bigInteger.copyOfRange(0, firstEmpty)
    }

    fun countLeadingZeroWords(bigInteger: ULongArray): Int {
        // Presume there are no leading zeros
        var lastNonEmptyIndex = bigInteger.size - 1
        // Check if it's an empty array
        if (lastNonEmptyIndex <= 0) {
            return 0
        }
        // Get the last element (Word order is high endian so leading zeros are only on highest indexes
        var element = bigInteger[lastNonEmptyIndex]
        while (element == 0UL && lastNonEmptyIndex > 0) {
            lastNonEmptyIndex -= 1
            element = bigInteger[lastNonEmptyIndex]
        }
        if (bigInteger[lastNonEmptyIndex] == 0UL) {
            lastNonEmptyIndex -= 1
        }
        return bigInteger.size - lastNonEmptyIndex - 1
    }

    override fun shiftLeft(operand: ULongArray, places: Int): ULongArray {
        if (operand.contentEquals(ZERO)) {
            return operand
        }
        if (places == 0) {
            return operand
        }

        if (operand.isEmpty()) {
            return ZERO
        }

        val leadingZeroWords = countLeadingZeroWords(operand)
        if (operand.size == leadingZeroWords) {
            return ZERO
        }

        val originalSize = operand.size - leadingZeroWords
        val leadingZeros =
            numberOfLeadingZerosInAWord(operand[originalSize - 1])
        val shiftWords = places / basePowerOfTwo
        val shiftBits = places % basePowerOfTwo
        val wordsNeeded = if (shiftBits > leadingZeros) {
            shiftWords + 1
        } else {
            shiftWords
        }
        if (shiftBits == 0) {
            return ULongArray(originalSize + wordsNeeded) {
                when (it) {
                    in 0 until shiftWords -> 0U
                    else -> operand[it - shiftWords]
                }
            }
        }
        return ULongArray(originalSize + wordsNeeded) {
            when (it) {
                in 0 until shiftWords -> 0U
                shiftWords -> {
                    (operand[it - shiftWords] shl shiftBits) and baseMask
                }
                in (shiftWords + 1) until (originalSize + shiftWords) -> {
                    ((operand[it - shiftWords] shl shiftBits) and baseMask) or (operand[it - shiftWords - 1] shr (basePowerOfTwo - shiftBits))
                }
                originalSize + wordsNeeded - 1 -> {
                    (operand[it - wordsNeeded] shr (basePowerOfTwo - shiftBits))
                }
                else -> {
                    throw RuntimeException("Invalid case $it")
                }
            }
        }
    }

    override fun shiftRight(operand: ULongArray, places: Int): ULongArray {
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val leadingZeroWords = countLeadingZeroWords(operand)
        val realOperandSize = operand.size - leadingZeroWords
        val shiftBits = (places % basePowerOfTwo)
        val wordsToDiscard = places / basePowerOfTwo
        if (wordsToDiscard >= realOperandSize) {
            return ZERO
        }

        if (shiftBits == 0) {
            operand.copyOfRange(realOperandSize - wordsToDiscard, realOperandSize)
        }

        if (realOperandSize > 1 && realOperandSize - wordsToDiscard == 1) {
            return ulongArrayOf((operand[realOperandSize - 1] shr shiftBits))
        }

        val newLength = realOperandSize - wordsToDiscard
        if (newLength == 0) {
            return ZERO
        }

        val result = ULongArray(realOperandSize - wordsToDiscard) {
            when (it) {
                in 0 until (realOperandSize - 1 - wordsToDiscard) -> {
                    ((operand[it + wordsToDiscard] shr shiftBits)) or
                        ((operand[it + wordsToDiscard + 1] shl (basePowerOfTwo - shiftBits) and baseMask))
                }
                realOperandSize - 1 - wordsToDiscard -> {
                    (operand[it + wordsToDiscard] shr shiftBits)
                }
                else -> {
                    throw RuntimeException("Invalid case $it")
                }
            }
        }
        return result
    }

    fun compareWithStartIndexes(first: ULongArray, second: ULongArray, firstStart: Int, secondStart: Int): Int {
        // debugOperandsCheck(first, second)

        if (firstStart > secondStart) {
            return 1
        }
        if (secondStart > firstStart) {
            return -1
        }

        var counter = firstStart - 1
        var firstIsLarger = false
        var bothAreEqual = true
        while (counter >= 0) {
            if (first[counter] > second[counter]) {
                firstIsLarger = true
                bothAreEqual = false
                break
            }
            if (first[counter] < second[counter]) {
                firstIsLarger = false
                bothAreEqual = false
                break
            }
            counter--
        }
        if (bothAreEqual) {
            return 0
        }
        if (firstIsLarger) {
            return 1
        } else {
            return -1
        }
    }

    override fun compare(first: ULongArray, second: ULongArray): Int {
        val firstStart = first.size - countLeadingZeroWords(first)
        val secondStart = second.size - countLeadingZeroWords(second)
        return compareWithStartIndexes(first, second, firstStart, secondStart)
    }

    override fun numberOfDecimalDigits(operand: ULongArray): Long {
        val bitLenght = bitLength(operand)
        val minDigit = ceil((bitLenght - 1) * BigInteger.LOG_10_OF_2)
//        val maxDigit = floor(bitLenght * LOG_10_OF_2) + 1
//        val correct = this / 10.toBigInteger().pow(maxDigit.toInt())
//        return when {
//            correct == ZERO -> maxDigit.toInt() - 1
//            correct > 0 && correct < 10 -> maxDigit.toInt()
//            else -> -1
//        }

        var tmp = operand / pow(TEN, minDigit.toLong())
        var counter = 0L
        while (compare(tmp, ZERO) != 0) {
            tmp /= TEN
            counter++
        }
        return counter + minDigit.toInt()
    }

    // private fun addWithInPlaceBuffer TODO start here

    override fun add(first: ULongArray, second: ULongArray): ULongArray {
        // debugOperandsCheck(first, second)
        if (first.size == 1 && first[0] == 0UL) return second
        if (second.size == 1 && second[0] == 0UL) return first

        val firstStart = first.size - countLeadingZeroWords(first)
        val secondStart = second.size - countLeadingZeroWords(second)

        val (largerLength, smallerLength, largerData, smallerData, largerStart, smallerStart) = if (firstStart > secondStart) {
            Sextuple(first.size, second.size, first, second, firstStart, secondStart)
        } else {
            Sextuple(second.size, first.size, second, first, secondStart, firstStart)
        }
        val result = ULongArray(largerStart + 1) { 0u }
        var i = 0
        var sum: ULong = 0u
        while (i < smallerStart) {
            sum = sum + largerData[i] + smallerData[i]
            result[i] = sum and baseMask
            sum = sum shr 63
            i++
        }

        while (true) {
            if (sum == 0UL) {
                while (i < largerStart) {
                    result[i] = largerData[i]
                    i++
                }
                val final = if (result[result.size - 1] == 0UL) {
                    if ((result.size - 1) == 0) {
                        return ZERO
                    }
                    result.copyOfRange(0, result.size - 1)
                } else {
                    result
                }
                return final
            }
            if (i == largerLength) {
                result[largerLength] = sum
                return result
            }

            sum = sum + largerData[i]
            result[i] = (sum and baseMask)
            sum = sum shr 63
            i++
        }
    }

    fun subtractWithStartIndexes(
        first: ULongArray,
        second: ULongArray,
        firstStart: Int,
        secondStart: Int
    ): ULongArray {
        val comparison = compareWithStartIndexes(first, second, firstStart, secondStart)

        val firstSize = firstStart + 1
        val secondSize = secondStart + 1

        val firstIsLarger = comparison == 1

        if (comparison == 0) return ZERO

        if (secondSize == 1 && second[0] == 0UL) {
            return first
        }

        // Lets throw this just to catch when we didn't prepare the operands correctly
        if (!firstIsLarger) {
            throw RuntimeException("subtract result less than zero")
        }
        val (largerData, smallerData, largerStart, smallerStart) = if (firstIsLarger) {
            Quadruple(first, second, firstStart, secondStart)
        } else {
            Quadruple(second, first, secondStart, firstStart)
        }
        val result = ULongArray(largerStart) { 0U }
        var i = 0
        var diff: ULong = 0u
        while (i < smallerStart) {
            diff = largerData[i] - smallerData[i] - diff
            result[i] = (diff and baseMask)
            diff = diff shr 63
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i] - diff
            result[i] = (diff and baseMask)
            diff = diff shr 63
            i++
        }

        while (i < largerStart) {
            result[i] = largerData[i]
            i++
        }

        if (countLeadingZeroWords(result) == (result.size - 1) && result[0] == 0UL) {
            return ZERO
        }

        return result
    }

    override fun subtract(first: ULongArray, second: ULongArray): ULongArray {
        // debugOperandsCheck(first, second)
        val firstStart = first.size - countLeadingZeroWords(first)
        val secondStart = second.size - countLeadingZeroWords(second)
        return subtractWithStartIndexes(first, second, firstStart, secondStart)
    }

    override fun multiply(first: ULongArray, second: ULongArray): ULongArray {
        // debugOperandsCheck(first, second)
        val firstRealSize = first.size - countLeadingZeroWords(first)
        val seondRealSize = second.size - countLeadingZeroWords(second)
        if (first.contentEquals(ZERO) || second.contentEquals(ZERO)) {
            return ZERO
        }

        if ((firstRealSize >= karatsubaThreshold || seondRealSize >= karatsubaThreshold) &&
            (firstRealSize <= toomCookThreshold || seondRealSize < toomCookThreshold)
        ) {
            return karatsubaMultiply(first, second)
        }

        if (firstRealSize >= toomCookThreshold && seondRealSize >= toomCookThreshold) {
            return toomCook3Multiply(first, second)
        }
        return basecaseMultiply(first, second)
    }

    fun basecaseMultiply(first: ULongArray, second: ULongArray): ULongArray {
        val secondStart = second.size - countLeadingZeroWords(second)
        var resultArray = ZERO
        second.forEachIndexed { index: Int, element: ULong ->
            if (index > secondStart) {
                resultArray
            } else {
                resultArray = resultArray + (baseMultiply(first, element) shl (index * basePowerOfTwo))
            }
        }
        return resultArray
    }

    fun combaMultiply(first: ULongArray, second: ULongArray) {
        // TODO
    }

    fun karatsubaMultiply(firstUnsigned: ULongArray, secondUnsigned: ULongArray): ULongArray {
        val first = SignedULongArray(firstUnsigned, true)
        val second = SignedULongArray(secondUnsigned, true)
        val firstRealSize = first.unsignedValue.size - countLeadingZeroWords(first.unsignedValue)
        val secondRealSize = second.unsignedValue.size - countLeadingZeroWords(second.unsignedValue)
        val halfLength = (kotlin.math.max(firstRealSize, secondRealSize) + 1) / 2

        val mask = (ONE shl (halfLength * wordSizeInBits)) - 1UL
        val firstLower = first and mask
        val firstHigher = first shr halfLength * wordSizeInBits
        val secondLower = second and mask
        val secondHigher = second shr halfLength * wordSizeInBits

        val higherProduct = firstHigher * secondHigher
        val lowerProduct = firstLower * secondLower
        val middleProduct = (firstHigher + firstLower) * (secondHigher + secondLower)
        val result =
            (higherProduct shl (2 * wordSizeInBits * halfLength)) + ((middleProduct - higherProduct - lowerProduct) shl (wordSizeInBits * halfLength)) + lowerProduct

        return result.unsignedValue
    }

    fun prependULongArray(original: ULongArray, numberOfWords: Int, value: ULong): ULongArray {

        return ULongArray(original.size + numberOfWords) {
            when {
                it < numberOfWords -> value
                else -> original[it - numberOfWords]
            }
        }
    }

    fun extendULongArray(original: ULongArray, numberOfWords: Int, value: ULong): ULongArray {

        return ULongArray(original.size + numberOfWords) {
            when {
                it < original.size -> original[it]
                else -> value
            }
        }
    }

    @Suppress("DuplicatedCode")
    fun toomCook3Multiply(firstUnchecked: ULongArray, secondUnchecked: ULongArray): ULongArray {
        val first = if (firstUnchecked.size % 3 != 0) {
            firstUnchecked.plus(ULongArray((((firstUnchecked.size + 2) / 3) * 3) - firstUnchecked.size) { 0U }.asIterable())
        } else {
            firstUnchecked
        }.toULongArray()

        val second = if (secondUnchecked.size % 3 != 0) {
            secondUnchecked.plus(ULongArray((((secondUnchecked.size + 2) / 3) * 3) - secondUnchecked.size) { 0U }.asIterable())
        } else {
            secondUnchecked
        }.toULongArray()
        val firstLength = first.size
        val secondLength = second.size

        val (firstPrepared, secondPrepared) = when {
            firstLength > secondLength -> {
                val prepared = extendULongArray(second, firstLength - secondLength, 0U)
                Pair(first, prepared)
            }
            firstLength < secondLength -> {
                val prepared = extendULongArray(first, secondLength - firstLength, 0U)
                Pair(prepared, second)
            }
            else -> Pair(first, second)
        }

        val longestLength = kotlin.math.max(first.size, second.size)

        val extendedDigit = (longestLength + 2) / 3

        val m0 = SignedULongArray(firstPrepared.slice(0 until extendedDigit).toULongArray(), true)
        val m1 = SignedULongArray(firstPrepared.slice(extendedDigit until extendedDigit * 2).toULongArray(), true)
        val m2 = SignedULongArray(firstPrepared.slice(extendedDigit * 2 until extendedDigit * 3).toULongArray(), true)

        val n0 = SignedULongArray(secondPrepared.slice(0 until extendedDigit).toULongArray(), true)
        val n1 = SignedULongArray(secondPrepared.slice(extendedDigit until extendedDigit * 2).toULongArray(), true)
        val n2 = SignedULongArray(secondPrepared.slice(extendedDigit * 2 until extendedDigit * 3).toULongArray(), true)

        val p0 = m0 + m2
        // p(0)
        val pe0 = m0
        // p(1)
        val pe1 = p0 + m1
        // p(-1)
        val pem1 = p0 - m1
        // p(-2)
        val doublePemM2 = (pem1 + m2) * SIGNED_POSITIVE_TWO
        val pem2 = doublePemM2 - m0
        // p(inf)
        val pinf = m2

        val q0 = n0 + n2
        // q(0)
        val qe0 = n0
        // q(1)
        val qe1 = q0 + n1
        // q(-1)
        val qem1 = q0 - n1
        // q(-2)
        val doubleQemN2 = (qem1 + n2) * SIGNED_POSITIVE_TWO
        val qem2 = doubleQemN2 - n0
        // q(inf)
        val qinf = n2

        val re0 = pe0 * qe0
        val re1 = pe1 * qe1
        val rem1 = pem1 * qem1
        val rem2 = pem2 * qem2
        val rinf = pinf * qinf

        var r0 = re0
        var r4 = rinf
        val rem2re1diff = (rem2 - re1)
        // var r3 = SignedULongArray(exactDivideBy3(rem2re1diff.unsignedValue), rem2re1diff.sign)
        var r3 = rem2re1diff / SignedULongArray(ulongArrayOf(3U), true)
        // println("R3 ${r3.sign} ${r3.unsignedValue}")
        var r1 = (re1 - rem1) shr 1
        var r2 = rem1 - r0
        r3 = ((r2 - r3) shr 1) + SIGNED_POSITIVE_TWO * rinf
        r2 = r2 + r1 - r4
        r1 = r1 - r3

        val bShiftAmount = extendedDigit * 63
        val rb0 = r0
        val rb1 = (r1 shl (bShiftAmount))
        val rb2 = (r2 shl (bShiftAmount * 2))
        val rb3 = (r3 shl (bShiftAmount * 3))
        val rb4 = (r4 shl (bShiftAmount * 4))
        val rb = rb0 +
            rb1 +
            rb2 +
            rb3 +
            rb4

        return rb.unsignedValue
    }

    // Signed operations TODO evaluate if we really want to do this to support Toom-Cook or just move it out of arithmetic

    data class SignedULongArray(val unsignedValue: ULongArray, val sign: Boolean)

    private fun signedAdd(first: SignedULongArray, second: SignedULongArray) = if (first.sign xor second.sign) {
        if (first.unsignedValue > second.unsignedValue) {
            SignedULongArray(first.unsignedValue - second.unsignedValue, first.sign)
        } else {
            SignedULongArray(second.unsignedValue - first.unsignedValue, second.sign)
        }
    } else {
        // Same sign
        SignedULongArray(first.unsignedValue + second.unsignedValue, first.sign)
    }

    val SIGNED_POSITIVE_TWO = SignedULongArray(TWO, true)

    private fun signedSubtract(first: SignedULongArray, second: SignedULongArray) =
        signedAdd(first, second.copy(sign = !second.sign))

    private fun signedMultiply(first: SignedULongArray, second: SignedULongArray) =
        SignedULongArray(first.unsignedValue * second.unsignedValue, !(first.sign xor second.sign))

    private fun signedDivide(first: SignedULongArray, second: SignedULongArray) =
        SignedULongArray(first.unsignedValue / second.unsignedValue, !(first.sign xor second.sign))

    private fun signedRemainder(first: SignedULongArray, second: SignedULongArray) =
        SignedULongArray(first.unsignedValue % second.unsignedValue, !(first.sign xor second.sign))

    internal operator fun SignedULongArray.plus(other: SignedULongArray): SignedULongArray {
        return signedAdd(this, other)
    }

    internal operator fun SignedULongArray.minus(other: SignedULongArray): SignedULongArray {
        return signedSubtract(this, other)
    }

    internal operator fun SignedULongArray.times(other: SignedULongArray): SignedULongArray {
        return signedMultiply(this, other)
    }

    internal operator fun SignedULongArray.div(other: SignedULongArray): SignedULongArray {
        return signedDivide(this, other)
    }

    internal operator fun SignedULongArray.rem(other: SignedULongArray): SignedULongArray {
        return signedRemainder(this, other)
    }

    internal infix fun SignedULongArray.shr(places: Int) = SignedULongArray(unsignedValue shr places, sign)

    internal infix fun SignedULongArray.shl(places: Int) = SignedULongArray(unsignedValue shl places, sign)

    internal infix fun SignedULongArray.and(operand: ULongArray) = SignedULongArray(and(unsignedValue, operand), sign)

    // End of signed operations

    fun fftMultiply(first: ULongArray, second: ULongArray): ULongArray {
        return first
    }

    fun baseMultiply(first: ULongArray, second: ULong): ULongArray {

        val firstStart = first.size - countLeadingZeroWords(first)

        val secondLow = second and lowMask
        val secondHigh = second shr 32

        val result = ULongArray(firstStart + 1)

        var carryIntoNextRound = 0UL
        var i = 0
        var j = 0
        while (i < firstStart) {
            val firstLow = first[i] and lowMask
            val firstHigh = first[i] shr 32
            i++

            // Calculate low part product
            val lowerProduct = (firstLow * secondLow)
            var lowerCarry = lowerProduct shr 63
            var lowResult = carryIntoNextRound + (lowerProduct and baseMask)
            lowerCarry += lowResult shr 63
            lowResult = lowResult and baseMask

            val middleProduct = firstLow * secondHigh + secondLow * firstHigh
            var middleCarry = lowerCarry
            middleCarry += (middleProduct shr 31)
            lowResult += (middleProduct shl 32) and baseMask
            middleCarry += (lowResult shr 63)

            result[j] = lowResult and baseMask

            var highResult = middleCarry
            val higherProduct = (firstHigh * secondHigh) shl 1
            highResult = highResult + higherProduct

            carryIntoNextRound = highResult
            j++
        }
        if (carryIntoNextRound != 0UL) {
            result[j] = carryIntoNextRound
        }
        return result
    }

    /*
    Useful when we want to do a ULong * ULong -> ULongArray
     */
    fun multiply(first: ULong, second: ULong): ULongArray {
        if (first == 0UL || second == 0UL) {
            return ulongArrayOf(0UL)
        }
        // Split the operands
        val firstLow = first and lowMask
        val firstHigh = first shr 32
        val secondLow = second and lowMask
        val secondHigh = second shr 32

        // Calculate low part product
        val lowerProduct = firstLow * secondLow
        val lowCarry = lowerProduct shr 63
        var lowResult = lowerProduct and baseMask

        val middleProduct = firstLow * secondHigh + secondLow * firstHigh
        var middleCarry = lowCarry
        middleCarry += (middleProduct shr 31)
        lowResult += (middleProduct shl 32) and baseMask
        middleCarry += (lowResult shr 63)

        var highResult = middleCarry
        val higherProduct = (firstHigh * secondHigh) shl 1
        highResult = highResult + higherProduct

        return removeLeadingZeros(ulongArrayOf(lowResult and baseMask, highResult))
    }

    override fun pow(base: ULongArray, exponent: Long): ULongArray {
        if (exponent == 0L) {
            return ONE
        }
        if (exponent == 1L) {
            return base
        }
        if (base.size == 1 && base[0] == 10UL && exponent < powersOf10.size) {
            return powersOf10[exponent.toInt()]
        }
        return (0 until exponent).fold(ONE) { acc, _ ->
            acc * base
        }
    }

    fun normalize(dividend: ULongArray, divisor: ULongArray): Triple<ULongArray, ULongArray, Int> {
        val divisorSize = divisor.size
        val normalizationShift = numberOfLeadingZerosInAWord(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)
    }

    fun normalize(operand: ULongArray): Pair<ULongArray, Int> {
        val normalizationShift = numberOfLeadingZerosInAWord(operand[operand.size - 1])
        return Pair(operand.shl(normalizationShift), normalizationShift)
    }

    fun denormalize(
        remainderNormalized: ULongArray,
        normalizationShift: Int
    ): ULongArray {
        val remainder = remainderNormalized shr normalizationShift
        return remainder
    }

    /**
     * Based on Basecase DivRem algorithm from
     * Modern Computer Arithmetic, Richard Brent and Paul Zimmermann, Cambridge University Press, 2010.
     * Version 0.5.9
     * https://members.loria.fr/PZimmermann/mca/pub226.html
     */
    fun baseDivide(
        unnormalizedDividend: ULongArray,
        unnormalizedDivisor: ULongArray
    ): Pair<ULongArray, ULongArray> {
        if (unnormalizedDivisor > unnormalizedDividend) {
            return Pair(ZERO, unnormalizedDividend)
        }
        if (unnormalizedDivisor.size == 1 && unnormalizedDividend.size == 1) {
            return Pair(
                removeLeadingZeros(
                    ulongArrayOf(
                        unnormalizedDividend[0] / unnormalizedDivisor[0]
                    )
                ),
                removeLeadingZeros(
                    ulongArrayOf(
                        unnormalizedDividend[0] % unnormalizedDivisor[0]
                    )
                )
            )
        }
        val bitPrecision = bitLength(unnormalizedDividend) - bitLength(
            unnormalizedDivisor
        )
        if (bitPrecision == 0) {
            return Pair(ONE, unnormalizedDividend - unnormalizedDivisor)
        }

        var (dividend, divisor, normalizationShift) = normalize(
            unnormalizedDividend,
            unnormalizedDivisor
        )
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        var wordPrecision = dividendSize - divisorSize

        var qjhat: ULongArray
        var reconstructedQuotient: ULongArray
        var quotient = ULongArray(wordPrecision)

        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision * basePowerOfTwo))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient = ULongArray(wordPrecision + 1)
            quotient[wordPrecision] = 1U
            dividend = dividend - divisorTimesBaseToPowerOfM
        }

        for (j in (wordPrecision - 1) downTo 0) {
            val twoDigit = if (divisorSize + j < dividend.size) {
                ((ulongArrayOf(dividend[divisorSize + j]) shl basePowerOfTwo) + dividend[divisorSize + j - 1])
            } else {
                if (divisorSize + j == dividend.size) {
                    ulongArrayOf(dividend[divisorSize + j - 1])
                } else {
                    ZERO
                }
            }
            val convertedResult =
                BigInteger32Arithmetic.divide(twoDigit.to32Bit(), ulongArrayOf(divisor[divisorSize - 1]).to32Bit())
            qjhat = convertedResult.first.from32Bit()
            quotient[j] = if (qjhat < (baseMask - 1UL)) {
                qjhat[0]
            } else {
                baseMask
            }
            // We don't have signed integers here so we need to check if reconstructed quotient is larger than the dividend
            // instead of just doing  (dividend = dividend − qj * β^j * divisor) and then looping. Final effect is the same.
            reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            while (reconstructedQuotient > dividend) {
                quotient[j] = quotient[j] - 1U
                reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            }

            dividend = dividend - reconstructedQuotient
        }

        while (dividend >= divisor) {
            quotient += 1UL
            dividend -= divisor
        }
        val denormRemainder =
            denormalize(dividend, normalizationShift)
        return Pair(removeLeadingZeros(quotient), denormRemainder)
    }

    fun basicDivide2(
        unnormalizedDividend: ULongArray,
        unnormalizedDivisor: ULongArray
    ): Pair<ULongArray, ULongArray> {
        var (a, b, shift) = normalize(unnormalizedDividend, unnormalizedDivisor)
        val m = a.size - b.size
        val bmb = b shl (m * wordSizeInBits)
        var q = ULongArray(m + 1) { 0U }
        if (a > bmb) {
            q[m] = 1U
            a = a - bmb
        }
        var qjhat = ZERO
        var qjhatULong = ZERO
        var bjb = ZERO
        var delta = ZERO
        for (j in m - 1 downTo 0) {
            qjhatULong = BigInteger32Arithmetic.divide(
                (a.copyOfRange(b.size - 1, b.size + 1)).to32Bit(),
                ulongArrayOf(b[b.size - 1]).to32Bit()
            ).first.from32Bit()
            q[j] = min(qjhatULong, baseMaskArray)[0]
            bjb = b shl (j * BigInteger32Arithmetic.wordSizeInBits)
            val qjBjb = (b * q[j]) shl (j * wordSizeInBits)
            if (qjBjb > a) {
                delta = qjBjb - a
                while (delta > qjBjb) {
                    q[j] = q[j] - 1U
                    delta = delta - bjb
                }
                // quotient is now such that q[j] * b*B^j won't be larger than divisor
                a = a - (b * q[j]) shl (j * BigInteger32Arithmetic.wordSizeInBits)
            } else {
                a = a - qjBjb
            }
        }
        val denormRemainder =
            denormalize(a, shift)
        return Pair(removeLeadingZeros(q), denormRemainder)
    }

    /**
     * When division is known to be exact ( no remainder, we can use this, especially in Toom-Cook)
     * TODO Need to move modInverse from BigInteger to arithmetic, and then replace here
     */
    fun exactDivideBy3(operand: ULongArray): ULongArray {
        val base = BigInteger.ONE.shl(operand.size * 63)
        val creator = ModularBigInteger.creatorForModulo(base)
        val reciprocalOf3 = creator.fromInt(3).inverse()
        val multipliedByInverse = multiply(operand, reciprocalOf3.toBigInteger().magnitude.toULongArray())
        return multipliedByInverse.slice(operand.indices).toULongArray()
    }

    fun exactDivideBy3Better(operand: ULongArray): ULongArray {
        // TODO
        return operand
    }

    override fun reciprocal(operand: ULongArray): Pair<ULongArray, ULongArray> {
        return d1ReciprocalRecursiveWordVersion(operand)
    }

    fun d1ReciprocalRecursive(a: ULongArray): Pair<ULongArray, ULongArray> {
        val fullBitLenght = bitLength(a)
        val n = if (fullBitLenght > 63) {
            fullBitLenght - 63
        } else {
            fullBitLenght
        }
        if (n <= 30) {
            val rhoPowered = 1UL shl (n * 2)
            val longA = a[0]
            val x = rhoPowered / longA
            val r = rhoPowered - x * longA
            return Pair(ulongArrayOf(x), ulongArrayOf(r))
        }
        val l = floor((n - 1).toDouble() / 2).toInt()
        val h = n - l
        val mask = (ONE shl l) - ONE
        val ah = a shr l
        val al = and(a, mask)
        var (xh, rh) = d1ReciprocalRecursive(ah)
        val s = al * xh
//        val rhoL = (ONE shl l)
        val rhRhoL = rh shl l
        val t = if (rhRhoL >= s) {
            rhRhoL - s
        } else {
            xh = xh - ONE
            (rhRhoL + a) - s
        }
        val tm = t shr h
        val d = (xh * tm) shr h
        var x = (xh shl l) + d
        var r = (t shl l) - a * d
        if (r >= a) {
            x = x + ONE
            r = r - a
            if (r >= a) {
                x = x + ONE
                r = r - a
            }
        }
        return Pair(x, r)
    }

    fun d1ReciprocalRecursiveWordVersion(a: ULongArray): Pair<ULongArray, ULongArray> {
        val n = a.size - 1
        if (n <= 2) {
            val corrected = if (n == 0) {
                1
            } else {
                n
            }
            val rhoPowered = ONE shl (corrected * 2 * wordSizeInBits)
            val x = rhoPowered / a
            val r = rhoPowered - (x * a)
            return Pair(x, r)
        }
        val l = floor((n - 1).toDouble() / 2).toInt()
        val h = n - l
        val ah = a.copyOfRange(a.size - h - 1, a.size)
        val al = a.copyOfRange(0, l)
        var (xh, rh) = d1ReciprocalRecursiveWordVersion(ah)
        val s = al * xh
//        val rhoL = (ONE shl l)
        val rhRhoL = rh shl (l * wordSizeInBits)
        val t = if (rhRhoL >= s) {
            rhRhoL - s
        } else {
            xh = xh - ONE
            (rhRhoL + a) - s
        }
        val tm = t shr (h * wordSizeInBits)
        val d = (xh * tm) shr (h * wordSizeInBits)
        var x = (xh shl (l * wordSizeInBits)) + d
        var r = (t shl (l * wordSizeInBits)) - a * d
        if (r >= a) {
            x = x + ONE
            r = r - a
            if (r >= a) {
                x = x + ONE
                r = r - a
            }
        }
        return Pair(x, r)
    }

    private fun unbalancedReciprocal(a: ULongArray, diff: Int): Pair<ULongArray, ULongArray> {
        val n = a.size - 1 - diff
        val a0 = a.copyOfRange(n + 1, a.size)
        val a1 = a.copyOfRange(0, n)
        var (x, r) = d1ReciprocalRecursiveWordVersion(a0)
        if (x == ONE shl (n * 63)) {
            if (a1.compareTo(ZERO) == 0) {
                r = ZERO
            } else {
                x = x - ONE
                r = a - (a1 shl (n * 63))
            }
        } else {
            val rRhoD = r shl diff
            val a1x = a1 * x
            if (rRhoD > a1x) {
                r = rRhoD - a1x
            } else {
                x = x - ONE
                r = rRhoD - (a1 * x)
            }
        }
        return Pair(x, r)
    }

    internal fun convertTo64BitRepresentation(operand: ULongArray): ULongArray {
        if (operand == ZERO) return ZERO
        val length = bitLength(operand)
        val requiredLength = if (length % 64 == 0) {
            length / 64
        } else {
            (length / 64) + 1
        }
        var wordStep: Int
        var shiftAmount: Int

        val result = ULongArray(requiredLength)
        for (i in 0 until requiredLength) {
            wordStep = i / 63
            shiftAmount = i % 63
            if (i + wordStep + 1 < operand.size) {
                result[i] =
                    (operand[i + wordStep] shr shiftAmount) or ((operand[i + wordStep + 1] shl (63 - shiftAmount)))
            } else {
                result[i] = (operand[i + wordStep] shr shiftAmount)
            }
        }

        return result
    }

    internal fun convertTo32BitRepresentation(operand: ULongArray): UIntArray {
        val power64Representation = convertTo64BitRepresentation(operand)
        val result = UIntArray(power64Representation.size * 2)
        for (i in 0 until power64Representation.size) {
            result[2 * i] = (power64Representation[i] and BigInteger32Arithmetic.base.toULong()).toUInt()
            result[2 * i + 1] = (power64Representation[i] shr 32).toUInt()
        }

        return BigInteger32Arithmetic.removeLeadingZeros(result)
    }

    internal fun convertFrom32BitRepresentation(operand: UIntArray): ULongArray {
        if (operand.size == 0) {
            return ZERO
        }
        if (operand.size == 1) {
            return ulongArrayOf(operand[0].toULong())
        }
        val length = BigInteger32Arithmetic.bitLength(operand)
        val requiredLength = if (length % 63 == 0) {
            length / 63
        } else {
            (length / 63) + 1
        }

        val result = ULongArray(requiredLength)
        var skipWordCount: Int
        for (i in 0 until requiredLength) {
            skipWordCount = i / 32
            val shiftAmount = i % 32
            val position = (i * 2) - skipWordCount
            if (requiredLength == 2) {
                result[0] = operand[0].toULong() or ((operand[1].toULong() shl 32) and highMask)
                result[i] =
                    (operand[1].toULong() shr 31) or (operand[2].toULong() shl 1) or (operand[3].toULong() shl 33)
            } else {
                when (i) {
                    0 -> {
                        result[i] = operand[0].toULong() or ((operand[1].toULong() shl 32) and highMask)
                    }
                    in 1 until requiredLength - 1 -> {
                        result[i] =
                            (operand[position - 1].toULong() shr (32 - shiftAmount)) or
                                (operand[position].toULong() shl shiftAmount) or
                                ((operand[position + 1].toULong() shl (32 + shiftAmount)) and highMask)
                    }
                    requiredLength - 1 -> {
                        if (position < operand.size) {
                            result[i] =
                                (operand[position - 1].toULong() shr (32 - shiftAmount)) or
                                    (operand[position].toULong() shl shiftAmount)
                        } else {
                            result[i] =
                                (operand[position - 1].toULong() shr (32 - shiftAmount))
                        }
                    }
                }
            }
        }

        return result
    }

    override fun divide(first: ULongArray, second: ULongArray): Pair<ULongArray, ULongArray> {
        // debugOperandsCheck(first, second)
        return baseDivide(first, second)
    }

    internal fun reciprocalDivision(first: ULongArray, second: ULongArray): Pair<ULongArray, ULongArray> {
        if (first.size < second.size) {
            throw RuntimeException("Invalid division: ${first.size} words / ${second.size} words")
        }
        val shift = if (second.size == 1) {
            1
        } else {
            second.size - 1
        }
        val precisionExtension = (first.size - second.size + 1)
        val secondHigherPrecision = ULongArray(second.size + precisionExtension) {
            when {
                it >= precisionExtension -> second[it - precisionExtension]
                else -> 0UL
            }
        }

        val secondReciprocalWithRemainder = d1ReciprocalRecursiveWordVersion(secondHigherPrecision)

        val secondReciprocal = secondReciprocalWithRemainder.first
        var product = first * secondReciprocal
        // TODO Proper rounding
        if (product.compareTo(0UL) == 0) {
            return Pair(ZERO, first)
        }
        if (product.size == 1) {
            if (product >= baseMask - 1UL) {
                product = product + ONE
            }
        } else {
            val importantWord = product[product.size - second.size]
            if (importantWord >= baseMask) {
                product = ULongArray(product.size) {
                    when (it) {
                        product.size - 1 -> product[product.size - 1] + 1UL
                        else -> 0UL
                    }
                }
            }
        }

        val result = product.copyOfRange(2 * shift + precisionExtension, product.size)
        val remainder = first - (result * second)
        return Pair(result, remainder)
    }

    override fun sqrt(operand: ULongArray): Pair<ULongArray, ULongArray> {
        return reqursiveSqrt(operand)
    }

    private fun reqursiveSqrt(operand: ULongArray): Pair<ULongArray, ULongArray> {
        val n = operand.size
        val l = floor((n - 1).toDouble() / 4).toInt()
        if (l == 0) {
            return basecaseSqrt(operand)
        }
        val step = n / 4
        val stepRemainder = n % 4
        val baseLPowerShift = 63 * l
        val a1 = operand.copyOfRange(n - ((3 * step) + stepRemainder), n - ((2 * step) + stepRemainder))
        val a0 = operand.copyOfRange(0, n - ((3 * step) + stepRemainder))
        val a3a2 = operand.copyOfRange(n - ((2 * step) + stepRemainder), n)

        val (sPrim, rPrim) = reqursiveSqrt(a3a2)
        val (q, u) = ((rPrim shl baseLPowerShift) + a1) divrem (sPrim shl 1)
        var s = (sPrim shl baseLPowerShift) + q
        var r = (u shl baseLPowerShift) + a0 - (q * q)
        return Pair(s, r)
    }

    internal fun basecaseSqrt(operand: ULongArray): Pair<ULongArray, ULongArray> {
        val sqrt = sqrtInt(operand)
        val remainder = operand - (sqrt * sqrt)
        return Pair(sqrt, remainder)
    }

    internal fun sqrtInt(operand: ULongArray): ULongArray {
        var u = operand
        var s = ZERO
        var tmp = ZERO
        do {
            s = u
            tmp = s + (operand / s)
            u = tmp shr 1
        } while (u < s)
        return s
    }

    override fun gcd(first: ULongArray, second: ULongArray): ULongArray {
        return if (first.size > 150 || second.size > 150) {
            euclideanGcd(first, second)
        } else {
            binaryGcd(first, second)
        }
    }

    private fun euclideanGcd(first: ULongArray, second: ULongArray): ULongArray {
        var u = first
        var v = second
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
    }

    private tailrec fun binaryGcd(first: ULongArray, second: ULongArray): ULongArray {
        // debugOperandsCheck(first, second)
        if (first.contentEquals(second)) {
            return first
        }

        if (first.contentEquals(ZERO)) {
            return second
        }

        if (second.contentEquals(ZERO)) {
            return first
        }

        if (and(first, ONE).contentEquals(ZERO)) { // first is even
            if (and(second, ONE).contentEquals(ZERO)) { // second is even
                return binaryGcd(first shr 1, second shr 1) shl 1
            } else { // second is odd
                return binaryGcd(first shr 1, second)
            }
        }

        if (and(second, ONE).contentEquals(ZERO)) {
            return binaryGcd(first, second shr 1)
        }

        return if (compare(first, second) == 1) {
            binaryGcd(subtract(first, second) shr 1, second)
        } else {
            binaryGcd(subtract(second, first) shr 1, first)
        }
    }

    fun min(first: ULongArray, second: ULongArray): ULongArray {
        return if (first < second) {
            first
        } else {
            second
        }
    }

    fun max(first: ULongArray, second: ULongArray): ULongArray {
        return if (first > second) {
            first
        } else {
            second
        }
    }

    override fun parseForBase(number: String, base: Int): ULongArray {
        var parsed = ZERO
        number.toLowerCase().forEach { char ->
            parsed = (parsed * base.toULong()) + (char.toDigit()).toULong()
        }
        return removeLeadingZeros(parsed)
    }

    override fun toString(operand: ULongArray, base: Int): String {
        var copy = operand.copyOf()
        val baseArray = ulongArrayOf(base.toULong())
        val stringBuilder = StringBuilder()
        while (copy != ZERO) {
            val divremResult = (copy divrem baseArray)
            if (divremResult.second.isEmpty()) {
                stringBuilder.append(0)
            } else {
                stringBuilder.append(divremResult.second[0].toString(base))
            }

            copy = divremResult.first
        }
        return stringBuilder.toString().reversed()
    }

    override fun and(operand: ULongArray, mask: ULongArray): ULongArray {
        return ULongArray(operand.size) {
                if (it < mask.size) {
                    operand[it] and mask[it]
                } else {
                    0UL
                }
            }
    }

    override fun or(operand: ULongArray, mask: ULongArray): ULongArray {
        return removeLeadingZeros(
            ULongArray(operand.size) {
                if (it < mask.size) {
                    operand[it] or mask[it]
                } else {
                    operand[it]
                }
            }
        )
    }

    override fun xor(operand: ULongArray, mask: ULongArray): ULongArray {
        return removeLeadingZeros(
            ULongArray(operand.size) {
                if (it < mask.size) {
                    operand[it] xor mask[it]
                } else {
                    operand[it] xor 0UL
                }
            }
        )
    }

    override fun not(operand: ULongArray): ULongArray {
        val leadingZeros = numberOfLeadingZerosInAWord(operand[operand.size - 1])
        val cleanupMask = (((1UL shl leadingZeros + 1) - 1U) shl (basePowerOfTwo - leadingZeros)).inv()
        val inverted = ULongArray(operand.size) {
            if (it < operand.size - 2) {
                operand[it].inv() and baseMask
            } else {
                operand[it].inv() and cleanupMask
            }
        }

        return inverted
    }

    // -------------- Bitwise ---------------- //

    internal infix fun ULongArray.shl(places: Int): ULongArray {
        return shiftLeft(this, places)
    }

    internal infix fun ULongArray.shr(places: Int): ULongArray {
        return shiftRight(this, places)
    }

    override fun bitAt(operand: ULongArray, position: Long): Boolean {
        if (position / 63 > Int.MAX_VALUE) {
            throw RuntimeException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= operand.size) {
            return false
        }
        val bitPosition = position % 63
        val word = operand[wordPosition.toInt()]
        return (word and (1UL shl bitPosition.toInt()) == 1UL)
    }

    override fun setBitAt(operand: ULongArray, position: Long, bit: Boolean): ULongArray {
        if (position / 63 > Int.MAX_VALUE) {
            throw RuntimeException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= operand.size) {
            throw IndexOutOfBoundsException("Invalid position, addressed word $wordPosition larger than number of words ${operand.size}")
        }
        val bitPosition = position % 63
        val setMask = 1UL shl bitPosition.toInt()
        return ULongArray(operand.size) {
            if (it == wordPosition.toInt()) {
                if (bit) {
                    operand[it] or setMask
                } else {
                    operand[it] xor setMask
                }
            } else {
                operand[it]
            }
        }
    }

    // -------------- Operations ---------------- //

    internal operator fun ULongArray.plus(other: ULongArray): ULongArray {
        return add(this, other)
    }

    internal operator fun ULongArray.minus(other: ULongArray): ULongArray {
        return subtract(this, other)
    }

    internal operator fun ULongArray.times(other: ULongArray): ULongArray {
        return multiply(this, other)
    }

    internal operator fun ULongArray.plus(other: ULong): ULongArray {
        return add(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.minus(other: ULong): ULongArray {
        return subtract(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.times(other: ULong): ULongArray {
        return multiply(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.div(other: ULong): ULongArray {
        return divide(this, ulongArrayOf(other)).first
    }

    internal operator fun ULongArray.rem(other: ULong): ULongArray {
        return divide(this, ulongArrayOf(other)).second
    }

    internal operator fun ULongArray.div(other: ULongArray): ULongArray {
        return divide(this, other).first
    }

    internal operator fun ULongArray.rem(other: ULongArray): ULongArray {
        return divide(this, other).second
    }

    internal infix fun ULongArray.divrem(other: ULongArray): Pair<ULongArray, ULongArray> {
        return divide(this, other)
    }

    internal operator fun ULongArray.compareTo(other: ULongArray): Int {
        return compare(this, other)
    }

    internal operator fun ULongArray.compareTo(other: ULong): Int {
        return compare(this, ulongArrayOf(other))
    }

    internal fun ULongArray.to32Bit(): UIntArray {
        return convertTo32BitRepresentation(this)
    }

    internal fun UIntArray.from32Bit(): ULongArray {
        return convertFrom32BitRepresentation(this)
    }

    override fun fromULong(uLong: ULong): ULongArray {
        return fromLong(uLong.toLong())
    }

    override fun fromUInt(uInt: UInt): ULongArray = ulongArrayOf(uInt.toULong())

    override fun fromUShort(uShort: UShort): ULongArray = ulongArrayOf(uShort.toULong())

    override fun fromUByte(uByte: UByte): ULongArray = ulongArrayOf(uByte.toULong())

    override fun fromLong(long: Long): ULongArray {
        if ((long.absoluteValue.toULong() and overflowMask shr 63) == 1UL) {
            return ulongArrayOf(baseMask) + 1U
        }
        return ulongArrayOf((long.absoluteValue.toULong() and baseMask))
    }

    override fun fromInt(int: Int): ULongArray = ulongArrayOf(int.absoluteValue.toULong())

    override fun fromShort(short: Short): ULongArray = ulongArrayOf(short.toInt().absoluteValue.toULong())

    override fun fromByte(byte: Byte): ULongArray = ulongArrayOf(byte.toInt().absoluteValue.toULong())

    override fun toByteArray(operand: ULongArray, sign: Sign): Array<Byte> {
        return BigInteger32Arithmetic.toByteArray(convertTo32BitRepresentation(operand), sign)
    }

    override fun fromByteArray(byteArray: Array<Byte>): Pair<ULongArray, Sign> {
        val result = BigInteger32Arithmetic.fromByteArray(byteArray)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun fromByteArray(byteArray: ByteArray): Pair<ULongArray, Sign> {
        val result = BigInteger32Arithmetic.fromByteArray(byteArray)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun fromUByteArray(uByteArray: Array<UByte>, endianness: Endianness): Pair<ULongArray, Sign> {
        val result = BigInteger32Arithmetic.fromUByteArray(uByteArray, endianness)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun fromUByteArray(uByteArray: UByteArray, endianness: Endianness): Pair<ULongArray, Sign> {
        val result = BigInteger32Arithmetic.fromUByteArray(uByteArray, endianness)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun toTypedUByteArray(operand: ULongArray, endianness: Endianness): Array<UByte> {
        val result = BigInteger32Arithmetic.toTypedUByteArray(convertTo32BitRepresentation(operand), endianness)
        return result
    }

    override fun toUByteArray(operand: ULongArray, endianness: Endianness): UByteArray {
        val result = BigInteger32Arithmetic.toUByteArray(convertTo32BitRepresentation(operand), endianness)
        return result
    }

    private fun debugOperandsCheck(first: ULongArray, second: ULongArray) {
        if (debugOperandSize && (first.isEmpty() || second.isEmpty())) {
            throw RuntimeException("Empty operands")
        }
    }

    // ------------- Useful constants --------------
    val powersOf10 = arrayOf(
        ulongArrayOf(1UL),
        ulongArrayOf(10UL),
        ulongArrayOf(100UL),
        ulongArrayOf(1000UL),
        ulongArrayOf(10000UL),
        ulongArrayOf(100000UL),
        ulongArrayOf(1000000UL),
        ulongArrayOf(10000000UL),
        ulongArrayOf(100000000UL),
        ulongArrayOf(1000000000UL),
        ulongArrayOf(10000000000UL),
        ulongArrayOf(100000000000UL),
        ulongArrayOf(1000000000000UL),
        ulongArrayOf(10000000000000UL),
        ulongArrayOf(100000000000000UL),
        ulongArrayOf(1000000000000000UL),
        ulongArrayOf(10000000000000000UL),
        ulongArrayOf(100000000000000000UL),
        ulongArrayOf(1000000000000000000UL),
        ulongArrayOf(776627963145224192UL, 1UL),
        ulongArrayOf(7766279631452241920UL, 10UL),
        ulongArrayOf(3875820019684212736UL, 108UL),
        ulongArrayOf(1864712049423024128UL, 1084UL),
        ulongArrayOf(200376420520689664UL, 10842UL),
        ulongArrayOf(2003764205206896640UL, 108420UL),
        ulongArrayOf(1590897978359414784UL, 1084202UL),
        ulongArrayOf(6685607746739372032UL, 10842021UL),
        ulongArrayOf(2292473209410289664UL, 108420217UL),
        ulongArrayOf(4477988020393345024UL, 1084202172UL),
        ulongArrayOf(7886392056514347008UL, 10842021724UL),
        ulongArrayOf(5076944270305263616UL, 108420217248UL),
        ulongArrayOf(4652582518778757120UL, 1084202172485UL),
        ulongArrayOf(408965003513692160UL, 10842021724855UL),
        ulongArrayOf(4089650035136921600UL, 108420217248550UL),
        ulongArrayOf(4003012203950112768UL, 1084202172485504UL),
        ulongArrayOf(3136633892082024448UL, 10842021724855044UL),
        ulongArrayOf(3696222810255917056UL, 108420217248550443UL),
        ulongArrayOf(68739955140067328UL, 1084202172485504434UL),
        ulongArrayOf(687399551400673280UL, 1618649688000268532UL, 1UL),
        ulongArrayOf(6873995514006732800UL, 6963124843147909512UL, 11UL),
        ulongArrayOf(4176350882083897344UL, 5067644173495664471UL, 117UL),
        ulongArrayOf(4870020673419870208UL, 4559581550682765674UL, 1175UL),
        ulongArrayOf(2583346549924823040UL, 8702327359408553513UL, 11754UL),
        ulongArrayOf(7386721425538678784UL, 4012925262392552860UL, 117549UL),
        ulongArrayOf(80237960548581376UL, 3235764476506425376UL, 1175494UL),
        ulongArrayOf(802379605485813760UL, 4687528654499926336UL, 11754943UL),
        ulongArrayOf(8023796054858137600UL, 758426360725384320UL, 117549435UL),
        ulongArrayOf(6450984253743169536UL, 7584263607253843208UL, 1175494350UL),
        ulongArrayOf(9169610316303040512UL, 2055659777700225622UL, 11754943508UL),
        ulongArrayOf(8685754831337422848UL, 2109853703292704613UL, 117549435082UL),
        ulongArrayOf(3847199981681246208UL, 2651792959217494523UL, 1175494350822UL),
        ulongArrayOf(1578511669393358848UL, 8071185518465393618UL, 11754943508222UL),
        ulongArrayOf(6561744657078812672UL, 6924878889815729717UL, 117549435082228UL),
        ulongArrayOf(1053842312804696064UL, 4685184640173866521UL, 1175494350822287UL),
        ulongArrayOf(1315051091192184832UL, 734986217464786171UL, 11754943508222875UL),
        ulongArrayOf(3927138875067072512UL, 7349862174647861711UL, 117549435082228750UL),
        ulongArrayOf(2377900603251621888UL, 8935017488495186458UL, 1175494350822287507UL),
        ulongArrayOf(5332261958806667264UL, 6339826553258882310UL, 2531571471368099271UL, 1UL),
        ulongArrayOf(7205759403792793600UL, 8058033311460168257UL, 6868970639971441100UL, 12UL),
        ulongArrayOf(7493989779944505344UL, 6793356819763476113UL, 4126102141730980352UL, 127UL),
        ulongArrayOf(1152921504606846976UL, 3369963939651330482UL, 4367533269890700295UL, 1274UL),
        ulongArrayOf(2305843009213693952UL, 6029523285948977397UL, 6781844551487899721UL, 12744UL),
        ulongArrayOf(4611686018427387904UL, 4955000638361119124UL, 3254841256895566560UL, 127447UL),
        ulongArrayOf(0UL, 3433146199337312205UL, 4878296458391338181UL, 1274473UL),
        ulongArrayOf(0UL, 6661345882808794626UL, 2666104399639502773UL, 12744735UL),
        ulongArrayOf(0UL, 2049854570104515604UL, 8214299922685476121UL, 127447352UL),
        ulongArrayOf(0UL, 2051801627335604424UL, 8356022932016554748UL, 1274473528UL),
        ulongArrayOf(0UL, 2071272199646492624UL, 549880988472565210UL, 12744735289UL),
        ulongArrayOf(0UL, 2265977922755374624UL, 5498809884725652102UL, 127447352890UL),
        ulongArrayOf(0UL, 4213035153844194624UL, 8871238662982641982UL, 1274473528905UL),
        ulongArrayOf(0UL, 5236863391022843008UL, 5702038298133437552UL, 12744735289059UL),
        ulongArrayOf(0UL, 6251773725954551040UL, 1680150760205720677UL, 127447352890596UL),
        ulongArrayOf(0UL, 7177505038416855552UL, 7578135565202430968UL, 1274473528905961UL),
        ulongArrayOf(0UL, 7211446126185124864UL, 1994379357186103223UL, 12744735289059618UL),
        ulongArrayOf(0UL, 7550857003867817984UL, 1497049498151480621UL, 127447352890596182UL),
        ulongArrayOf(0UL, 1721593743839973376UL, 5747122944660030410UL, 1274473528905961821UL),
        ulongArrayOf(0UL, 7992565401544957952UL, 2130997225471649253UL, 3521363252204842408UL, 1UL),
        ulongArrayOf(0UL, 6138677720611373056UL, 2863228181006940922UL, 7543516411484096658UL, 13UL),
        ulongArrayOf(0UL, 6046544984985075712UL, 962165699505081802UL, 1648187820002760119UL, 138UL),
        ulongArrayOf(0UL, 5125217628722102272UL, 398284958196042218UL, 7258506163172825383UL, 1381UL),
        ulongArrayOf(0UL, 5135316102947143680UL, 3982849581960422185UL, 8021457373744823174UL, 13817UL),
        ulongArrayOf(0UL, 5236300845197557760UL, 2935007672185118623UL, 6427597442610025280UL, 138178UL),
        ulongArrayOf(0UL, 6246148267701698560UL, 1679960611286858811UL, 8935742204971597955UL, 1381786UL),
        ulongArrayOf(0UL, 7121250455888330752UL, 7576234076013812308UL, 6347073718022997279UL, 13817869UL),
        ulongArrayOf(0UL, 6648900300899876864UL, 1975364465299916623UL, 8130504959101317950UL, 138178696UL),
        ulongArrayOf(0UL, 1925398751015337984UL, 1306900579289614621UL, 7518073296174973038UL, 1381786968UL),
        ulongArrayOf(0UL, 807243436443828224UL, 3845633756041370404UL, 1393756666911523917UL, 13817869688UL),
        ulongArrayOf(0UL, 8072434364438282240UL, 1562849412994600808UL, 4714194632260463366UL, 138178696881UL),
        ulongArrayOf(0UL, 6937367349544615936UL, 6405122093091232280UL, 1025086138330754621UL, 1381786968815UL),
        ulongArrayOf(0UL, 4810069237462728704UL, 8710988709783667959UL, 1027489346452770408UL, 13817869688151UL),
        ulongArrayOf(0UL, 1983832190353408000UL, 4099538766143697323UL, 1051521427672928281UL, 138178696881511UL),
        ulongArrayOf(0UL, 1391577829824528384UL, 4101899514017870000UL, 1291842239874507006UL, 1381786968815111UL),
        ulongArrayOf(0UL, 4692406261390508032UL, 4125506992759596769UL, 3695050361890294256UL, 13817869688151111UL),
        ulongArrayOf(0UL, 807202429631201280UL, 4361581780176864463UL, 57015471483839332UL, 138178696881511114UL),
        ulongArrayOf(0UL, 8072024296312012800UL, 6722329654349541398UL, 570154714838393324UL, 1381786968815111140UL),
        ulongArrayOf(
            0UL,
            6933266668281921536UL,
            2659692285511983332UL,
            5701547148383933247UL,
            4594497651296335592UL,
            1UL
        ),
        ulongArrayOf(
            0UL,
            4769062424835784704UL,
            8150178781410281711UL,
            1675239262710677624UL,
            9051488365544252694UL,
            14UL
        ),
        ulongArrayOf(
            0UL,
            1573764064083968000UL,
            7714811519264610651UL,
            7529020590252000440UL,
            7504535323749544669UL,
            149UL
        ),
        ulongArrayOf(
            0UL,
            6514268603984904192UL,
            3361138897807900047UL,
            1503229607681797944UL,
            1258376942657240234UL,
            1498UL
        ),
        ulongArrayOf(
            0UL,
            579081781865611264UL,
            5941272867514673053UL,
            5808924039963203635UL,
            3360397389717626533UL,
            14981UL
        ),
        ulongArrayOf(
            0UL,
            5790817818656112640UL,
            4072496454018075682UL,
            2749008178503381508UL,
            5933857786611937912UL,
            149813UL
        )
    )
}