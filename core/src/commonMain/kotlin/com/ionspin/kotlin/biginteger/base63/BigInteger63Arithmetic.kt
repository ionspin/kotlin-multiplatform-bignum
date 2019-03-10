package com.ionspin.kotlin.biginteger.base63

import com.ionspin.kotlin.biginteger.BigIntegerArithmetic
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
    val base: ULong = 0x7FFFFFFFFFFFFFFFUL
    override val basePowerOfTwo: Int = 63

    val highMask = 0x7FFFFFFF00000000UL


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
                    (operand[it - shiftWords] shl shiftBits) and base
                }
                in (shiftWords + 1) until (originalSize + shiftWords) -> {
                    ((operand[it - shiftWords] shl shiftBits) and base) or (operand[it - shiftWords - 1] shr (basePowerOfTwo - shiftBits))
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
        var transfer: UInt = 0U

        val leadingZeroes =
            numberOfLeadingZeroes(operand[operand.size - 1])
        val shiftWords = places / basePowerOfTwo
        val shiftBits = (places % basePowerOfTwo)
        val wordsToDiscard = if (shiftBits >= (basePowerOfTwo - leadingZeroes)) {
            shiftWords + 1
        } else {
            shiftWords
        }
        if (wordsToDiscard >= operand.size) {
            return ZERO
        }

        if (shiftBits == 0) {
            operand.copyOfRange(operand.size - wordsToDiscard, operand.size)
        }

        return ULongArray(operand.size - wordsToDiscard) {
            when (it) {
                in 0..(operand.size - 2 - wordsToDiscard) -> {
                    ((operand[it + wordsToDiscard] shr shiftBits)) or
                            ((operand[it + wordsToDiscard + 1] shl (basePowerOfTwo - shiftBits) and base))
                }
                operand.size - 1 - wordsToDiscard -> {
                    (operand[it + wordsToDiscard] shr shiftBits)
                }
                else -> {
                    throw RuntimeException("Invalid case $it")
                }
            }
        }
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

    override fun addition(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun substract(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
    }

    override fun multiply(first: ULongArray, second: ULongArray): ULongArray {
        TODO("not implemented yet")
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
    fun basicDivide(
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
        val wordPrecision = dividendSize - divisorSize


        var qjhat = ulongArrayOf()
        var reconstructedQuotient: ULongArray
        var quotient = ULongArray(wordPrecision)

        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision * BigInteger32Arithmetic.basePowerOfTwo))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient = ULongArray(wordPrecision + 1)
            quotient[wordPrecision] = 1U
            dividend = dividend - divisorTimesBaseToPowerOfM
        }

        for (j in (wordPrecision - 1) downTo 0) {
            val twoDigit =
                ((ulongArrayOf(dividend[divisorSize + j]) shl basePowerOfTwo) + dividend[divisorSize + j - 1])
            qjhat = BigInteger32Arithmetic.divide(twoDigit.to32Bit(), ulongArrayOf(divisor[divisorSize - 1]).to32Bit())
                .first.from32Bit()
            quotient[j] = if (qjhat < (base - 1UL)) {
                qjhat[0]
            } else {
                base - 1U
            }
            // We don't have signed integers here so we need to check if reconstructed quotient is larger than the dividend
            // instead of just doing  A ← A − qj β B and then looping. Final effect is the same.
            reconstructedQuotient = ((divisor * quotient[j]) shl (j * BigInteger32Arithmetic.basePowerOfTwo))
            while (reconstructedQuotient > dividend) {
                quotient[j] = quotient[j] - 1U
                reconstructedQuotient = ((divisor * quotient[j]) shl (j * BigInteger32Arithmetic.basePowerOfTwo))
            }
            dividend = dividend - reconstructedQuotient
        }

        val denormRemainder =
            denormalize(dividend, normalizationShift)
        return Pair(removeLeadingZeroes(quotient), denormRemainder)
    }

    fun convertTo64BitRepresentation(operand: ULongArray): ULongArray {
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

        return result
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
                        (operand[position - 1].toULong() shr (32 - shiftAmount)) or (operand[position].toULong() shl shiftAmount) or ((operand[position + 1].toULong() shl (32 + shiftAmount)) and highMask)
                }
                requiredLength - 1 -> {
                    if (position < operand.size) {
                        result[i] =
                            (operand[position - 1].toULong() shr (32 - shiftAmount))  or (operand[position].toULong() shl shiftAmount)
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
        TODO("not implemented yet")
    }

    override fun parseBase(number: String, base: Int) {
        TODO("not implemented yet")
    }

    override fun toString(operand: ULongArray, base: Int): String {
        var copy = operand.copyOf()
        val baseArray = ulongArrayOf(base.toULong())
        val stringBuilder = StringBuilder()
        while (copy != ulongArrayOf(0U)) {
            stringBuilder.append(divide(copy, baseArray).second[0].toString(base))
        }
        return stringBuilder.toString().reversed()
    }


    internal infix fun ULongArray.shl(places: Int): ULongArray {
        return shiftLeft(this, places)
    }

    internal infix fun ULongArray.shr(places: Int): ULongArray {
        return shiftRight(this, places)
    }


    internal operator fun ULongArray.plus(other: ULongArray): ULongArray {
        return addition(this, other)
    }

    internal operator fun ULongArray.minus(other: ULongArray): ULongArray {
        return substract(this, other)
    }

    internal operator fun ULongArray.times(other: ULongArray): ULongArray {
        return multiply(this, other)
    }

    internal operator fun ULongArray.plus(other: ULong): ULongArray {
        return addition(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.minus(other: ULong): ULongArray {
        return substract(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.times(other: ULong): ULongArray {
        return multiply(this, ulongArrayOf(other))
    }

    internal operator fun ULongArray.div(other: ULong): ULongArray {
        return basicDivide(this, ulongArrayOf(other)).first
    }

    internal operator fun ULongArray.rem(other: ULong): ULongArray {
        return basicDivide(this, ulongArrayOf(other)).second
    }

    internal operator fun ULongArray.div(other: ULongArray): ULongArray {
        return basicDivide(this, other).first
    }

    internal operator fun ULongArray.rem(other: ULongArray): ULongArray {
        return basicDivide(this, other).second
    }

    internal infix fun ULongArray.divrem(other: ULongArray): Pair<ULongArray, ULongArray> {
        return basicDivide(this, other)
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


}