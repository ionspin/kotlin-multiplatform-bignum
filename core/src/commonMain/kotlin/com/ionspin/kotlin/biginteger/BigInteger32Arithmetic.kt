package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-3/9/19
 */
@ExperimentalUnsignedTypes
internal object BigInteger32Arithmetic : BigIntegerArithmetic<UIntArray, UInt> {
    val mask = 0xFFFFFFFFUL
    val overflowMask = 0x100000000U
    val lowerMask = 0xFFFFUL
    override val base: UInt = 0xFFFFFFFFU
    override val basePowerOfTwo = 32

    override val ZERO = UIntArray(0)
    override val ONE = UIntArray(1) { 1U }

    /**
     * Hackers delight 5-11
     */
    override fun numberOfLeadingZeroes(value: UInt): Int {
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

    override fun bitLength(value: UIntArray): Int {
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size) * basePowerOfTwo

    }

    override fun bitLength(value: UInt): Int {
        return basePowerOfTwo - numberOfLeadingZeroes(value)
    }

    fun removeLeadingZeroes(bigInteger: UIntArray): UIntArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0U } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            //Array is equal to zero, so we return array with zero elements
            return ZERO
        }
        return bigInteger.copyOfRange(0, firstEmpty)

    }

    override fun shiftLeft(operand: UIntArray, places: Int): UIntArray {
        if (operand.isEmpty() || places == 0) {
            return operand
        }
        val originalSize = operand.size
        val leadingZeroes = numberOfLeadingZeroes(operand[operand.size - 1])
        val shiftWords = places / basePowerOfTwo
        val shiftBits = places % basePowerOfTwo
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
        var transfer: UInt = 0U

        val leadingZeroes = numberOfLeadingZeroes(operand[operand.size - 1])
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

        return UIntArray(operand.size - wordsToDiscard) {
            when (it) {
                in 0..(operand.size - 2 - wordsToDiscard) -> {
                    (operand[it + wordsToDiscard] shr shiftBits) or (operand[it + wordsToDiscard + 1] shl (basePowerOfTwo - shiftBits))
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

    fun normalize(dividend: UIntArray, divisor: UIntArray): Triple<UIntArray, UIntArray, Int> {
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val normalizationShift = numberOfLeadingZeroes(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)

    }

    fun normalize(operand : UIntArray) : Pair<UIntArray, Int> {
        val normalizationShift = numberOfLeadingZeroes(operand[operand.size - 1])
        return Pair(operand.shl(normalizationShift) , normalizationShift)
    }

    fun denormalize(
        remainderNormalized: UIntArray,
        normalizationShift: Int
    ): UIntArray {
        val remainder = remainderNormalized shr normalizationShift
        return remainder
    }

    //---------------- Primitive operations -----------------------//

    override fun compare(first: UIntArray, second: UIntArray): Int {


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

    override fun addition(first: UIntArray, second: UIntArray): UIntArray {
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
            sum = sum shr basePowerOfTwo
        }

    }

    override fun substract(first: UIntArray, second: UIntArray): UIntArray {
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
            diff = (diff and overflowMask) shr basePowerOfTwo
            i++
        }

        while (diff != 0UL) {
            diff = largerData[i].toULong() - diff
            if ((diff and overflowMask) shr basePowerOfTwo == 1UL) {
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
            return ZERO
        }
        //Remove zero words
        val firstEmpty = result.indexOfLast { it != 0U } + 1

        return result.copyOfRange(0, firstEmpty)
    }

    fun multiply(first: UInt, second: UInt): UIntArray {
        val result = first * second
        val high = (result shr basePowerOfTwo).toUInt()
        val low = result.toUInt()

        return removeLeadingZeroes(uintArrayOf(low, high))
    }

    override fun multiply(first: UIntArray, second: UInt): UIntArray {

        val result = UIntArray(first.size + 1)

        var product = 0UL
        var sum = 0UL
        for (i in 0 until first.size) {
            product = first[i].toULong() * second
            sum = result[i].toULong() + (product and mask).toUInt()
            result[i] = (sum and mask).toUInt()
            sum = sum shr basePowerOfTwo
            result[i + 1] = (product shr basePowerOfTwo).toUInt() + sum.toUInt()
        }

        return removeLeadingZeroes(result)
    }

    override fun multiply(first: UIntArray, second: UIntArray): UIntArray {
        return second.foldIndexed(ZERO) { index, acc, element ->
            acc + (multiply(first, element) shl (index * basePowerOfTwo))

        }

    }

    override fun divide(first: UIntArray, second: UIntArray) : Pair<UIntArray, UIntArray> {
        return basicDivide(first, second)
    }

    override fun divide(first: UIntArray, second: UInt) : Pair<UIntArray, UIntArray> {
        return basicDivide(first, uintArrayOf(second))
    }

    /**
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
            return Pair(uintArrayOf(0U), unnormalizedDividend)
        }
        if (unnormalizedDivisor.size == 1 && unnormalizedDividend.size == 1) {
            return Pair(uintArrayOf(unnormalizedDividend[0] / unnormalizedDivisor[0]),
                uintArrayOf(unnormalizedDividend[0] % unnormalizedDivisor[0]))
        }
        val bitPrecision = bitLength(unnormalizedDividend) - bitLength(unnormalizedDivisor)
        if (bitPrecision == 0) {
            return Pair(uintArrayOf(1U), unnormalizedDividend - unnormalizedDivisor)
        }


        var (dividend, divisor, normalizationShift) = normalize(unnormalizedDividend, unnormalizedDivisor)
        val dividendSize = dividend.size
        val divisorSize = divisor.size
        val wordPrecision = dividendSize - divisorSize



        var qjhat = 0UL
        var reconstructedQuotient: UIntArray
        var quotient = UIntArray(wordPrecision)

        val divisorTimesBaseToPowerOfM = (divisor shl (wordPrecision * basePowerOfTwo))
        if (dividend >= divisorTimesBaseToPowerOfM) {
            quotient = UIntArray(wordPrecision + 1)
            quotient[wordPrecision] = 1U
            dividend = dividend - divisorTimesBaseToPowerOfM
        }

        for (j in (wordPrecision - 1) downTo 0) {
            qjhat =
                ((dividend[divisorSize + j].toULong() shl basePowerOfTwo) + dividend[divisorSize + j - 1]) / divisor[divisorSize - 1]
            quotient[j] = if (qjhat < (base - 1UL)) qjhat.toUInt() else base - 1U
            // We don't have signed integers here so we need to check if reconstructed quotient is larger than the dividend
            // instead of just doing  A ← A − qj β B and then looping. Final effect is the same.
            reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            while (reconstructedQuotient > dividend) {
                quotient[j] -= 1U
                reconstructedQuotient = ((divisor * quotient[j]) shl (j * basePowerOfTwo))
            }
            dividend = dividend - reconstructedQuotient
        }

        val denormRemainder = denormalize(dividend, normalizationShift)
        return Pair(quotient, denormRemainder)
    }

    fun baseReciprocal(unnomrmalizedOperand : UIntArray, precision : Int) : UIntArray {
        val (operand, normalizationShift) = normalize(unnomrmalizedOperand)
        val operandSize = operand.size
        if (operandSize <= 2) {
            return (((uintArrayOf(1U) shl (2 * basePowerOfTwo)) / operand) - 1U)
        }

        TODO()


    }

    internal infix fun UIntArray.shl(places: Int): UIntArray {
        return shiftLeft(this, places)
    }

    internal infix fun UIntArray.shr(places: Int): UIntArray {
        return shiftRight(this, places)
    }


    internal operator fun UIntArray.plus(other: UIntArray): UIntArray {
        return addition(this, other)
    }

    internal operator fun UIntArray.minus(other: UIntArray): UIntArray {
        return substract(this, other)
    }

    internal operator fun UIntArray.times(other: UIntArray): UIntArray {
        return multiply(this, other)
    }

    internal operator fun UIntArray.plus(other: UInt): UIntArray {
        return addition(this, uintArrayOf(other))
    }

    internal operator fun UIntArray.minus(other: UInt): UIntArray {
        return substract(this, uintArrayOf(other))
    }

    internal operator fun UIntArray.times(other: UInt): UIntArray {
        return multiply(this, other)
    }

    internal operator fun UIntArray.div(other: UInt): UIntArray {
        return basicDivide(this, uintArrayOf(other)).first
    }

    internal operator fun UIntArray.rem(other: UInt): UIntArray {
        return basicDivide(this, uintArrayOf(other)).second
    }

    internal operator fun UIntArray.div(other: UIntArray): UIntArray {
        return basicDivide(this, other).first
    }

    internal operator fun UIntArray.rem(other: UIntArray): UIntArray {
        return basicDivide(this, other).second
    }

    internal infix fun UIntArray.divrem(other: UIntArray): Pair<UIntArray, UIntArray> {
        return basicDivide(this, other)
    }

    internal operator fun UIntArray.compareTo(other: UIntArray): Int {
        return compare(this, other)
    }

    internal operator fun UIntArray.compareTo(other: UInt): Int {
        return compare(this, uintArrayOf(other))
    }


}