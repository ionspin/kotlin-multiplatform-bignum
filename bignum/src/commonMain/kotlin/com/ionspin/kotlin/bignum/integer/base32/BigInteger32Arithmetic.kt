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

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigIntegerArithmetic
import com.ionspin.kotlin.bignum.integer.Quadruple
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.toDigit
import kotlin.experimental.and
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
internal object BigInteger32Arithmetic : BigIntegerArithmetic<UIntArray, UInt> {
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

    const val karatsubaThreshold = 3


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
        if (value.isEmpty()) {
            return 0
        }
        val mostSignificant = value[value.size - 1]
        return bitLength(mostSignificant) + (value.size - 1) * basePowerOfTwo

    }

    fun bitLength(value: UInt): Int {
        return basePowerOfTwo - numberOfLeadingZeroes(
            value
        )
    }

    override fun trailingZeroBits(value: UIntArray): Int {
        TODO()
    }

    fun removeLeadingZeroes(bigInteger: UIntArray): UIntArray {
        val firstEmpty = bigInteger.indexOfLast { it != 0U } + 1
        if (firstEmpty == -1 || firstEmpty == 0) {
            return ZERO
        }
        return bigInteger.copyOfRange(0, firstEmpty)

    }

    override fun shiftLeft(operand: UIntArray, places: Int): UIntArray {
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
        return removeLeadingZeroes(result)

    }

    fun normalize(dividend: UIntArray, divisor: UIntArray): Triple<UIntArray, UIntArray, Int> {
        val divisorSize = divisor.size
        val normalizationShift =
            numberOfLeadingZeroes(divisor[divisorSize - 1])
        val divisorNormalized = divisor.shl(normalizationShift)
        val dividendNormalized = dividend.shl(normalizationShift)

        return Triple(dividendNormalized, divisorNormalized, normalizationShift)

    }

    fun normalize(operand: UIntArray): Pair<UIntArray, Int> {
        val normalizationShift =
            numberOfLeadingZeroes(operand[operand.size - 1])
        return Pair(operand.shl(normalizationShift), normalizationShift)
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

    override fun substract(first: UIntArray, second: UIntArray): UIntArray {
        val firstIsLarger = compare(first, second) == 1

        val (largerLength, smallerLength, largerData, smallerData) = if (firstIsLarger) {
            Quadruple(first.size, second.size, first, second)
        } else {
            Quadruple(second.size, first.size, second, first)
        }
        val result = UIntArray(largerLength + 1) { 0u }
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

        return removeLeadingZeroes(result)
    }

    fun karatsubaMultiply(first: UIntArray, second: UIntArray): UIntArray {


        val halfLength = (kotlin.math.max(first.size, second.size) + 1) / 2


        val mask = (ONE shl (halfLength * wordSizeInBits)) - 1U
        val firstLower = and(first, mask)
        val firstHigher = first shr halfLength * wordSizeInBits
        val secondLower = and(second, mask)
        val secondHigher = second shr halfLength * wordSizeInBits

        //
        val higherProduct = firstHigher * secondHigher
        val lowerProduct = firstLower * secondLower
        val middleProduct = (firstHigher + firstLower) * (secondHigher + secondLower)
        val result =
            (higherProduct shl (2 * wordSizeInBits * halfLength)) + ((middleProduct - higherProduct - lowerProduct) shl (wordSizeInBits * halfLength)) + lowerProduct

        return result

    }

    override fun multiply(first: UIntArray, second: UIntArray): UIntArray {
        if (first == ZERO || second == ZERO) {
            return ZERO
        }
        //Need to debug 32 bit variant, seems to fail on lower product
//        if (first.size >= karatsubaThreshold || second.size == karatsubaThreshold) {
//            return karatsubaMultiply(first, second)
//        }

        return removeLeadingZeroes(
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
        return (0 until exponent).fold(ONE) { acc, _ ->
            acc * base
        }
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
                removeLeadingZeroes(
                    uintArrayOf(
                        unnormalizedDividend[0] / unnormalizedDivisor[0]
                    )
                ),
                removeLeadingZeroes(
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
        return Pair(removeLeadingZeroes(quotient), denormRemainder)
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
        return Pair(removeLeadingZeroes(q), denormRemainder)
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
                1UL shl (requiredPrecision) //We are sure that precision is less or equal to 63, so inside int range
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
        //TODO Proper rounding
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
        return naiveGcd(first, second)
    }

    private fun naiveGcd(first: UIntArray, second: UIntArray): UIntArray {
        var u = first
        var v = second
        while (v != ZERO) {
            val tmpU = u
            u = v
            v = tmpU % v
        }
        return u
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
        return removeLeadingZeroes(
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
        return removeLeadingZeroes(
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
        return removeLeadingZeroes(
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
        return removeLeadingZeroes(
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
        return substract(this, other)
    }

    internal operator fun UIntArray.times(other: UIntArray): UIntArray {
        return multiply(this, other)
    }

    internal operator fun UIntArray.plus(other: UInt): UIntArray {
        return add(this, uintArrayOf(other))
    }

    internal operator fun UIntArray.minus(other: UInt): UIntArray {
        return substract(this, uintArrayOf(other))
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
                Pair(removeLeadingZeroes(corrected), resolvedSign)
            }
            Sign.NEGATIVE -> {
                val collected = chunked.flatMap { chunk ->
                    val result = chunk.reversed().foldIndexed(0U) { index, acc, byte ->
                        acc + (byte.toUInt() shl ((chunk.size - 1) * 8 - index * 8))
                    }
                    uintArrayOf(result)
                }.toUIntArray()
                val substracted = collected - 1U
                val inverted = substracted.map { it.inv() }.toUIntArray()
                if (collected.contentEquals(ZERO)) {
                    return Pair(ZERO, Sign.ZERO)
                }

                Pair(removeLeadingZeroes(inverted), resolvedSign)
            }
            Sign.ZERO -> throw RuntimeException("Bug in fromByteArray, sign shouldn't ever be zero at this point.")
        }

    }

    private fun List<Byte>.dropLeadingZeroes(): List<Byte> {
        return this.dropWhile { it == 0.toByte() }
    }

    private fun Array<Byte>.dropLeadingZeroes(): Array<Byte> {
        return this.dropWhile { it == 0.toByte() }.toTypedArray()
    }
}