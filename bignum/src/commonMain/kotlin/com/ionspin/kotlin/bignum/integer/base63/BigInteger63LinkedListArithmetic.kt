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
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.base32.BigInteger32Arithmetic
import com.ionspin.kotlin.bignum.integer.util.toDigit
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Just to experiment and see if using immutable lists is better optimized than using arrays.
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Mar-2019
 */
@ExperimentalUnsignedTypes
internal object BigInteger63LinkedListArithmetic : BigIntegerArithmetic<List<ULong>, ULong> {
    override val ZERO: List<ULong> = listOf(0u)
    override val ONE: List<ULong> = listOf(1u)
    override val TWO: List<ULong> = listOf(2u)
    override val TEN: List<ULong> = listOf(10UL)
    override val basePowerOfTwo: Int = 63
    val wordSizeInBits = 63

    val baseMask: ULong = 0x7FFFFFFFFFFFFFFFUL

    val lowMask = 0x00000000FFFFFFFFUL
    val highMask = 0x7FFFFFFF00000000UL
    val overflowMask = 0x8000000000000000UL

    const val karatsubaThreshold = 45

    override fun numberOfLeadingZeroes(value: ULong): Int {
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

    override fun bitLength(value: List<ULong>): Int {
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size - 1) * 63
    }

    fun bitLength(value: ULong): Int {
        return 63 - numberOfLeadingZeroes(value)
    }

    fun trailingZeroBits(value: ULong): Int {
        return 63 - bitLength(value.inv() and baseMask)
    }

    override fun trailingZeroBits(value: List<ULong>): Int {
        TODO()
    }

