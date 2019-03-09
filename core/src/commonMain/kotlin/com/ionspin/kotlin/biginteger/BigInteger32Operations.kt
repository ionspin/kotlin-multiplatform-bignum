package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
internal object BigInteger32Operations {
    val mask = 0xFFFFFFFFUL
    val overflowMask = 0x100000000U
    val lowerMask = 0xFFFFUL
    val base: UInt = 0xFFFFFFFFU

    /**
     * Hackers delight 5-11
     */
    fun numberOfLeadingZeroes(value: UInt): Int {
        var x = value
        var y: UInt
        var n = 32

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

    fun bitLength(value: UIntArray): Int {
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size) * 32

    }

    fun bitLength(value: UInt): Int {
        return 32 - numberOfLeadingZeroes(value)
    }

    fun removeLeadingZeroes(bigInteger: UIntArray): UIntArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0U } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            //If the big integer was 0, return just an array with one element equal to zero
            return bigInteger.copyOfRange(0, 1)
        }
        return bigInteger.copyOfRange(0, firstEmpty)

    }

    fun removeLeadingZeroesInt(bigInteger: UIntArray): UIntArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0U } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            //If the big integer was 0, return just an array with one element equal to zero
            return bigInteger.copyOfRange(0, 1)
        }
        return bigInteger.copyOfRange(0, firstEmpty)

    }

    fun shiftLeft(operand: UIntArray, places: Int): UIntArray {
        if (operand.size == 0 || places == 0) {
            return operand
        }
        val originalSize = operand.size
        val leadingZeroes = numberOfLeadingZeroes(operand[operand.size - 1])
        val shiftWords = places / 32
        val shiftBits = places % 32
        val wordsNeeded = if (shiftBits > leadingZeroes) {
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
                    (operand[it - shiftWords] shl shiftBits) or (operand[it - shiftWords - 1] shr (32 - shiftBits))
                }
                originalSize + wordsNeeded - 1 -> {
                    (operand[it - wordsNeeded] shr (32 - shiftBits))
                }
                else -> { throw RuntimeException("Invalid case $it")}

            }
        }

    }

    fun shiftRight(operand: UIntArray, places: Int): UIntArray {
        var transfer: UInt = 0U

        val leadingZeroes = numberOfLeadingZeroes(operand[operand.size - 1])
        val shiftWords = places / 32
        val shiftBits = (places % 32)
        val wordsToDiscard = if (shiftBits >= (32 - leadingZeroes)) {
            shiftWords + 1
        } else {
            shiftWords
        }
        if (wordsToDiscard >= operand.size) {
            return UIntArray(0)
        }

        if (shiftBits == 0) {
            operand.copyOfRange(operand.size - wordsToDiscard, operand.size)
        }

        return UIntArray(operand.size - wordsToDiscard) {
            when (it) {
                in 0..(operand.size - 2 - wordsToDiscard) -> {
                    (operand[it + wordsToDiscard] shr shiftBits) or (operand[it + wordsToDiscard + 1] shl (32 - shiftBits))
                }
                operand.size - 1 - wordsToDiscard -> {
                    (operand[it + wordsToDiscard] shr shiftBits)
                }
                else -> { throw RuntimeException("Invalid case $it")}
            }
        }

    }

    fun normalize(dividend: UIntArray, divisor: UIntArray): Triple<UIntArray, UIntArray, Int> {
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val normalizationShift = numberOfLeadingZeroes(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)

    }

    fun denormalize(
        quotientNormalized: UIntArray,
        remainderNormalized: UIntArray,
        normalizationShift: Int
    ): Pair<UIntArray, UIntArray> {
        val quotient = quotientNormalized shr normalizationShift
        val remainder = remainderNormalized shr normalizationShift
        return Pair(quotient, remainder)
    }

    //---------------- Primitive operations -----------------------//

    fun compare(first: UIntArray, second: UIntArray): Int {


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

    fun addition(first: UIntArray, second: UIntArray): UIntArray {
        if (first.size == 1 && first[0] == 0U) return second
        if (second.size == 1 && second[0] == 0U) return first

        val (maxLength, minLength, largerData, smallerData) = if (first.size > second.size) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }


        val result = UIntArray(maxLength + 1) { index -> 0u }
        var i = 0
        var sum: ULong = 0u
        while (i < minLength) {
            sum = sum + largerData[i] + smallerData[i]
            result[i] = (sum and mask).toUInt()
            sum = sum shr 32
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
            sum = sum shr 32
        }

    }

    fun substract(first: UIntArray, second: UIntArray): UIntArray {
        val firstIsLarger = compare(first, second) == 1

        val (largerLength, smallerLength, largerData, smallerData) = if (firstIsLarger) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }
        val result = UIntArray(largerLength + 1) { index -> 0u }
        var i = 0
        var diff: ULong = 0u
        while (i < smallerLength) {
            diff = largerData[i].toULong() - smallerData[i] - diff
            result[i] = diff.toUInt()
            diff = (diff and overflowMask) shr 32
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i].toULong() - diff
            if ((diff and overflowMask) shr 32 == 1UL) {
                result[i] = (diff - 1UL).toUInt()
            } else {
                result[i] = diff.toUInt()
                diff = 0UL
            }
            diff = diff shr 63
            i++
        }

        while (i < largerLength) {
            result[i] = largerData[i]
            i++
        }

        if (result.filter { it == 0U }.isEmpty()) {
            return UIntArray(0)
        }
        //Remove zero words
        val firstEmpty = result.indexOfLast { it != 0U } + 1

        return result.copyOfRange(0, firstEmpty)
    }

    fun multiply(first: UInt, second: UInt): UIntArray {
        val result = first * second
        val high = (result shr 32).toUInt()
        val low = result.toUInt()

        return removeLeadingZeroes(uintArrayOf(low, high))
    }

    fun multiply(first: UIntArray, second: UInt): UIntArray {

        val result = UIntArray(first.size + 1)

        var product = 0UL
        var sum = 0UL
        for (i in 0 until first.size) {
            product = first[i].toULong() * second
            sum = result[i].toULong() + (product and mask).toUInt()
            result[i] = (sum and mask).toUInt()
            sum = sum shr 32
            result[i + 1] = (product shr 32).toUInt() + sum.toUInt()
        }

        return removeLeadingZeroes(result)
    }

    fun multiply(first: UIntArray, second: UIntArray): UIntArray {
        return second.foldIndexed(UIntArray(0)) { index, acc, element ->
            acc + (multiply(first, element) shl (index * 32))

        }

    }

    /**
     * Based on algorithm from MCA
     */
    fun basicDivide(
        unnormalizedDividend: UIntArray,
        unnormalizedDivisor: UIntArray
    ): Pair<UIntArray, UIntArray> {
        var (dividend, divisor, normalizationShift) = normalize(unnormalizedDividend, unnormalizedDivisor)
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val wordPrecision = dividendSize - divisorSize
        val quotient = UIntArray(wordPrecision)
        quotient[wordPrecision - 1] = 0U
        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision + 32))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient[wordPrecision - 1] = 1U
            dividend = dividend - (divisor shl (wordPrecision + 32))
        }

        for (j in (wordPrecision - 1) downTo 0) {
            val qjhat = (dividend[divisorSize + j].toULong() * base + dividend[divisorSize + j - 1]) / divisor[divisorSize - 1]
            quotient[j] = if (qjhat < (base - 1UL)) qjhat.toUInt() else base - 1U
            dividend = dividend - divisor * quotient[j] * (base shr j)
            while (divisor < 0U) {
                quotient[j] = quotient[j] - 1U
                dividend = dividend + divisor * (base shr j)
            }
        }

        val (denormQuotient, denormRemainder) = denormalize(quotient, dividend, normalizationShift)
        return Pair(denormQuotient, denormRemainder)
    }

    private infix fun UIntArray.shl(places: Int): UIntArray {
        return shiftLeft(this, places)
    }

    private infix fun UIntArray.shr(places: Int): UIntArray {
        return shiftLeft(this, places)
    }


    private operator fun UIntArray.plus(other: UIntArray): UIntArray {
        return addition(this, other)
    }

    private operator fun UIntArray.minus(other: UIntArray): UIntArray {
        return substract(this, other)
    }

    private operator fun UIntArray.times(other: UIntArray): UIntArray {
        return multiply(this, other)
    }

    private operator fun UIntArray.plus(other: UInt): UIntArray {
        return addition(this, uintArrayOf(other))
    }

    private operator fun UIntArray.minus(other: UInt): UIntArray {
        return substract(this, uintArrayOf(other))
    }

    private operator fun UIntArray.times(other: UInt): UIntArray {
        return multiply(this, other)
    }
    private operator fun UIntArray.div(other: UInt): UIntArray {
        TODO()
        return basicDivide(this, uintArrayOf(other)).first
    }

    private operator fun UIntArray.compareTo(other: UIntArray): Int {
        return compare(this, other)
    }

    private operator fun UIntArray.compareTo(other: UInt): Int {
        return compare(this, uintArrayOf(other))
    }
}