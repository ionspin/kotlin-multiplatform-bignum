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

package com.ionspin.kotlin.bignum.integer.base32

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigInteger32ArithmeticInterface
import com.ionspin.kotlin.bignum.integer.Quadruple
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.toDigit
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */
@ExperimentalUnsignedTypes
internal object BigInteger32Arithmetic : BigInteger32ArithmeticInterface {
    override val _emitIntArray: IntArray = intArrayOf()
    val baseMask = 0xFFFFFFFFUL
    val baseMaskInt = 0xFFFFFFFFU
    val overflowMask = 0x100000000U
    val lowerMask = 0xFFFFUL
    val base: UInt = 0xFFFFFFFFU
    override val basePowerOfTwo = 32
    val wordSizeInBits = 32

    override val ZERO = UIntArray(0)
    override val ONE = UIntArray(1) { 1U }
    override val TWO = UIntArray(1) { 2U }
    override val TEN = UIntArray(1) { 10U }

    const val karatsubaThreshold = 60 // TODO Improve thresholds
    const val toomCookThreshold = 15_000 // TODO Use Toom-Cook3

    /**
     * Hackers delight 5-11
     */
    override fun numberOfLeadingZerosInAWord(value: UInt): Int {
        var x = value
        var y: UInt
        var n = basePowerOfTwo

        y = x shr 16
        if (y != 0U) {
            n = n - 16
            x = y
        }
        y = x shr 8
        if (y != 0U) {
            n = n - 8
            x = y
        }
        y = x shr 4
        if (y != 0U) {
            n = n - 4
            x = y
        }
        y = x shr 2
        if (y != 0U) {
            n = n - 2
            x = y
        }
        y = x shr 1
        if (y != 0U) {
            return n - 2
        }

        return n - x.toInt()
    }

    fun numberOfTrailingZerosInAWord(value: UInt): Int {
        var x = value
        var y: UInt
        var n = 32

        y = (x shl 16) and baseMaskInt
        if (y != 0U) {
            n -= 16
            x = y
        }
        y = (x shl 8) and baseMaskInt
        if (y != 0U) {
            n = n - 8
            x = y
        }
        y = (x shl 4) and baseMaskInt
        if (y != 0U) {
            n = n - 4
            x = y
        }
        y = (x shl 2) and baseMaskInt
        if (y != 0U) {
            n = n - 2
            x = y
        }
        y = (x shl 1) and baseMaskInt
        if (y != 0U) {
            return n - 2
        }
        return n - x.toInt()
    }

