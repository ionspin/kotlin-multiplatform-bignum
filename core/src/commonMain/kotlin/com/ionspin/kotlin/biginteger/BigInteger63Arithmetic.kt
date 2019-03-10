package com.ionspin.kotlin.biginteger

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
        return bitLength(mostSignificant) + (value.size) * 63
    }

    fun bitLength(value: ULong): Int {
        return 63 - numberOfLeadingZeroes(value)
    }

    override fun shiftLeft(operand: ULongArray, places: Int): ULongArray {
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

        return ULongArray(operand.size - wordsToDiscard) {
            when (it) {
                in 0..(operand.size - 2 - wordsToDiscard) -> {
                    ((operand[it + wordsToDiscard] shr shiftBits) ) or
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
        TODO("not implemented yet")
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

    override fun divide(first: ULongArray, second: ULongArray): Pair<ULongArray, ULongArray> {
        TODO("not implemented yet")
    }


}