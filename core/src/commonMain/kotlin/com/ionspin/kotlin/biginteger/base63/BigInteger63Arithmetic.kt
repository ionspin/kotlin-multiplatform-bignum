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

package com.ionspin.kotlin.biginteger.base63

import com.ionspin.kotlin.biginteger.BigIntegerArithmetic
import com.ionspin.kotlin.biginteger.Quadruple
import com.ionspin.kotlin.biginteger.base32.BigInteger32Arithmetic

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
@ExperimentalUnsignedTypes
object BigInteger63Arithmetic : BigIntegerArithmetic<ULongArray, ULong> {
    override val ZERO: ULongArray = ulongArrayOf(0u)
    override val ONE: ULongArray = ulongArrayOf(1u)
    override val basePowerOfTwo: Int = 63

    val baseMask: ULong = 0x7FFFFFFFFFFFFFFFUL

    val lowMask      = 0x00000000FFFFFFFFUL
    val highMask     = 0x7FFFFFFF00000000UL
    val overflowMask = 0x8000000000000000UL


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

    override fun bitLength(value: ULongArray): Int {
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size - 1) * 63
    }

    fun bitLength(value: ULong): Int {
        return 63 - numberOfLeadingZeroes(value)
    }

    fun removeLeadingZeroes(bigInteger: ULongArray): ULongArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0UL } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            //Array is equal to zero, so we return array with zero elements
            return ZERO
        }
        return bigInteger.copyOfRange(0, firstEmpty)

    }

    override fun shiftLeft(operand: ULongArray, places: Int): ULongArray {
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
            return ULongArray(operand.size + wordsNeeded) {
                when (it) {
                    in 0 until shiftWords -> 0U
                    else -> operand[it - shiftWords]
                }
            }
        }
        return ULongArray(operand.size + wordsNeeded) {
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
        val shiftBits = (places % basePowerOfTwo)
        val wordsToDiscard = places / basePowerOfTwo
        if (wordsToDiscard >= operand.size) {
            return ZERO
        }

        if (shiftBits == 0) {
            operand.copyOfRange(operand.size - wordsToDiscard, operand.size)
        }

        if (operand.size > 1 && operand.size - wordsToDiscard == 1) {
            return ulongArrayOf((operand[operand.size - 1] shr shiftBits))
        }


        val result = ULongArray(operand.size - wordsToDiscard) {
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

    override fun compare(first: ULongArray, second: ULongArray): Int {
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
        if (firstIsLarger) {
            return 1
        } else {
            return -1
        }
    }

    override fun add(first: ULongArray, second: ULongArray): ULongArray {
        if (first.size == 1 && first[0] == 0UL) return second
        if (second.size == 1 && second[0] == 0UL) return first

        val (maxLength, minLength, largerData, smallerData) = if (first.size > second.size) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }
        val result = ULongArray(maxLength + 1) { index -> 0u }
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
                    result.copyOfRange(0, result.size - 1)
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

    override fun substract(first: ULongArray, second: ULongArray): ULongArray {
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
            throw RuntimeException("subtraction result less than zero")
        }
        val (largerLength, smallerLength, largerData, smallerData) = if (firstIsLarger) {
            Quadruple(firstPrepared.size, secondPrepared.size, firstPrepared, secondPrepared)
        } else {
            Quadruple(secondPrepared.size, firstPrepared.size, secondPrepared, firstPrepared)
        }
        val result = ULongArray(largerLength + 1) { index -> 0u }
        var i = 0
        var diff: ULong = 0u
        while (i < smallerLength) {
            diff = largerData[i] - smallerData[i] - diff
            if ((diff and overflowMask) shr 63 == 1UL) {
                result[i] = diff and baseMask
            } else {
                result[i] = diff and baseMask
            }
            diff = diff shr 63
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i] - diff
            if ((diff and overflowMask) shr 63 == 1UL) {
                result[i] = (diff - 1UL) and baseMask
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
            return ULongArray(0)
        }


        return removeLeadingZeroes(result)
    }

    override fun multiply(first: ULongArray, second: ULongArray): ULongArray {
        var resultArray = ulongArrayOf()
        second.forEachIndexed { index: Int, element: ULong ->
            resultArray = resultArray + (multiply(first, element) shl (index * basePowerOfTwo))
        }
        return removeLeadingZeroes(resultArray)

    }

    fun multiply(first: ULongArray, second: ULong): ULongArray {

        val secondLow = second and lowMask
        val secondHigh = second shr 32

        val result = ULongArray(first.size + 1)

        var carryIntoNextRound = 0UL
        var i = 0
        var j = 0
        while (i < first.size) {
            val firstLow = first[i] and lowMask
            val firstHigh = first[i] shr 32
            i++

            //Calculate low part product
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
    Useful when we want to do a ULong * ULong -> ULongArray, currently not used anywhere, and untested
     */
    fun multiply(first: ULong, second: ULong): ULongArray {
        //Split the operands
        val firstLow = first and lowMask
        val firstHigh = first shr 32
        val secondLow = second and lowMask
        val secondHigh = second shr 32


        //Calculate low part product
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

        return removeLeadingZeroes(ulongArrayOf(lowResult and baseMask, highResult))
    }

    fun normalize(dividend: ULongArray, divisor: ULongArray): Triple<ULongArray, ULongArray, Int> {
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val normalizationShift = numberOfLeadingZeroes(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)

    }

    fun normalize(operand: ULongArray): Pair<ULongArray, Int> {
        val normalizationShift = numberOfLeadingZeroes(operand[operand.size - 1])
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
                removeLeadingZeroes(
                    ulongArrayOf(
                        unnormalizedDividend[0] / unnormalizedDivisor[0]
                    )
                ),
                removeLeadingZeroes(
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


        var qjhat : ULongArray
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
            val convertedResult = BigInteger32Arithmetic.divide(twoDigit.to32Bit(), ulongArrayOf(divisor[divisorSize - 1]).to32Bit())
            qjhat = convertedResult.first.from32Bit()
            quotient[j] = if (qjhat < (baseMask - 1UL)) {
                qjhat[0]
            } else {
                baseMask - 1U
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
        return Pair(removeLeadingZeroes(quotient), denormRemainder)
    }

    fun convertTo64BitRepresentation(operand: ULongArray): ULongArray {
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

    fun convertTo32BitRepresentation(operand: ULongArray): UIntArray {
        val power64Representation = convertTo64BitRepresentation(operand)
        val result = UIntArray(power64Representation.size * 2)
        for (i in 0 until power64Representation.size) {
            result[2 * i] = (power64Representation[i] and BigInteger32Arithmetic.base.toULong()).toUInt()
            result[2 * i + 1] = (power64Representation[i] shr 32).toUInt()
        }

        return BigInteger32Arithmetic.removeLeadingZeroes(result)
    }

    fun convertFrom32BitRepresentation(operand: UIntArray): ULongArray {
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
        var skipWordCount = 0
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

    override fun divide(first: ULongArray, second: ULongArray): Pair<ULongArray, ULongArray> {
        return baseDivide(first, second)
    }

    override fun parseForBase(number: String, base: Int) : ULongArray {
        var parsed = ZERO
        number.forEachIndexed {index, char ->
            val previous = (parsed * base.toULong())
            parsed = previous + (char.toInt() - 48).toULong()
            val temp = 1
        }
        return parsed
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

    fun longArrayAnd(operand: ULongArray, mask: ULongArray): ULongArray {
        return ULongArray(operand.size) {
            if (it < mask.size) {
                operand[it] and mask[it]
            } else {
                0UL
            }
        }
    }

    fun longArrayOr(operand: ULongArray, mask: ULongArray): ULongArray {
        return ULongArray(operand.size) {
            if (it < mask.size) {
                operand[it] or mask[it]
            } else {
                operand[it]
            }
        }
    }

    fun longArrayXor(operand: ULongArray, mask: ULongArray): ULongArray {
        return ULongArray(operand.size) {
            if (it < mask.size) {
                operand[it] xor mask[it]
            } else {
                operand[it] xor 0UL
            }
        }
    }

    fun longArrayInv(operand: ULongArray): ULongArray {
        return ULongArray(operand.size) {
            operand[it].inv()
        }
    }

    // -------------- Bitwise ---------------- //

    private infix fun ULongArray.and(other: ULongArray): ULongArray {
        return longArrayAnd(this, other)
    }

    private infix fun ULongArray.or(other: ULongArray): ULongArray {
        return longArrayOr(this, other)
    }

    private infix fun ULongArray.xor(other: ULongArray): ULongArray {
        return longArrayXor(this, other)
    }

    private infix fun ULongArray.and(other: ULong): ULongArray {
        return longArrayAnd(this, ulongArrayOf(other))
    }

    private infix fun ULongArray.or(other: ULong): ULongArray {
        return longArrayOr(this, ulongArrayOf(other))
    }

    private infix fun ULongArray.xor(other: ULong): ULongArray {
        return longArrayXor(this, ulongArrayOf(other))
    }


    internal infix fun ULongArray.shl(places: Int): ULongArray {
        return shiftLeft(this, places)
    }

    internal infix fun ULongArray.shr(places: Int): ULongArray {
        return shiftRight(this, places)
    }

    // -------------- Operations ---------------- //


    internal operator fun ULongArray.plus(other: ULongArray): ULongArray {
        return add(this, other)
    }

    internal operator fun ULongArray.minus(other: ULongArray): ULongArray {
        return substract(this, other)
    }

    internal operator fun ULongArray.times(other: ULongArray): ULongArray {
        return multiply(this, other)
    }

    internal operator fun ULongArray.plus(other: ULong): ULongArray {
        return add(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.minus(other: ULong): ULongArray {
        return substract(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.times(other: ULong): ULongArray {
        return multiply(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.div(other: ULong): ULongArray {
        return baseDivide(this, ulongArrayOf(other)).first
    }

    internal operator fun ULongArray.rem(other: ULong): ULongArray {
        return baseDivide(this, ulongArrayOf(other)).second
    }

    internal operator fun ULongArray.div(other: ULongArray): ULongArray {
        return baseDivide(this, other).first
    }

    internal operator fun ULongArray.rem(other: ULongArray): ULongArray {
        return baseDivide(this, other).second
    }

    internal infix fun ULongArray.divrem(other: ULongArray): Pair<ULongArray, ULongArray> {
        return baseDivide(this, other)
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

    override fun fromLong(long: Long): ULongArray {
        if ((long.toULong() and overflowMask shr 63) == 1UL) {
            return ulongArrayOf(baseMask) + 1U
        }
        return ulongArrayOf((long.toULong() and baseMask))

    }

    override fun fromInt(int: Int): ULongArray = ulongArrayOf(int.toULong())

    override fun fromShort(short: Short): ULongArray = ulongArrayOf(short.toULong())

    override fun fromByte(byte: Byte): ULongArray = ulongArrayOf(byte.toULong())


}