    fun removeLeadingZeroes(bigInteger: List<ULong>): List<ULong> {
        val firstEmpty = bigInteger.indexOfLast { it != 0UL } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            // Array is equal to zero, so we return array with zero elements
            return ZERO
        }
        return bigInteger.subList(0, firstEmpty)
    }

    override fun shiftLeft(operand: List<ULong>, places: Int): List<ULong> {
        if (operand == ZERO) {
            return operand
        }
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val originalSize = operand.size
        val leadingZeroes =
            numberOfLeadingZeroes(operand[operand.size - 1])
        val shiftWords = places / basePowerOfTwo
        val shiftBits = places % basePowerOfTwo
        val wordsNeeded = if (shiftBits > leadingZeroes) {
            shiftWords + 1
        } else {
            shiftWords
        }
        if (shiftBits == 0) {
            return List(operand.size + wordsNeeded) {
                when (it) {
                    in 0 until shiftWords -> 0U
                    else -> operand[it - shiftWords]
                }
            }
        }
        return List(operand.size + wordsNeeded) {
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

    override fun shiftRight(operand: List<ULong>, places: Int): List<ULong> {
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val shiftBits = (places % basePowerOfTwo)
        val wordsToDiscard = places / basePowerOfTwo
        if (wordsToDiscard >= operand.size) {
            return ZERO
        }

        if (shiftBits == 0) {
            operand.subList(operand.size - wordsToDiscard, operand.size)
        }

        if (operand.size > 1 && operand.size - wordsToDiscard == 1) {
            return listOf((operand[operand.size - 1] shr shiftBits))
        }

        val result = List<ULong>(operand.size - wordsToDiscard) {
            when (it) {
                in 0 until (operand.size - 1 - wordsToDiscard) -> {
                    ((operand[it + wordsToDiscard] shr shiftBits)) or
                        ((operand[it + wordsToDiscard + 1] shl (basePowerOfTwo - shiftBits) and baseMask))
                }
                operand.size - 1 - wordsToDiscard -> {
                    (operand[it + wordsToDiscard] shr shiftBits)
                }
                else -> {
                    throw RuntimeException("Invalid case $it")
                }
            }
        }
        return removeLeadingZeroes(result)
    }

    override fun compare(first: List<ULong>, second: List<ULong>): Int {
        if (first.size > second.size) {
            return 1
        }
        if (second.size > first.size) {
            return -1
        }

        var counter = first.size - 1
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
        return if (firstIsLarger) {
            1
        } else {
            -1
        }
    }

    override fun add(first: List<ULong>, second: List<ULong>): List<ULong> {
        if (first.size == 1 && first[0] == 0UL) return second
        if (second.size == 1 && second[0] == 0UL) return first

        val (maxLength, minLength, largerData, smallerData) = if (first.size > second.size) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }
        val result = MutableList<ULong>(maxLength + 1) { 0u }
        var i = 0
        var sum: ULong = 0u
        while (i < minLength) {
            sum = sum + largerData[i] + smallerData[i]
            result[i] = sum and baseMask
            sum = sum shr 63
            i++
        }

        while (true) {
            if (sum == 0UL) {
                while (i < maxLength) {
                    result[i] = largerData[i]
                    i++
                }
                val final = if (result[result.size - 1] == 0UL) {
                    result.subList(0, result.size - 1)
                } else {
                    result
                }
                return removeLeadingZeroes(final)
            }
            if (i == maxLength) {
                result[maxLength] = sum
                return removeLeadingZeroes(result)
            }

            sum = sum + largerData[i]
            result[i] = (sum and baseMask)
            sum = sum shr 63
            i++
        }
    }

    override fun substract(first: List<ULong>, second: List<ULong>): List<ULong> {
        val firstPrepared = removeLeadingZeroes(first)
        val secondPrepared = removeLeadingZeroes(second)
        val comparison = compare(firstPrepared, secondPrepared)
        val firstIsLarger = comparison == 1

        if (comparison == 0) return ZERO

        if (second.size == 1 && second[0] == 0UL) {
            return first
        }

        // Lets throw this just to catch when we didn't prepare the operands correctly
        if (!firstIsLarger) {
            throw RuntimeException("subtract result less than zero")
        }
        val (largerLength, smallerLength, largerData, smallerData) = if (firstIsLarger) {
            Quadruple(firstPrepared.size, secondPrepared.size, firstPrepared, secondPrepared)
        } else {
            Quadruple(secondPrepared.size, firstPrepared.size, secondPrepared, firstPrepared)
        }
        val result = MutableList<ULong>(largerLength + 1) { 0u }
        var i = 0
        var diff: ULong = 0u
        while (i < smallerLength) {
            diff = largerData[i] - smallerData[i] - diff
            if ((diff and overflowMask) shr 63 == 1UL) {
                result[i] = (diff and baseMask)
            } else {
                result[i] = diff and baseMask
            }
            diff = diff shr 63
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i] - diff
            if ((diff and overflowMask) shr 63 == 1UL) {
                result[i] = diff and baseMask
            } else {
                result[i] = diff and baseMask
                diff = 0UL
            }
            diff = diff shr 63
            i++
        }

        while (i < largerLength) {
            result[i] = largerData[i]
            i++
        }

        if (result.filter { it == 0UL }.isEmpty()) {
            return List<ULong>(0) { 0UL }
        }

        return removeLeadingZeroes(result)
    }

    override fun multiply(first: List<ULong>, second: List<ULong>): List<ULong> {
        if (first == ZERO || second == ZERO) {
            return ZERO
        }

        if (first.size >= karatsubaThreshold || second.size == karatsubaThreshold) {
            return karatsubaMultiply(first, second)
        }

        var resultArray = listOf<ULong>()
        second.forEachIndexed { index: Int, element: ULong ->
            resultArray = resultArray + (multiply(first, element) shl (index * basePowerOfTwo))
        }
        return removeLeadingZeroes(resultArray)
    }

    fun karatsubaMultiply(first: List<ULong>, second: List<ULong>): List<ULong> {

        val halfLength = (kotlin.math.max(first.size, second.size) + 1) / 2

        val mask = (ONE shl (halfLength * wordSizeInBits)) - 1UL
        val firstLower = and(first, mask)
        val firstHigher = first shr halfLength * wordSizeInBits
        val secondLower = and(second, mask)
        val secondHigher = second shr halfLength * wordSizeInBits

        //
        val higherProduct = firstHigher * secondHigher
        val lowerProduct = firstLower * secondLower
        val middleProduct = (firstHigher + firstLower) * (secondHigher + secondLower)
        val result =
            (higherProduct shl (126 * halfLength)) + ((middleProduct - higherProduct - lowerProduct) shl (wordSizeInBits * halfLength)) + lowerProduct

        return result
    }

    fun multiply(first: List<ULong>, second: ULong): List<ULong> {

        val secondLow = second and lowMask
        val secondHigh = second shr 32

        val result = MutableList<ULong>(first.size + 1) { 0UL }

        var carryIntoNextRound = 0UL
        var i = 0
        var j = 0
        while (i < first.size) {
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
        return removeLeadingZeroes(result)
    }

    /*
    Useful when we want to do a ULong * ULong -> List<ULong>, currently not used anywhere, and untested
     */
    fun multiply(first: ULong, second: ULong): List<ULong> {
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

        return removeLeadingZeroes(listOf(lowResult and baseMask, highResult))
    }

    override fun pow(base: List<ULong>, exponent: Long): List<ULong> {
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

    fun normalize(dividend: List<ULong>, divisor: List<ULong>): Triple<List<ULong>, List<ULong>, Int> {
        val divisorSize = divisor.size
        val normalizationShift = numberOfLeadingZeroes(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)
    }

    fun normalize(operand: List<ULong>): Pair<List<ULong>, Int> {
        val normalizationShift = numberOfLeadingZeroes(operand[operand.size - 1])
        return Pair(operand.shl(normalizationShift), normalizationShift)
    }

    fun denormalize(
        remainderNormalized: List<ULong>,
        normalizationShift: Int
    ): List<ULong> {
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
        unnormalizedDividend: List<ULong>,
        unnormalizedDivisor: List<ULong>
    ): Pair<List<ULong>, List<ULong>> {
        if (unnormalizedDivisor > unnormalizedDividend) {
            return Pair(ZERO, unnormalizedDividend)
        }
        if (unnormalizedDivisor.size == 1 && unnormalizedDividend.size == 1) {
            return Pair(
                removeLeadingZeroes(
                    listOf(
                        unnormalizedDividend[0] / unnormalizedDivisor[0]
                    )
                ),
                removeLeadingZeroes(
                    listOf(
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
        val wordPrecision = dividendSize - divisorSize

        var qjhat: List<ULong>
        var reconstructedQuotient: List<ULong>
        var quotient = MutableList<ULong>(wordPrecision) { 0UL }

        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision * basePowerOfTwo))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient = MutableList<ULong>(wordPrecision + 1) { 0UL }
            quotient[wordPrecision] = 1U
            dividend = dividend - divisorTimesBaseToPowerOfM
        }

        for (j in (wordPrecision - 1) downTo 0) {
            val twoDigit = if (divisorSize + j < dividend.size) {
                ((listOf(dividend[divisorSize + j]) shl basePowerOfTwo) + dividend[divisorSize + j - 1])
            } else {
                if (divisorSize + j == dividend.size) {
                    listOf(dividend[divisorSize + j - 1])
                } else {
                    ZERO
                }
            }
            val convertedResult =
                BigInteger32Arithmetic.divide(twoDigit.to32Bit(), listOf(divisor[divisorSize - 1]).to32Bit())
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
            quotient = quotient.plus(1UL).toMutableList()
            dividend -= divisor
        }
        val denormRemainder =
            denormalize(dividend, normalizationShift)
        return Pair(removeLeadingZeroes(quotient), denormRemainder)
    }

    fun convertTo64BitRepresentation(operand: List<ULong>): List<ULong> {
        if (operand == ZERO) return ZERO
        val length = bitLength(operand)
        val requiredLength = if (length % 64 == 0) {
            length / 64
        } else {
            (length / 64) + 1
        }
        var wordStep: Int
        var shiftAmount: Int

        val result = MutableList<ULong>(requiredLength) { 0UL }
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

    fun convertTo32BitRepresentation(operand: List<ULong>): UIntArray {
        val power64Representation = convertTo64BitRepresentation(operand)
        val result = UIntArray(power64Representation.size * 2)
        for (i in 0 until power64Representation.size) {
            result[2 * i] = (power64Representation[i] and BigInteger32Arithmetic.base.toULong()).toUInt()
            result[2 * i + 1] = (power64Representation[i] shr 32).toUInt()
        }

        return BigInteger32Arithmetic.removeLeadingZeroes(result)
    }

    fun convertFrom32BitRepresentation(operand: UIntArray): List<ULong> {
        if (operand.size == 0) {
            return ZERO
        }
        if (operand.size == 1) {
            return listOf(operand[0].toULong())
        }
        val length = BigInteger32Arithmetic.bitLength(operand)
        val requiredLength = if (length % 63 == 0) {
            length / 63
        } else {
            (length / 63) + 1
        }

        val result = MutableList<ULong>(requiredLength) { 0UL }
        var skipWordCount: Int
        for (i in 0 until requiredLength) {
            skipWordCount = i / 32
            val shiftAmount = i % 32
            val position = (i * 2) - skipWordCount
            when (i) {
                0 -> {
                    result[i] = operand[(i * 2)].toULong() or ((operand[(i * 2) + 1].toULong() shl 32) and highMask)
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
//        if (operand.size % 2 != 0) {
//            val lastI = requiredLength - 1 + skipWordCount
//            result[lastI] =
//                (operand[(lastI * 2) - 1].toULong() shr (32 - lastI)) or (operand[(lastI * 2)].toULong() shl lastI)
//        }
//        result[requiredLength - 1] = (operand[operand.size - 1].toULong() shl ((operand.size - 1) / 2)) or (operand[operand.size - 2].toULong() shr (32 - (operand.size - 1)/2))
        return result
    }

    override fun divide(first: List<ULong>, second: List<ULong>): Pair<List<ULong>, List<ULong>> {
        return baseDivide(first, second)
    }

    override fun reciprocal(operand: List<ULong>): Pair<List<ULong>, List<ULong>> {
        return d1ReciprocalRecursiveWordVersion(operand)
    }

    fun d1ReciprocalRecursiveWordVersion(a: List<ULong>): Pair<List<ULong>, List<ULong>> {
        val n = a.size - 1
        if (n <= 2) {
            val corrected = if (n == 0) {
                1
            } else {
                n
            }
            val rhoPowered = ONE shl (corrected * 2 * BigInteger63Arithmetic.wordSizeInBits)
            val x = rhoPowered / a
            val r = rhoPowered - (x * a)
            return Pair(x, r)
        }
        val l = floor((n - 1).toDouble() / 2).toInt()
        val h = n - l
        val ah = a.subList(a.size - h - 1, a.size)
        val al = a.subList(0, l)
        var (xh, rh) = d1ReciprocalRecursiveWordVersion(ah)
        val s = al * xh
//        val rhoL = (ONE shl l)
        val rhRhoL = rh shl (l * BigInteger63Arithmetic.wordSizeInBits)
        val t = if (rhRhoL >= s) {
            rhRhoL - s
        } else {
            xh = xh - BigInteger63Arithmetic.ONE
            (rhRhoL + a) - s
        }
        val tm = t shr (h * BigInteger63Arithmetic.wordSizeInBits)
        val d = (xh * tm) shr (h * BigInteger63Arithmetic.wordSizeInBits)
        var x = (xh shl (l * BigInteger63Arithmetic.wordSizeInBits)) + d
        var r = (t shl (l * BigInteger63Arithmetic.wordSizeInBits)) - a * d
        if (r >= a) {
            x = x + BigInteger63Arithmetic.ONE
            r = r - a
            if (r >= a) {
                x = x + BigInteger63Arithmetic.ONE
                r = r - a
            }
        }
        return Pair(x, r)
    }

    override fun sqrt(operand: List<ULong>): Pair<List<ULong>, List<ULong>> {
        return reqursiveSqrt(operand)
    }

    private fun reqursiveSqrt(operand: List<ULong>): Pair<List<ULong>, List<ULong>> {
        val n = operand.size
        val l = floor((n - 1).toDouble() / 4).toInt()
        if (l == 0) {
            return basecaseSqrt(operand)
        }
        val step = n / 4
        val stepRemainder = n % 4
        val baseLPowerShift = 63 * l
        val a1 = operand.subList(n - ((3 * step) + stepRemainder), n - ((2 * step) + stepRemainder))
        val a0 = operand.subList(0, n - ((3 * step) + stepRemainder))
        val a3a2 = operand.subList(n - ((2 * step) + stepRemainder), n)

        val (sPrim, rPrim) = reqursiveSqrt(a3a2)
        val (q, u) = ((rPrim shl baseLPowerShift) + a1) divrem (sPrim shl 1)
        var s = (sPrim shl baseLPowerShift) + q
        var r = (u shl baseLPowerShift) + a0 - (q * q)
        return Pair(s, r)
    }

    internal fun basecaseSqrt(operand: List<ULong>): Pair<List<ULong>, List<ULong>> {
        val sqrt = sqrtInt(operand)
        val remainder = operand - (sqrt * sqrt)
        return Pair(sqrt, remainder)
    }

    internal fun sqrtInt(operand: List<ULong>): List<ULong> {
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

    override fun gcd(first: List<ULong>, second: List<ULong>): List<ULong> {
        return if (first.size > 150 || second.size > 150) {
            euclideanGcd(first, second)
        } else {
            binaryGcd(first, second)
        }
    }

    private fun euclideanGcd(first: List<ULong>, second: List<ULong>): List<ULong> {
        var u = first
        var v = second
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
    }

    private tailrec fun binaryGcd(first: List<ULong>, second: List<ULong>): List<ULong> {
        if (first == second) {
            return first
        }

        if (first == ZERO) {
            return second
        }

        if (second == ZERO) {
            return first
        }

        if (and(first, ONE) == ZERO) { // first is even
            if (and(second, ONE) == ZERO) { // second is even
                return binaryGcd(first shr 1, second shr 1) shl 1
            } else { // second is odd
                return binaryGcd(first shr 1, second)
            }
        }

        if (and(second, ONE) == ZERO) {
            return binaryGcd(first, second shr 1)
        }

        return if (compare(first, second) == 1) {
            binaryGcd(substract(first, second) shr 1, second)
        } else {
            binaryGcd(substract(second, first) shr 1, first)
        }
    }

    override fun parseForBase(number: String, base: Int): List<ULong> {
        var parsed = ZERO
        number.toLowerCase().forEach { char ->
            parsed = (parsed * base.toULong()) + (char.toDigit()).toULong()
        }
        return removeLeadingZeroes(parsed)
    }

    override fun toString(operand: List<ULong>, base: Int): String {
        var copy = operand
        val baseArray = listOf(base.toULong())
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

    override fun numberOfDecimalDigits(operand: List<ULong>): Long {
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

    override fun and(operand: List<ULong>, mask: List<ULong>): List<ULong> {
        return removeLeadingZeroes(
            List<ULong>(operand.size) {
                if (it < mask.size) {
                    operand[it] and mask[it]
                } else {
                    0UL
                }
            }
        )
    }

    override fun or(operand: List<ULong>, mask: List<ULong>): List<ULong> {
        return removeLeadingZeroes(
            List(operand.size) {
                if (it < mask.size) {
                    operand[it] or mask[it]
                } else {
                    operand[it]
                }
            }
        )
    }

    override fun xor(operand: List<ULong>, mask: List<ULong>): List<ULong> {
        return removeLeadingZeroes(
            List(operand.size) {
                if (it < mask.size) {
                    operand[it] xor mask[it]
                } else {
                    operand[it] xor 0UL
                }
            }
        )
    }

    override fun not(operand: List<ULong>): List<ULong> {
        val leadingZeroes = numberOfLeadingZeroes(operand[operand.size - 1])
        val cleanupMask = (((1UL shl leadingZeroes + 1) - 1U) shl (basePowerOfTwo - leadingZeroes)).inv()
        val inverted = List<ULong>(operand.size) {
            if (it < operand.size - 2) {
                operand[it].inv() and baseMask
            } else {
                operand[it].inv() and cleanupMask
            }
        }

        return inverted
    }

    // -------------- Bitwise ---------------- //

    internal infix fun List<ULong>.shl(places: Int): List<ULong> {
        return shiftLeft(this, places)
    }

    internal infix fun List<ULong>.shr(places: Int): List<ULong> {
        return shiftRight(this, places)
    }

    override fun bitAt(operand: List<ULong>, position: Long): Boolean {
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

    override fun setBitAt(operand: List<ULong>, position: Long, bit: Boolean): List<ULong> {
        if (position / 63 > Int.MAX_VALUE) {
            throw RuntimeException("Invalid bit index, too large, cannot access word (Word position > Int.MAX_VALUE")
        }

        val wordPosition = position / 63
        if (wordPosition >= operand.size) {
            throw IndexOutOfBoundsException("Invalid position, addressed word $wordPosition larger than number of words ${operand.size}")
        }
        val bitPosition = position % 63
        val setMask = 1UL shl bitPosition.toInt()
        return List(operand.size) {
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

    internal operator fun List<ULong>.plus(other: List<ULong>): List<ULong> {
        return add(this, other)
    }

    internal operator fun List<ULong>.minus(other: List<ULong>): List<ULong> {
        return substract(this, other)
    }

    internal operator fun List<ULong>.times(other: List<ULong>): List<ULong> {
        return multiply(this, other)
    }

    internal operator fun List<ULong>.plus(other: ULong): List<ULong> {
        return add(this, listOf(other))
    }

    internal operator fun List<ULong>.minus(other: ULong): List<ULong> {
        return substract(this, listOf(other))
    }

    internal operator fun List<ULong>.times(other: ULong): List<ULong> {
        return multiply(this, listOf(other))
    }

    internal operator fun List<ULong>.div(other: ULong): List<ULong> {
        return baseDivide(this, listOf(other)).first
    }

    internal operator fun List<ULong>.rem(other: ULong): List<ULong> {
        return baseDivide(this, listOf(other)).second
    }

    internal operator fun List<ULong>.div(other: List<ULong>): List<ULong> {
        return baseDivide(this, other).first
    }

    internal operator fun List<ULong>.rem(other: List<ULong>): List<ULong> {
        return baseDivide(this, other).second
    }

    internal infix fun List<ULong>.divrem(other: List<ULong>): Pair<List<ULong>, List<ULong>> {
        return baseDivide(this, other)
    }

    internal operator fun List<ULong>.compareTo(other: List<ULong>): Int {
        return compare(this, other)
    }

    internal operator fun List<ULong>.compareTo(other: ULong): Int {
        return compare(this, listOf(other))
    }

    internal fun List<ULong>.to32Bit(): UIntArray {
        return convertTo32BitRepresentation(this)
    }

    internal fun UIntArray.from32Bit(): List<ULong> {
        return convertFrom32BitRepresentation(this)
    }

    override fun fromULong(uLong: ULong): List<ULong> = listOf(uLong)

    override fun fromUInt(uInt: UInt): List<ULong> = listOf(uInt.toULong())

    override fun fromUShort(uShort: UShort): List<ULong> = listOf(uShort.toULong())

    override fun fromUByte(uByte: UByte): List<ULong> = listOf(uByte.toULong())

    override fun fromLong(long: Long): List<ULong> {
        if ((long.absoluteValue.toULong() and overflowMask shr 63) == 1UL) {
            return listOf(baseMask) + 1U
        }
        return listOf((long.absoluteValue.toULong() and baseMask))
    }

    override fun fromInt(int: Int): List<ULong> = listOf(int.absoluteValue.toULong())

    override fun fromShort(short: Short): List<ULong> = listOf(short.toInt().absoluteValue.toULong())

    override fun fromByte(byte: Byte): List<ULong> = listOf(byte.toInt().absoluteValue.toULong())

    override fun toByteArray(operand: List<ULong>, sign: Sign): Array<Byte> {
        return BigInteger32Arithmetic.toByteArray(convertTo32BitRepresentation(operand), sign)
    }

    override fun fromByteArray(byteArray: Array<Byte>): Pair<List<ULong>, Sign> {
        val result = BigInteger32Arithmetic.fromByteArray(byteArray)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun fromUByteArray(uByteArray: Array<UByte>, endianness: Endianness): Pair<List<ULong>, Sign> {
        val result = BigInteger32Arithmetic.fromUByteArray(uByteArray, endianness)
        return Pair(convertFrom32BitRepresentation(result.first), result.second)
    }

    override fun toUByteArray(operand: List<ULong>, endianness: Endianness): Array<UByte> {
        val result = BigInteger32Arithmetic.toUByteArray(convertTo32BitRepresentation(operand), endianness)
        return result
    }

    // ------------- Useful constants --------------
    val powersOf10 = arrayOf(
        listOf(1UL),
        listOf(10UL),
        listOf(100UL),
        listOf(1000UL),
        listOf(10000UL),
        listOf(100000UL),
        listOf(1000000UL),
        listOf(10000000UL),
        listOf(100000000UL),
        listOf(1000000000UL),
        listOf(10000000000UL),
        listOf(100000000000UL),
        listOf(1000000000000UL),
        listOf(10000000000000UL),
        listOf(100000000000000UL),
        listOf(1000000000000000UL),
        listOf(10000000000000000UL),
        listOf(100000000000000000UL),
        listOf(1000000000000000000UL),
        listOf(776627963145224192UL, 1UL),
        listOf(7766279631452241920UL, 10UL),
        listOf(3875820019684212736UL, 108UL),
        listOf(1864712049423024128UL, 1084UL),
        listOf(200376420520689664UL, 10842UL),
        listOf(2003764205206896640UL, 108420UL),
        listOf(1590897978359414784UL, 1084202UL),
        listOf(6685607746739372032UL, 10842021UL),
        listOf(2292473209410289664UL, 108420217UL),
        listOf(4477988020393345024UL, 1084202172UL),
        listOf(7886392056514347008UL, 10842021724UL),
        listOf(5076944270305263616UL, 108420217248UL),
        listOf(4652582518778757120UL, 1084202172485UL),
        listOf(408965003513692160UL, 10842021724855UL),
        listOf(4089650035136921600UL, 108420217248550UL),
        listOf(4003012203950112768UL, 1084202172485504UL),
        listOf(3136633892082024448UL, 10842021724855044UL),
        listOf(3696222810255917056UL, 108420217248550443UL),
        listOf(68739955140067328UL, 1084202172485504434UL),
        listOf(687399551400673280UL, 1618649688000268532UL, 1UL),
        listOf(6873995514006732800UL, 6963124843147909512UL, 11UL),
        listOf(4176350882083897344UL, 5067644173495664471UL, 117UL),
        listOf(4870020673419870208UL, 4559581550682765674UL, 1175UL),
        listOf(2583346549924823040UL, 8702327359408553513UL, 11754UL),
        listOf(7386721425538678784UL, 4012925262392552860UL, 117549UL),
        listOf(80237960548581376UL, 3235764476506425376UL, 1175494UL),
        listOf(802379605485813760UL, 4687528654499926336UL, 11754943UL),
        listOf(8023796054858137600UL, 758426360725384320UL, 117549435UL),
        listOf(6450984253743169536UL, 7584263607253843208UL, 1175494350UL),
        listOf(9169610316303040512UL, 2055659777700225622UL, 11754943508UL),
        listOf(8685754831337422848UL, 2109853703292704613UL, 117549435082UL),
        listOf(3847199981681246208UL, 2651792959217494523UL, 1175494350822UL),
        listOf(1578511669393358848UL, 8071185518465393618UL, 11754943508222UL),
        listOf(6561744657078812672UL, 6924878889815729717UL, 117549435082228UL),
        listOf(1053842312804696064UL, 4685184640173866521UL, 1175494350822287UL),
        listOf(1315051091192184832UL, 734986217464786171UL, 11754943508222875UL),
        listOf(3927138875067072512UL, 7349862174647861711UL, 117549435082228750UL),
        listOf(2377900603251621888UL, 8935017488495186458UL, 1175494350822287507UL),
        listOf(5332261958806667264UL, 6339826553258882310UL, 2531571471368099271UL, 1UL),
        listOf(7205759403792793600UL, 8058033311460168257UL, 6868970639971441100UL, 12UL),
        listOf(7493989779944505344UL, 6793356819763476113UL, 4126102141730980352UL, 127UL),
        listOf(1152921504606846976UL, 3369963939651330482UL, 4367533269890700295UL, 1274UL),
        listOf(2305843009213693952UL, 6029523285948977397UL, 6781844551487899721UL, 12744UL),
        listOf(4611686018427387904UL, 4955000638361119124UL, 3254841256895566560UL, 127447UL),
        listOf(0UL, 3433146199337312205UL, 4878296458391338181UL, 1274473UL),
        listOf(0UL, 6661345882808794626UL, 2666104399639502773UL, 12744735UL),
        listOf(0UL, 2049854570104515604UL, 8214299922685476121UL, 127447352UL),
        listOf(0UL, 2051801627335604424UL, 8356022932016554748UL, 1274473528UL),
        listOf(0UL, 2071272199646492624UL, 549880988472565210UL, 12744735289UL),
        listOf(0UL, 2265977922755374624UL, 5498809884725652102UL, 127447352890UL),
        listOf(0UL, 4213035153844194624UL, 8871238662982641982UL, 1274473528905UL),
        listOf(0UL, 5236863391022843008UL, 5702038298133437552UL, 12744735289059UL),
        listOf(0UL, 6251773725954551040UL, 1680150760205720677UL, 127447352890596UL),
        listOf(0UL, 7177505038416855552UL, 7578135565202430968UL, 1274473528905961UL),
        listOf(0UL, 7211446126185124864UL, 1994379357186103223UL, 12744735289059618UL),
        listOf(0UL, 7550857003867817984UL, 1497049498151480621UL, 127447352890596182UL),
        listOf(0UL, 1721593743839973376UL, 5747122944660030410UL, 1274473528905961821UL),
        listOf(0UL, 7992565401544957952UL, 2130997225471649253UL, 3521363252204842408UL, 1UL),
        listOf(0UL, 6138677720611373056UL, 2863228181006940922UL, 7543516411484096658UL, 13UL),
        listOf(0UL, 6046544984985075712UL, 962165699505081802UL, 1648187820002760119UL, 138UL),
        listOf(0UL, 5125217628722102272UL, 398284958196042218UL, 7258506163172825383UL, 1381UL),
        listOf(0UL, 5135316102947143680UL, 3982849581960422185UL, 8021457373744823174UL, 13817UL),
        listOf(0UL, 5236300845197557760UL, 2935007672185118623UL, 6427597442610025280UL, 138178UL),
        listOf(0UL, 6246148267701698560UL, 1679960611286858811UL, 8935742204971597955UL, 1381786UL),
        listOf(0UL, 7121250455888330752UL, 7576234076013812308UL, 6347073718022997279UL, 13817869UL),
        listOf(0UL, 6648900300899876864UL, 1975364465299916623UL, 8130504959101317950UL, 138178696UL),
        listOf(0UL, 1925398751015337984UL, 1306900579289614621UL, 7518073296174973038UL, 1381786968UL),
        listOf(0UL, 807243436443828224UL, 3845633756041370404UL, 1393756666911523917UL, 13817869688UL),
        listOf(0UL, 8072434364438282240UL, 1562849412994600808UL, 4714194632260463366UL, 138178696881UL),
        listOf(0UL, 6937367349544615936UL, 6405122093091232280UL, 1025086138330754621UL, 1381786968815UL),
        listOf(0UL, 4810069237462728704UL, 8710988709783667959UL, 1027489346452770408UL, 13817869688151UL),
        listOf(0UL, 1983832190353408000UL, 4099538766143697323UL, 1051521427672928281UL, 138178696881511UL),
        listOf(0UL, 1391577829824528384UL, 4101899514017870000UL, 1291842239874507006UL, 1381786968815111UL),
        listOf(0UL, 4692406261390508032UL, 4125506992759596769UL, 3695050361890294256UL, 13817869688151111UL),
        listOf(0UL, 807202429631201280UL, 4361581780176864463UL, 57015471483839332UL, 138178696881511114UL),
        listOf(0UL, 8072024296312012800UL, 6722329654349541398UL, 570154714838393324UL, 1381786968815111140UL),
        listOf(0UL, 6933266668281921536UL, 2659692285511983332UL, 5701547148383933247UL, 4594497651296335592UL, 1UL),
        listOf(0UL, 4769062424835784704UL, 8150178781410281711UL, 1675239262710677624UL, 9051488365544252694UL, 14UL),
        listOf(0UL, 1573764064083968000UL, 7714811519264610651UL, 7529020590252000440UL, 7504535323749544669UL, 149UL),
        listOf(0UL, 6514268603984904192UL, 3361138897807900047UL, 1503229607681797944UL, 1258376942657240234UL, 1498UL),
        listOf(0UL, 579081781865611264UL, 5941272867514673053UL, 5808924039963203635UL, 3360397389717626533UL, 14981UL),
        listOf(
            0UL,
            5790817818656112640UL,
            4072496454018075682UL,
            2749008178503381508UL,
            5933857786611937912UL,
            149813UL
        )
    )
}