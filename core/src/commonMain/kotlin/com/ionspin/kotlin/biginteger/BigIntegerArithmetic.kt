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

package com.ionspin.kotlin.biginteger

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-Mar-3/10/19
 */
interface BigIntegerArithmetic<BackingCollectionType, BackingWordType> {
    val ZERO : BackingCollectionType
    val ONE : BackingCollectionType
    val basePowerOfTwo: Int
    /**
     * Hackers delight 5-11
     */
    fun numberOfLeadingZeroes(value: BackingWordType): Int
    fun bitLength(value: BackingCollectionType): Int
    fun shiftLeft(operand: BackingCollectionType, places: Int): BackingCollectionType
    fun shiftRight(operand: BackingCollectionType, places: Int): BackingCollectionType
    fun compare(first: BackingCollectionType, second: BackingCollectionType): Int
    fun add(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType
    fun substract(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType
    fun multiply(first: BackingCollectionType, second: BackingCollectionType): BackingCollectionType
    fun divide(first: BackingCollectionType, second: BackingCollectionType): Pair<BackingCollectionType, BackingCollectionType>
    fun parseForBase(number : String, base : Int) : BackingCollectionType
    fun toString(operand: BackingCollectionType, base : Int) : String


}