    override fun bitLength(value: UIntArray): Int {
        if (value.isEmpty()) {
            return 0
        }
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size - 1) * basePowerOfTwo
    }

    fun bitLength(value: UInt): Int {
        return basePowerOfTwo - numberOfLeadingZerosInAWord(
            value
        )
    }

    fun trailingZeroBits(value: UInt): Int {
        return numberOfTrailingZerosInAWord(value)
    }

    override fun trailingZeroBits(value: UIntArray): Int {
        if (value.contentEquals(ZERO)) { return 0 }
        val zeroWordsCount = value.takeWhile { it == 0U }.count()
        if (zeroWordsCount == value.size) {
            return 0
        }
        return trailingZeroBits(value[zeroWordsCount]) + (zeroWordsCount * 63)
    }

    fun removeLeadingZeros(bigInteger: UIntArray): UIntArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0U } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            return ZERO
        }
        return bigInteger.copyOfRange(0, firstEmpty)
    }

    fun countLeadingZeroWords(bigInteger: UIntArray): Int {
        // Presume there are no leading zeros
        var lastNonEmptyIndex = bigInteger.size - 1
        // Check if it's an empty array
        if (lastNonEmptyIndex <= 0) {
            return 0
        }
        // Get the last element (Word order is high endian so leading zeros are only on highest indexes
        var element = bigInteger[lastNonEmptyIndex]
        while (element == 0U && lastNonEmptyIndex > 0) {
            lastNonEmptyIndex -= 1
            element = bigInteger[lastNonEmptyIndex]
        }
        if (bigInteger[lastNonEmptyIndex] == 0U) {
            lastNonEmptyIndex -= 1
        }
        return bigInteger.size - lastNonEmptyIndex - 1
    }

    override fun shiftLeft(operand: UIntArray, places: Int): UIntArray {
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val originalSize = operand.size
        val leadingZeros =
            numberOfLeadingZerosInAWord(operand[operand.size - 1])
        val shiftWords = places / basePowerOfTwo
        val shiftBits = places % basePowerOfTwo
        val wordsNeeded = if (shiftBits > leadingZeros) {
            shiftWords + 1
        } else {
            shiftWords
        }
        if (shiftBits == 0) {
            return UIntArray(operand.size + wordsNeeded) {
                when (it) {
                    in 0 until shiftWords -> 0U
                    else -> operand[it - shiftWords]
                }
            }
        }
        return UIntArray(operand.size + wordsNeeded) {
            when (it) {
                in 0 until shiftWords -> 0U
                shiftWords -> {
                    (operand[it - shiftWords] shl shiftBits)
                }
                in (shiftWords + 1) until (originalSize + shiftWords) -> {
                    (operand[it - shiftWords] shl shiftBits) or (operand[it - shiftWords - 1] shr (basePowerOfTwo - shiftBits))
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

    override fun shiftRight(operand: UIntArray, places: Int): UIntArray {
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val shiftBits = (places % basePowerOfTwo)
        val wordsToDiscard = places / basePowerOfTwo
        if (wordsToDiscard >= operand.size) {
            return ZERO
        }

        if (shiftBits == 0) {
            return operand.copyOfRange(wordsToDiscard, operand.size)
        }

        if (operand.size > 1 && operand.size - wordsToDiscard == 1) {
            return uintArrayOf(operand[operand.size - 1] shr shiftBits)
        }

        val result = UIntArray(operand.size - wordsToDiscard) {
            when (it) {
                in 0 until (operand.size - 1 - wordsToDiscard) -> {
                    (operand[it + wordsToDiscard] shr shiftBits) or (operand[it + wordsToDiscard + 1] shl (basePowerOfTwo - shiftBits))
                }
                operand.size - 1 - wordsToDiscard -> {
                    (operand[it + wordsToDiscard] shr (shiftBits))
                }
                else -> {
                    throw RuntimeException("Invalid case $it")
                }
            }
        }
        return removeLeadingZeros(result)
    }

    fun normalize(dividend: UIntArray, divisor: UIntArray): Triple<UIntArray, UIntArray, Int> {
        val divisorSize = divisor.size
        val normalizationShift =
            numberOfLeadingZerosInAWord(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)
    }

    fun normalize(operand: UIntArray): Pair<UIntArray, Int> {
        val normalizationShift =
            numberOfLeadingZerosInAWord(operand[operand.size - 1])
        return Pair(operand.shl(normalizationShift), normalizationShift)
    }

    fun denormalize(
        remainderNormalized: UIntArray,
        normalizationShift: Int
    ): UIntArray {
        val remainder = remainderNormalized shr normalizationShift
        return remainder
    }

    // ---------------- Primitive operations -----------------------//

    override fun compare(first: UIntArray, second: UIntArray): Int {
        val firstStart = first.size - countLeadingZeroWords(first)
        val secondStart = second.size - countLeadingZeroWords(second)
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

    override fun add(first: UIntArray, second: UIntArray): UIntArray {
        if (first.size == 1 && first[0] == 0U) return second
        if (second.size == 1 && second[0] == 0U) return first

        val (maxLength, minLength, largerData, smallerData) = if (first.size > second.size) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }

        val result = UIntArray(maxLength + 1) { 0u }
        var i = 0
        var sum: ULong = 0u
        while (i < minLength) {
            sum = sum + largerData[i] + smallerData[i]
            result[i] = (sum and baseMask).toUInt()
            sum = sum shr basePowerOfTwo
            i++
        }

        while (true) {
            if (sum == 0UL) {
                while (i < maxLength) {
                    result[i] = largerData[i]
                    i++
                }
                return if (result[result.size - 1] == 0U) {
                    result.copyOfRange(0, result.size - 1)
                } else {
                    result
                }
            }
            if (i == maxLength) {
                result[maxLength] = sum.toUInt()
                return result
            }

            sum = sum + largerData[i]
            result[i] = (sum and baseMask).toUInt()
            sum = sum shr basePowerOfTwo
            i++
        }
    }

    override fun subtract(first: UIntArray, second: UIntArray): UIntArray {
        val firstWithoutLeadingZeroes = removeLeadingZeros(first)
        val secondWithoutLeadingZeroes = removeLeadingZeros(second)
        val firstIsLarger = compare(firstWithoutLeadingZeroes, secondWithoutLeadingZeroes) == 1
        val (largerLength, smallerLength, largerData, smallerData) = if (firstIsLarger) {
            Quadruple(firstWithoutLeadingZeroes.size, secondWithoutLeadingZeroes.size, firstWithoutLeadingZeroes, secondWithoutLeadingZeroes)
        } else {
            Quadruple(secondWithoutLeadingZeroes.size, firstWithoutLeadingZeroes.size, secondWithoutLeadingZeroes, firstWithoutLeadingZeroes)
        }
        val result = UIntArray(largerLength + 1) { 0u }
        var i = 0
        var diff: ULong = 0u
        while (i < smallerLength) {
            if (i >= largerData.size) {
                println("Breakpoint")
            }
            if (i >= smallerData.size) {
                println("Breakpoint")
            }
            diff = largerData[i].toULong() - smallerData[i] - diff
            result[i] = diff.toUInt()
            diff = (diff and overflowMask) shr wordSizeInBits
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i] - diff
            result[i] = (diff.toUInt() and baseMaskInt)
            diff = (diff and overflowMask) shr wordSizeInBits
            i++
        }

        while (i < largerLength) {
            result[i] = largerData[i]
            i++
        }

        if (result.filter { it == 0U }.isEmpty()) {
            return ZERO
        }
        // Remove zero words
        val firstEmpty = result.indexOfLast { it != 0U } + 1

        return result.copyOfRange(0, firstEmpty)
    }

    fun multiply(first: UInt, second: UInt): UIntArray {
        val result = first * second
        val high = (result shr basePowerOfTwo).toUInt()
        val low = result.toUInt()

        return removeLeadingZeros(uintArrayOf(low, high))
    }

    fun multiply(first: UIntArray, second: UInt): UIntArray {

        val result = UIntArray(first.size + 1)

        var product: ULong
        var sum: ULong
        for (i in 0 until first.size) {
            product = first[i].toULong() * second
            sum = result[i].toULong() + (product and baseMask).toUInt()
            result[i] = (sum and baseMask).toUInt()
            sum = sum shr basePowerOfTwo
            result[i + 1] = (product shr basePowerOfTwo).toUInt() + sum.toUInt()
        }

        return removeLeadingZeros(result)
    }

    fun karatsubaMultiply(firstUnsigned: UIntArray, secondUnsigned: UIntArray): UIntArray {
        val first = SignedUIntArray(firstUnsigned, true)
        val second = SignedUIntArray(secondUnsigned, true)
        val halfLength = (kotlin.math.max(first.unsignedValue.size, second.unsignedValue.size) + 1) / 2

        val mask = subtract(ONE shl (halfLength * wordSizeInBits), ONE)
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

    fun prependULongArray(original: UIntArray, numberOfWords: Int, value: UInt): UIntArray {

        return UIntArray(original.size + numberOfWords) {
            when {
                it < numberOfWords -> value
                else -> original[it - numberOfWords]
            }
        }
    }

    fun extendUIntArray(original: UIntArray, numberOfWords: Int, value: UInt): UIntArray {

        return UIntArray(original.size + numberOfWords) {
            when {
                it < original.size -> original[it]
                else -> value
            }
        }
    }

    @Suppress("DuplicatedCode")
    fun toomCook3Multiply(firstUnchecked: UIntArray, secondUnchecked: UIntArray): UIntArray {
        val first = if (firstUnchecked.size % 3 != 0) {
            firstUnchecked.plus(UIntArray((((firstUnchecked.size + 2) / 3) * 3) - firstUnchecked.size) { 0U }.asIterable())
        } else {
            firstUnchecked
        }.toUIntArray()

        val second = if (secondUnchecked.size % 3 != 0) {
            secondUnchecked.plus(UIntArray((((secondUnchecked.size + 2) / 3) * 3) - secondUnchecked.size) { 0U }.asIterable())
        } else {
            secondUnchecked
        }.toUIntArray()
        val firstLength = first.size
        val secondLength = second.size

        val (firstPrepared, secondPrepared) = when {
            firstLength > secondLength -> {
                val prepared =
                    extendUIntArray(
                        second,
                        firstLength - secondLength,
                        0U
                    )
                Pair(first, prepared)
            }
            firstLength < secondLength -> {
                val prepared =
                    extendUIntArray(
                        first,
                        secondLength - firstLength,
                        0U
                    )
                Pair(prepared, second)
            }
            else -> Pair(first, second)
        }

        val longestLength = kotlin.math.max(first.size, second.size)

        val extendedDigit = (longestLength + 2) / 3

        val m0 = SignedUIntArray(
            firstPrepared.slice(0 until extendedDigit).toUIntArray(),
            true
        )
        val m1 = SignedUIntArray(
            firstPrepared.slice(extendedDigit until extendedDigit * 2).toUIntArray(), true
        )
        val m2 = SignedUIntArray(
            firstPrepared.slice(extendedDigit * 2 until extendedDigit * 3).toUIntArray(), true
        )

        val n0 = SignedUIntArray(
            secondPrepared.slice(0 until extendedDigit).toUIntArray(),
            true
        )
        val n1 = SignedUIntArray(
            secondPrepared.slice(extendedDigit until extendedDigit * 2).toUIntArray(), true
        )
        val n2 = SignedUIntArray(
            secondPrepared.slice(extendedDigit * 2 until extendedDigit * 3).toUIntArray(), true
        )

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
        // var r3 = SignedUIntArray(exactDivideBy3(rem2re1diff.unsignedValue), rem2re1diff.sign)
        var r3 = rem2re1diff / SignedUIntArray(
            uintArrayOf(
                3U
            ), true
        )
        // println("R3 ${r3.sign} ${r3.unsignedValue}")
        var r1 = (re1 - rem1) shr 1
        var r2 = rem1 - r0
        r3 = ((r2 - r3) shr 1) + SIGNED_POSITIVE_TWO * rinf
        r2 = r2 + r1 - r4
        r1 = r1 - r3

        val bShiftAmount = extendedDigit * wordSizeInBits
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

    data class SignedUIntArray(val unsignedValue: UIntArray, val sign: Boolean)

    private fun signedAdd(first: SignedUIntArray, second: SignedUIntArray) = if (first.sign xor second.sign) {
        if (first.unsignedValue > second.unsignedValue) {
            SignedUIntArray(first.unsignedValue - second.unsignedValue, first.sign)
        } else {
            SignedUIntArray(second.unsignedValue - first.unsignedValue, second.sign)
        }
    } else {
        // Same sign
        SignedUIntArray(first.unsignedValue + second.unsignedValue, first.sign)
    }

    val SIGNED_POSITIVE_TWO = SignedUIntArray(TWO, true)

    private fun signedSubtract(first: SignedUIntArray, second: SignedUIntArray) = signedAdd(first, second.copy(sign = !second.sign))

    private fun signedMultiply(first: SignedUIntArray, second: SignedUIntArray) = SignedUIntArray(first.unsignedValue * second.unsignedValue, !(first.sign xor second.sign))

    private fun signedDivide(first: SignedUIntArray, second: SignedUIntArray) = SignedUIntArray(first.unsignedValue / second.unsignedValue, !(first.sign xor second.sign))

    private fun signedRemainder(first: SignedUIntArray, second: SignedUIntArray) = SignedUIntArray(first.unsignedValue % second.unsignedValue, !(first.sign xor second.sign))

    internal operator fun SignedUIntArray.plus(other: SignedUIntArray): SignedUIntArray {
        return signedAdd(this, other)
    }

    internal operator fun SignedUIntArray.minus(other: SignedUIntArray): SignedUIntArray {
        return signedSubtract(this, other)
    }

    internal operator fun SignedUIntArray.times(other: SignedUIntArray): SignedUIntArray {
        return signedMultiply(this, other)
    }

    internal operator fun SignedUIntArray.div(other: SignedUIntArray): SignedUIntArray {
        return signedDivide(this, other)
    }

    internal operator fun SignedUIntArray.rem(other: SignedUIntArray): SignedUIntArray {
        return signedRemainder(this, other)
    }

    internal infix fun SignedUIntArray.shr(places: Int) = SignedUIntArray(unsignedValue shr places, sign)

    internal infix fun SignedUIntArray.shl(places: Int) = SignedUIntArray(unsignedValue shl places, sign)

    internal infix fun SignedUIntArray.and(operand: UIntArray) = SignedUIntArray(and(unsignedValue, operand), sign)

    // End of signed operations

    override fun multiply(first: UIntArray, second: UIntArray): UIntArray {
        if (first == ZERO || second == ZERO) {
            return ZERO
        }
        if (first.size >= karatsubaThreshold || second.size == karatsubaThreshold) {
            return karatsubaMultiply(first, second)
        }

        return removeLeadingZeros(
            second.foldIndexed(ZERO) { index, acc, element ->
                acc + (multiply(
                    first,
                    element
                ) shl (index * basePowerOfTwo))
            }
        )
    }

    /**
     * Just for testing against basecase multiplication. TODO add basecase multiply
     */
    internal fun multiplyNoKaratsuba(first: UIntArray, second: UIntArray): UIntArray {
        if (first == ZERO || second == ZERO) {
            return ZERO
        }
        if (first.size >= karatsubaThreshold || second.size == karatsubaThreshold) {
            return karatsubaMultiply(first, second)
        }

        return removeLeadingZeros(
            second.foldIndexed(ZERO) { index, acc, element ->
                acc + (multiply(
                    first,
                    element
                ) shl (index * basePowerOfTwo))
            }
        )
    }

    override fun pow(base: UIntArray, exponent: Long): UIntArray {
        if (exponent == 0L) {
            return ONE
        }
        if (exponent == 1L) {
            return base
        }
        var helperVar = ONE
        var exponentVar = exponent
        var baseVar = base
        while (exponentVar > 1) {
            if (exponentVar % 2 == 0L) {
                baseVar = baseVar * baseVar
                exponentVar /= 2
            } else {
                helperVar = baseVar * helperVar
                baseVar = baseVar * baseVar
                exponentVar = (exponentVar - 1) / 2
            }
        }
        return helperVar * baseVar
    }

    override fun divide(first: UIntArray, second: UIntArray): Pair<UIntArray, UIntArray> {
        return basicDivide(first, second)
    }

    /*
     * Based on Basecase DivRem algorithm from
     * Modern Computer Arithmetic, Richard Brent and Paul Zimmermann, Cambridge University Press, 2010.
     * Version 0.5.9
     * https://members.loria.fr/PZimmermann/mca/pub226.html
     */
    fun basicDivide(
        unnormalizedDividend: UIntArray,
        unnormalizedDivisor: UIntArray
    ): Pair<UIntArray, UIntArray> {
        if (unnormalizedDivisor > unnormalizedDividend) {
            return Pair(ZERO, unnormalizedDividend)
        }
        if (unnormalizedDivisor.size == 1 && unnormalizedDividend.size == 1) {
            return Pair(
                removeLeadingZeros(
                    uintArrayOf(
                        unnormalizedDividend[0] / unnormalizedDivisor[0]
                    )
                ),
                removeLeadingZeros(
                    uintArrayOf(
                        unnormalizedDividend[0] % unnormalizedDivisor[0]
                    )
                )
            )
        }
        val bitPrecision = bitLength(unnormalizedDividend) - bitLength(
            unnormalizedDivisor
        )
        if (bitPrecision == 0) {
            return Pair(uintArrayOf(1U), unnormalizedDividend - unnormalizedDivisor)
        }

        var (dividend, divisor, normalizationShift) = normalize(
            unnormalizedDividend,
            unnormalizedDivisor
        )
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val wordPrecision = dividendSize - divisorSize

        var qjhat: ULong
        var reconstructedQuotient: UIntArray
        var quotient = UIntArray(wordPrecision)

        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision * basePowerOfTwo))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient = UIntArray(wordPrecision + 1)
            quotient[wordPrecision] = 1U
            dividend = dividend - divisorTimesBaseToPowerOfM
        }

        for (j in (wordPrecision - 1) downTo 0) {
            qjhat = if (divisorSize + j < dividend.size) {
                ((dividend[divisorSize + j].toULong() shl basePowerOfTwo) +
                    dividend[divisorSize + j - 1]) /
                    divisor[divisorSize - 1]
            } else {
                if (divisorSize + j == dividend.size) {
                    ((dividend[divisorSize + j - 1]) / divisor[divisorSize - 1]).toULong()
                } else {
                    0UL
                }
            }

            quotient[j] = if (qjhat < (base - 1UL)) {
                qjhat.toUInt()
            } else {
                base - 1U
            }

            // We don't have signed integers here so we need to check if reconstructed quotient is larger than the dividend
            // instead of just doing  (dividend = dividend − qj * β^j * divisor) and then looping while dividend is less than 0.
            // Final effect is the same.
            reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            while (reconstructedQuotient > dividend) {
                quotient[j] = quotient[j] - 1U
                reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            }
            dividend = dividend - reconstructedQuotient
        }

        while (dividend >= divisor) {
            quotient += 1U
            dividend -= divisor
        }

        val denormRemainder =
            denormalize(dividend, normalizationShift)
        return Pair(removeLeadingZeros(quotient), denormRemainder)
    }

    fun basicDivide2(
        unnormalizedDividend: UIntArray,
        unnormalizedDivisor: UIntArray
    ): Pair<UIntArray, UIntArray> {
        var (a, b, shift) = normalize(unnormalizedDividend, unnormalizedDivisor)
        val m = a.size - b.size
        val bmb = b shl (m * wordSizeInBits)
        var q = UIntArray(m + 1) { 0U }
        if (a > bmb) {
            q[m] = 1U
            a = a - bmb
        }
        var qjhat = ZERO
        var qjhatULong = 0UL
        var bjb = ZERO
        var delta = ZERO
        for (j in m - 1 downTo 0) {
            qjhatULong = toULongExact(a.copyOfRange(b.size - 1, b.size + 1)) / b[b.size - 1]
            q[j] = min(qjhatULong, baseMask).toUInt()
            bjb = b shl (j * wordSizeInBits)
            val qjBjb = (b * q[j]) shl (j * wordSizeInBits)
            if (qjBjb > a) {
                delta = qjBjb - a
                while (delta > qjBjb) {
                    q[j] = q[j] - 1U
                    delta = delta - bjb
                }
                // quotient is now such that q[j] * b*B^j won't be larger than divisor
                a = a - (b * q[j]) shl (j * wordSizeInBits)
            } else {
                a = a - qjBjb
            }
        }
        val denormRemainder =
            denormalize(a, shift)
        return Pair(removeLeadingZeros(q), denormRemainder)
    }

    fun d1ReciprocalRecursiveWordVersion(a: UIntArray): Pair<UIntArray, UIntArray> {
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

    fun reciprocalSingleWord(operand: UInt): Pair<UIntArray, Int> {
        val bitLength = bitLength(operand)
        val requiredPrecision = bitLength * 4
        if (bitLength * 2 <= 63) {
            val base =
                1UL shl (requiredPrecision) // We are sure that precision is less or equal to 63, so inside int range
            var result = base / operand

            return checkReciprocal(uintArrayOf(operand), Pair(uintArrayOf(result.toUInt()), requiredPrecision))
        } else {
            val base = ONE.shl(requiredPrecision)
            val result = base / operand
            return checkReciprocal(uintArrayOf(operand), Pair(result, requiredPrecision))
        }
    }

    private fun checkReciprocal(
        operand: UIntArray,
        reciprocal: Pair<UIntArray, Int>
    ): Pair<UIntArray, Int> {
        val product = (operand * reciprocal.first)
        val check = product shr reciprocal.second
        return if (check != ONE) {
            Pair(reciprocal.first, reciprocal.second - 1)
        } else {
            Pair(reciprocal.first, reciprocal.second)
        }
    }

    override fun reciprocal(operand: UIntArray): Pair<UIntArray, UIntArray> {
        return d1ReciprocalRecursiveWordVersion(operand)
    }

    internal fun reciprocalDivision(first: UIntArray, second: UIntArray): Pair<UIntArray, UIntArray> {
        val reciprocalExtension = first.size - second.size
        val precisionShift = (reciprocalExtension * 2 * wordSizeInBits)
        val secondHighPrecision = second shl precisionShift

        val secondReciprocalWithRemainder = d1ReciprocalRecursiveWordVersion(secondHighPrecision)

        val secondReciprocal = secondReciprocalWithRemainder.first
        var product = first * secondReciprocal
        // TODO Proper rounding
        if (product.compareTo(0U) == 0) {
            return Pair(ZERO, first)
        }
        if (product.size == 1) {
            if (product >= baseMaskInt - 1U) {
                product = product + ONE
            }
        } else {
            val importantWord = product[product.size - second.size]
            if (importantWord >= baseMask) {
                product = UIntArray(product.size) {
                    when (it) {
                        product.size - 1 -> product[product.size - 1] + 1U
                        else -> 0U
                    }
                }
            }
        }

        var numberOfWords = product.size - (secondReciprocal.size * 2) + reciprocalExtension * 2
        if (numberOfWords == 0) {
            numberOfWords = 1
        }
        val result = product.copyOfRange(product.size - numberOfWords, product.size)
        val remainder = first - (result * second)
        return Pair(result, remainder)
    }

    override fun sqrt(operand: UIntArray): Pair<UIntArray, UIntArray> {
        return reqursiveSqrt(operand)
    }

    private fun reqursiveSqrt(operand: UIntArray): Pair<UIntArray, UIntArray> {
        val n = operand.size
        val l = floor((n - 1).toDouble() / 4).toInt()
        if (l == 0) {
            return basecaseSqrt(operand)
        }
        val step = n / 4
        val stepRemainder = n % 4
        val baseLPowerShift = 32 * l
        val a1 = operand.copyOfRange(n - ((3 * step) + stepRemainder), n - ((2 * step) + stepRemainder))
        val a0 = operand.copyOfRange(0, n - ((3 * step) + stepRemainder))
        val a3a2 = operand.copyOfRange(n - ((2 * step) + stepRemainder), n)

        val (sPrim, rPrim) = reqursiveSqrt(a3a2)
        val (q, u) = basicDivide2(((rPrim shl baseLPowerShift) + a1), (sPrim shl 1))
        var s = (sPrim shl baseLPowerShift) + q
        var r = (u shl baseLPowerShift) + a0 - (q * q)
        return Pair(s, r)
    }

    internal fun basecaseSqrt(operand: UIntArray): Pair<UIntArray, UIntArray> {
        val sqrt = sqrtInt(operand)
        val remainder = operand - (sqrt * sqrt)
        return Pair(sqrt, remainder)
    }

    internal fun sqrtInt(operand: UIntArray): UIntArray {
        var u = operand
        var s: UIntArray
        var tmp: UIntArray
        do {
            s = u
            tmp = s + (basicDivide2(operand, s).first)
            u = tmp shr 1
        } while (u < s)
        return s
    }

    override fun gcd(first: UIntArray, second: UIntArray): UIntArray {
        return if (first.size > 150 || second.size > 150) {
            euclideanGcd(first, second)
        } else {
            binaryGcd(first, second)
        }
    }

    private fun euclideanGcd(first: UIntArray, second: UIntArray): UIntArray {
        var u = first
        var v = second
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
    }

    private tailrec fun binaryGcd(first: UIntArray, second: UIntArray): UIntArray {
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

    override fun parseForBase(number: String, base: Int): UIntArray {
        var parsed = ZERO
        number.forEach { char ->
            parsed = (parsed * base.toUInt()) + char.toDigit().toUInt()
        }
        return parsed
    }

    override fun toString(operand: UIntArray, base: Int): String {
        var copy = operand.copyOf()
        val baseArray = uintArrayOf(base.toUInt())
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

    override fun numberOfDecimalDigits(operand: UIntArray): Long {
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

    override fun and(operand: UIntArray, mask: UIntArray): UIntArray {
        return removeLeadingZeros(
            UIntArray(operand.size) {
                if (it < mask.size) {
                    operand[it] and mask[it]
                } else {
                    0U
                }
            }
        )
    }

    override fun or(operand: UIntArray, mask: UIntArray): UIntArray {
        return removeLeadingZeros(
            UIntArray(operand.size) {
                if (it < mask.size) {
                    operand[it] or mask[it]
                } else {
                    operand[it]
                }
            }
        )
    }

    override fun xor(operand: UIntArray, mask: UIntArray): UIntArray {
        return removeLeadingZeros(
            UIntArray(operand.size) {
                if (it < mask.size) {
                    operand[it] xor mask[it]
                } else {
                    operand[it] xor 0U
                }
            }
        )
    }

    override fun not(operand: UIntArray): UIntArray {
        return removeLeadingZeros(
            UIntArray(operand.size) {
                operand[it].inv()
            }
        )
    }

    internal infix fun UIntArray.shl(places: Int): UIntArray {
        return shiftLeft(this, places)
    }

    internal infix fun UIntArray.shr(places: Int): UIntArray {
        return shiftRight(this, places)
    }

    override fun bitAt(operand: UIntArray, position: Long): Boolean {
        if (position / 63 > Int.MAX_VALUE) {
            throw RuntimeException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= operand.size) {
            return false
        }
        val bitPosition = position % 63
        val word = operand[wordPosition.toInt()]
        return (word and (1U shl bitPosition.toInt()) == 1U)
    }

    override fun setBitAt(operand: UIntArray, position: Long, bit: Boolean): UIntArray {
        if (position / 63 > Int.MAX_VALUE) {
            throw RuntimeException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= operand.size) {
            throw IndexOutOfBoundsException("Invalid position, addressed word $wordPosition larger than number of words ${operand.size}")
        }
        val bitPosition = position % 63
        val setMask = 1U shl bitPosition.toInt()
        return UIntArray(operand.size) {
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

    internal operator fun UIntArray.plus(other: UIntArray): UIntArray {
        return add(this, other)
    }

    internal operator fun UIntArray.minus(other: UIntArray): UIntArray {
        return subtract(this, other)
    }

    internal operator fun UIntArray.times(other: UIntArray): UIntArray {
        return multiply(this, other)
    }

    internal operator fun UIntArray.plus(other: UInt): UIntArray {
        return add(this, uintArrayOf(other))
    }

    internal operator fun UIntArray.minus(other: UInt): UIntArray {
        return subtract(this, uintArrayOf(other))
    }

    internal operator fun UIntArray.times(other: UInt): UIntArray {
        return multiply(this, other)
    }

    internal operator fun UIntArray.div(other: UInt): UIntArray {
        return divide(this, uintArrayOf(other)).first
    }

    internal operator fun UIntArray.rem(other: UInt): UIntArray {
        return divide(this, uintArrayOf(other)).second
    }

    internal operator fun UIntArray.div(other: UIntArray): UIntArray {
        return divide(this, other).first
    }

    internal operator fun UIntArray.rem(other: UIntArray): UIntArray {
        return divide(this, other).second
    }

    internal infix fun UIntArray.divrem(other: UIntArray): Pair<UIntArray, UIntArray> {
        return divide(this, other)
    }

    internal operator fun UIntArray.compareTo(other: UIntArray): Int {
        return compare(this, other)
    }

    internal operator fun UIntArray.compareTo(other: UInt): Int {
        return compare(this, uintArrayOf(other))
    }

    fun toUnsignedIntArrayCodeFormat(array: UIntArray): String {
        return array.joinToString(prefix = "uintArrayOf(", separator = ", ", postfix = ")") {
            it.toString() + "U"
        }
    }

    override fun fromULong(uLong: ULong): UIntArray = uintArrayOf(
        ((uLong and 0xFFFFFFFF00000000U) shr 32).toUInt(),
        uLong.toUInt()
    )

    override fun fromUInt(uInt: UInt): UIntArray = uintArrayOf(uInt)

    override fun fromUShort(uShort: UShort): UIntArray = uintArrayOf(uShort.toUInt())

    override fun fromUByte(uByte: UByte): UIntArray = uintArrayOf(uByte.toUInt())

    override fun fromLong(long: Long): UIntArray = uintArrayOf(
        ((long.toULong() and 0xFFFFFFFF00000000U) shr 32).toUInt(),
        long.absoluteValue.toUInt()
    )

    override fun fromInt(int: Int): UIntArray = uintArrayOf(int.absoluteValue.toUInt())

    override fun fromShort(short: Short): UIntArray = uintArrayOf(short.toInt().absoluteValue.toUInt())

    override fun fromByte(byte: Byte): UIntArray = uintArrayOf(byte.toInt().absoluteValue.toUInt())

    fun toULongExact(operand: UIntArray): ULong {
        if (operand.size > 2) {
            throw ArithmeticException("Exact conversion not possible, operand size ${operand.size}")
        }
        var result: ULong = 0UL
        for (i in operand.size - 1 downTo 0) {
            result += (operand[i].toULong() shl (i * wordSizeInBits))
        }
        return result
    }

    override fun toByteArray(operand: UIntArray, sign: Sign): Array<Byte> {
        if (operand.isEmpty()) {
            return emptyArray()
        }
        val bitLength = bitLength(operand)
        return when (sign) {
            Sign.ZERO -> {
                emptyList()
            }
            Sign.POSITIVE -> {
                val collected = operand.flatMap {
                    listOf(
                        ((it shr 24) and 0xFFU).toByte(),
                        ((it shr 16) and 0xFFU).toByte(),
                        ((it shr 8) and 0xFFU).toByte(),
                        ((it) and 0xFFU).toByte()
                    )
                }.takeLast(operand.size * 4 + 1).chunked(4).reversed().flatten()
                val corrected = if (bitLength % 8 == 0) {
                    listOf(0x00.toByte()) + collected
                } else {
                    collected
                }
                corrected
            }
            Sign.NEGATIVE -> {
                val inverted = operand.map { it.inv() }.toUIntArray()
                val converted = inverted + 1U
                val collected = converted.flatMap {
                    listOf(
                        ((it shr 24) and 0xFFU).toByte(),
                        ((it shr 16) and 0xFFU).toByte(),
                        ((it shr 8) and 0xFFU).toByte(),
                        ((it) and 0xFFU).toByte()
                    )
                }.takeLast(operand.size * 4 + 1).chunked(4).reversed().flatten()
                val corrected = if (bitLength % 8 == 0) {
                    listOf(0xFF.toByte()) + collected
                } else {
                    collected
                }
                val signExtensionCount = corrected.takeWhile { it == 0xFF.toByte() }.size
                val perfected = if (signExtensionCount > 1) {
                    corrected.subList(signExtensionCount - 1, corrected.size)
                } else {
                    corrected
                }
                perfected
            }
        }.toTypedArray()
    }

    override fun fromByteArray(byteArray: Array<Byte>): Pair<UIntArray, Sign> {
        val sign = (byteArray[0].toInt() ushr 7) and 0b00000001
        val chunked = byteArray.toList().reversed().chunked(4)

        val resolvedSign = when (sign) {
            0 -> Sign.POSITIVE
            1 -> Sign.NEGATIVE
            else -> throw RuntimeException("Invalid sign value when converting from byte array")
        }
        return when (resolvedSign) {
            Sign.POSITIVE -> {
                val collected = chunked.flatMap { chunk ->
                    val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                        acc + ((byte.toUInt() and 0xFFU) shl ((chunk.size - 1) * 8 - index * 8))
                    }
                    val discard = 4 - chunk.size
                    val discarded = (result shl (8 * discard)) shr (8 * discard)
                    uintArrayOf(discarded)
                }.toUIntArray()
                if (collected.contentEquals(ZERO)) {
                    return Pair(ZERO, Sign.ZERO)
                }
                val corrected = collected.dropLastWhile { it == 0U }.toUIntArray()
                Pair(removeLeadingZeros(corrected), resolvedSign)
            }
            Sign.NEGATIVE -> {
                val collected = chunked.flatMap { chunk ->
                    val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                        acc + (byte.toUInt() shl ((chunk.size - 1) * 8 - index * 8))
                    }
                    uintArrayOf(result)
                }.toUIntArray()
                val subtracted = collected - 1U
                val inverted = subtracted.map { it.inv() }.toUIntArray()
                if (collected.contentEquals(ZERO)) {
                    return Pair(ZERO, Sign.ZERO)
                }

                Pair(removeLeadingZeros(inverted), resolvedSign)
            }
            Sign.ZERO -> throw RuntimeException("Bug in fromByteArray, sign shouldn't ever be zero at this point.")
        }
    }

    override fun fromByteArray(byteArray: ByteArray): Pair<UIntArray, Sign> {
        val sign = (byteArray[0].toInt() ushr 7) and 0b00000001
        val chunked = byteArray.toList().reversed().chunked(4)

        val resolvedSign = when (sign) {
            0 -> Sign.POSITIVE
            1 -> Sign.NEGATIVE
            else -> throw RuntimeException("Invalid sign value when converting from byte array")
        }
        return when (resolvedSign) {
            Sign.POSITIVE -> {
                val collected = chunked.flatMap { chunk ->
                    val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                        acc + ((byte.toUInt() and 0xFFU) shl ((chunk.size - 1) * 8 - index * 8))
                    }
                    val discard = 4 - chunk.size
                    val discarded = (result shl (8 * discard)) shr (8 * discard)
                    uintArrayOf(discarded)
                }.toUIntArray()
                if (collected.contentEquals(ZERO)) {
                    return Pair(ZERO, Sign.ZERO)
                }
                val corrected = collected.dropLastWhile { it == 0U }.toUIntArray()
                Pair(removeLeadingZeros(corrected), resolvedSign)
            }
            Sign.NEGATIVE -> {
                val collected = chunked.flatMap { chunk ->
                    val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                        acc + (byte.toUInt() shl ((chunk.size - 1) * 8 - index * 8))
                    }
                    uintArrayOf(result)
                }.toUIntArray()
                val subtracted = collected - 1U
                val inverted = subtracted.map { it.inv() }.toUIntArray()
                if (collected.contentEquals(ZERO)) {
                    return Pair(ZERO, Sign.ZERO)
                }

                Pair(removeLeadingZeros(inverted), resolvedSign)
            }
            Sign.ZERO -> throw RuntimeException("Bug in fromByteArray, sign shouldn't ever be zero at this point.")
        }
    }

    override fun fromUByteArray(uByteArray: Array<UByte>, endianness: Endianness): Pair<UIntArray, Sign> {
        val chunked = when (endianness) {
            Endianness.BIG -> {
                uByteArray.toList().reversed().chunked(4)
            }
            Endianness.LITTLE -> {
                uByteArray.toList().chunked(4)
            }
        }

        val resolvedSign = Sign.POSITIVE

        val collected = chunked.flatMap { chunk ->
            val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                acc + ((byte.toUInt() and 0xFFU) shl ((chunk.size - 1) * 8 - index * 8))
            }
            val discard = 4 - chunk.size
            val discarded = (result shl (8 * discard)) shr (8 * discard)
            uintArrayOf(discarded)
        }.toUIntArray()
        if (collected.contentEquals(ZERO)) {
            return Pair(ZERO, Sign.ZERO)
        }
        val corrected = collected.dropLastWhile { it == 0U }.toUIntArray()

        return Pair(removeLeadingZeros(corrected), resolvedSign)
    }

    override fun fromUByteArray(uByteArray: UByteArray, endianness: Endianness): Pair<UIntArray, Sign> {
        val chunked = when (endianness) {
            Endianness.BIG -> {
                uByteArray.toList().reversed().chunked(4)
            }
            Endianness.LITTLE -> {
                uByteArray.toList().chunked(4)
            }
        }

        val resolvedSign = Sign.POSITIVE

        val collected = chunked.flatMap { chunk ->
            val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                acc + ((byte.toUInt() and 0xFFU) shl ((chunk.size - 1) * 8 - index * 8))
            }
            val discard = 4 - chunk.size
            val discarded = (result shl (8 * discard)) shr (8 * discard)
            uintArrayOf(discarded)
        }.toUIntArray()
        if (collected.contentEquals(ZERO)) {
            return Pair(ZERO, Sign.ZERO)
        }
        val corrected = collected.dropLastWhile { it == 0U }.toUIntArray()

        return Pair(removeLeadingZeros(corrected), resolvedSign)
    }

    override fun toTypedUByteArray(operand: UIntArray, endianness: Endianness): Array<UByte> {
        val corrected = when (endianness) {
            Endianness.BIG -> {
                var index = 0
                val collected = operand.reversed().flatMap {
                    val leadingZeroBytes = if (index == operand.size - 1) {
                        numberOfLeadingZerosInAWord(it) / 8
                    } else {
                        0
                    }
                    val converted = listOf(
                        ((it shr 24) and 0xFFU).toUByte(),
                        ((it shr 16) and 0xFFU).toUByte(),
                        ((it shr 8) and 0xFFU).toUByte(),
                        ((it) and 0xFFU).toUByte()
                    )
                    index++
                    converted.drop(leadingZeroBytes)
                }
                collected
            }
            Endianness.LITTLE -> {
                var index = 0
                val collected = operand.reversed().flatMap {
                    val leadingZeroBytes = if (index == operand.size - 1) {
                        numberOfLeadingZerosInAWord(it) / 8
                    } else {
                        0
                    }
                    val converted = listOf(
                        ((it) and 0xFFU).toUByte(),
                        ((it shr 8) and 0xFFU).toUByte(),
                        ((it shr 16) and 0xFFU).toUByte(),
                        ((it shr 24) and 0xFFU).toUByte()
                    )
                    index++
                    converted.dropLast(leadingZeroBytes)
                }
                collected
            }
        }.toTypedArray()
        return corrected.dropLeadingZeros()
    }

    override fun toUByteArray(operand: UIntArray, endianness: Endianness): UByteArray {
        val corrected = when (endianness) {
            Endianness.BIG -> {
                var index = 0
                val collected = operand.reversed().flatMap {
                    val leadingZeroBytes = if (index == operand.size - 1) {
                        numberOfLeadingZerosInAWord(it) / 8
                    } else {
                        0
                    }
                    val converted = listOf(
                        ((it shr 24) and 0xFFU).toUByte(),
                        ((it shr 16) and 0xFFU).toUByte(),
                        ((it shr 8) and 0xFFU).toUByte(),
                        ((it) and 0xFFU).toUByte()
                    )
                    index++
                    converted.drop(leadingZeroBytes)
                }
                collected
            }
            Endianness.LITTLE -> {
                val collected = operand.flatMap {
                    var index = 0
                    val collected = operand.reversed().flatMap {
                        val leadingZeroBytes = if (index == operand.size - 1) {
                            numberOfLeadingZerosInAWord(it) / 8
                        } else {
                            0
                        }
                        val converted = listOf(
                            ((it) and 0xFFU).toUByte(),
                            ((it shr 8) and 0xFFU).toUByte(),
                            ((it shr 16) and 0xFFU).toUByte(),
                            ((it shr 24) and 0xFFU).toUByte()
                        )
                        index++
                        converted.dropLast(leadingZeroBytes)
                    }
                    collected
                }
                collected.toUByteArray()
            }
        }
        return corrected.toUByteArray()
    }

    private fun List<Byte>.dropLeadingZeros(): List<Byte> {
        return this.dropWhile { it == 0.toByte() }
    }

    private fun Array<Byte>.dropLeadingZeros(): Array<Byte> {
        return this.dropWhile { it == 0.toByte() }.toTypedArray()
    }

    private fun Array<UByte>.dropLeadingZeros(): Array<UByte> {
        return this.dropWhile { it == 0.toUByte() }.toTypedArray()
    }

    private fun UByteArray.dropLeadingZeros(): UByteArray {
        return this.dropWhile { it == 0.toUByte() }.toUByteArray()
    }
}