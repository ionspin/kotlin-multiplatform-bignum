// /*
//  *    Copyright 2019 Ugljesa Jovanovic
//  *
//  *    Licensed under the Apache License, Version 2.0 (the "License");
//  *    you may not use this file except in compliance with the License.
//  *    You may obtain a copy of the License at
//  *
//  *        http://www.apache.org/licenses/LICENSE-2.0
//  *
//  *    Unless required by applicable law or agreed to in writing, software
//  *    distributed under the License is distributed on an "AS IS" BASIS,
//  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  *    See the License for the specific language governing permissions and
//  *    limitations under the License.
//  *
//  */
//
// package com.ionspin.kotlin.bignum.integer.base32
//
// import com.ionspin.kotlin.bignum.integer.Sign
// import com.ionspin.kotlin.bignum.integer.base63.toJavaBigInteger
// import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
// import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
// import com.ionspin.kotlin.bignum.integer.util.toTwosComplementByteArray
// import kotlinx.coroutines.GlobalScope
// import kotlinx.coroutines.Job
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.runBlocking
// import org.junit.Test
// import java.math.BigInteger
// import kotlin.random.Random
// import kotlin.random.nextUInt
// import kotlin.test.assertTrue
//
// /**
//  * Created by Ugljesa Jovanovic
//  * ugljesa.jovanovic@ionspin.com
//  * on 28-Jul-2019
//  */
//
// typealias IonSpinBigInteger = com.ionspin.kotlin.bignum.integer.BigInteger
//
// class ByteArrayToAndFromTest {
//
//     @Test
//     fun testToByteArray() {
//
//         assertTrue {
//             val bigInt = IonSpinBigInteger.fromInt(170)
//             val javaBigInt = bigInt.toJavaBigInteger()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val intArray = bigInt.toTwosComplementByteArray()
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros())
//         }
//
//         assertTrue {
//             val bigInt32Array = BigInteger32Arithmetic.fromInt(170)
//             val javaBigInt = bigInt32Array.toJavaBigInteger().negate()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val intArray = BigInteger32Arithmetic.toByteArray(bigInt32Array)
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros())
//         }
//
//         assertTrue {
//
//             val value = 300L * Int.MAX_VALUE
//             val uIntArray = uintArrayOf(((value.toULong() and 0xFFFFFFFF000000U) shr 32).toUInt(), value.toUInt())
//
//             val bigInt32Array = uIntArray
//             val javaBigInt = uIntArray.toJavaBigInteger()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val intArray = BigInteger32Arithmetic.toByteArray(bigInt32Array)
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros())
//         }
//
//         assertTrue {
//
//             val value = 300L * Int.MAX_VALUE
//             val uIntArray = uintArrayOf(((value.toULong() and 0xFFFFFFFF000000U) shr 32).toUInt(), value.toUInt())
//
//             val bigInt32Array = uIntArray
//             val javaBigInt = uIntArray.toJavaBigInteger().negate()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val intArray = BigInteger32Arithmetic.toByteArray(bigInt32Array)
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros())
//         }
//
//         assertTrue {
//             val value = Long.MAX_VALUE
//             val bigInt32Array = BigInteger32Arithmetic.fromLong(value)
//             val javaBigInt = bigInt32Array.toJavaBigInteger()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val intArray = BigInteger32Arithmetic.toByteArray(bigInt32Array)
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros())
//         }
//     }
//
//
//
//     @Test
//     fun testFromByteArray() {
//         assertTrue {
//             val bigInt32Array = BigInteger32Arithmetic.fromInt(170)
//             val javaBigInt = BigInteger.valueOf(170)
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val reconstructedBigInt32ArrayAndSign =
//                 BigInteger32Arithmetic.fromByteArray(javaBigIntByteArray)
//
//             BigInteger32Arithmetic.compare(
//                 bigInt32Array,
//                 reconstructedBigInt32ArrayAndSign.first
//             ) == 0 && reconstructedBigInt32ArrayAndSign.second == Sign.POSITIVE
//         }
//
//         assertTrue {
//             val bigInt32Array = BigInteger32Arithmetic.fromInt(-170)
//             val javaBigInt = BigInteger.valueOf(-170)
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val reconstructedBigInt32ArrayAndSign =
//                 BigInteger32Arithmetic.fromByteArray(javaBigIntByteArray)
//
//             BigInteger32Arithmetic.compare(
//                 bigInt32Array,
//                 reconstructedBigInt32ArrayAndSign.first
//             ) == 0 && reconstructedBigInt32ArrayAndSign.second == Sign.NEGATIVE
//         }
//
//         assertTrue {
//             val value = 300L * Int.MAX_VALUE
//             val bigInt32Array = BigInteger32Arithmetic.fromLong(value)
//             val javaBigInt = bigInt32Array.toJavaBigInteger()
//             val javaBigIntByteArray = javaBigInt.toByteArray()
//             val reconstructedBigInt32ArrayAndSign =
//                 BigInteger32Arithmetic.fromByteArray(javaBigIntByteArray)
//
//             BigInteger32Arithmetic.compare(
//                 bigInt32Array,
//                 reconstructedBigInt32ArrayAndSign.first
//             ) == 0 && reconstructedBigInt32ArrayAndSign.second == Sign.POSITIVE
//         }
//     }
//
//     @Test
//     fun testManyFromByteArrays() {
//         val seed = 1
//         val random = Random(seed)
//
//         val jobList: MutableList<Job> = mutableListOf()
//         for (i in 0..10000 step 1) {
//             val job = GlobalScope.launch {
//                 val number = UIntArray(300) {
//                     random.nextUInt()
//                 }
//                 testSingleFromByteArray(number)
//             }
//             jobList.add(job)
//         }
//         runBlocking {
//             jobList.forEach {
//                if (it.isCancelled) {
//                    fail("Some of the tests failed")
//                }
//                it.join()
//            }
//         }
//     }
//
//     @Test
//     fun testManyPositiveToByteArrays() {
//         val seed = 1
//         val random = Random(seed)
//
//         val jobList: MutableList<Job> = mutableListOf()
//         for (i in 0..10000 step 1) {
//             val job = GlobalScope.launch {
//                 val numberOfInts = random.nextInt(300)
//                 val number = UIntArray(numberOfInts) {
//                     random.nextUInt()
//                 }
//                 testSingleToByteArray(number, Sign.POSITIVE)
//                 testSingleToByteArray(number, Sign.NEGATIVE)
//             }
//             jobList.add(job)
//         }
//         runBlocking {
//             jobList.forEach {
//                if (it.isCancelled) {
//                    fail("Some of the tests failed")
//                }
//                it.join()
//            }
//         }
//     }
//
//     @Test
//     fun testSpecificNumberToByteArray() {
// //        val number = uintArrayOf(
// //            3713029474U, 3627127149U, 1401971595U, 4235008121U, 3172413113U, 1983025212U, 3513944539U, 3585820969U,
// //            2458755354U, 394648975U, 625371470U, 1575471769U, 2100649587U, 3070076606U, 1984303761U, 2440113265U,
// //            2722398484U, 2418874620U, 2718310639U, 2153548368U, 2234909028U, 365768924U, 643745162U, 2807075107U,
// //            3001771099U, 2244833404U, 3132319387U, 757625671U, 4280429524U, 1119345431U, 99934901U, 3029420709U,
// //            3395394702U, 3445416033U, 3896809567U, 2914897175U, 3176210056U, 3359687212U, 240379348U, 1426389U)
//         val number = uintArrayOf(
//             2696295277U,
//             672340963U,
//             3539997803U,
//             4121786825U,
//             642721024U,
//             2138920079U,
//             3748217460U,
//             2167624736U,
//             1825374906U,
//             2423449999U
//         )
//
//         testSingleToByteArray(number, Sign.NEGATIVE)
//     }
//
//     @Test
//     fun testSpecificNumberFromByteArray() {
// //        val number = uintArrayOf(
// //            1382576883U,
// //            3185699695U,
// //            3990229257U,
// //            4214840377U,
// //            1857151281U,
// //            1056377018U,
// //            1097481378U,
// //            2989689242U,
// //            528133458U,
// //            1391926091U
// //        )
//
// //        val number = uintArrayOf(1477527302U, 1433375211U, 2299565196U, 157764436U, 1462860600U, 2909983771U, 4279152970U, 2795549498U, 2237905360U, 1446205346U)
//         val number = uintArrayOf(
//             506240625U,
//             2361259230U,
//             631867953U,
//             304652636U,
//             3096086296U,
//             3786664971U,
//             3328419072U,
//             2592013419U,
//             4032177177U,
//             2421456398U
//         )
//
//         testSingleFromByteArray(number)
//     }
//
//     fun testSingleFromByteArray(bigInt32Array: UIntArray) {
//         val javaBigInt = bigInt32Array.toJavaBigInteger()
//         val javaBigIntByteArray = javaBigInt.toByteArray()
//         val reconstructedBigInt32ArrayAndSign =
//             BigInteger32Arithmetic.fromByteArray(javaBigIntByteArray)
//         val signInt = (javaBigIntByteArray[0].toInt() ushr 7) and 0b00000001
//         val sign = when (signInt) {
//             0 -> Sign.POSITIVE
//             1 -> Sign.NEGATIVE
//             else -> throw RuntimeException("Invalid sign")
//         }
//         assertTrue("Failed on FROM byte array: \n val number = uintArrayOf(${bigInt32Array.joinToString(separator = ", ") { "${it}U" }})\n") {
//             BigInteger32Arithmetic.compare(
//                 bigInt32Array,
//                 reconstructedBigInt32ArrayAndSign.first
//             ) == 0 && reconstructedBigInt32ArrayAndSign.second == sign
//         }
//     }
//
//     fun testSingleToByteArray(bigInt32Array: UIntArray, sign: Sign) {
//         val javaBigInt = if (sign == Sign.NEGATIVE) {
//             bigInt32Array.toJavaBigInteger().negate()
//         } else {
//             bigInt32Array.toJavaBigInteger()
//         }
//         val javaBigIntByteArray = javaBigInt.toByteArray()
//         val intArray = BigInteger32Arithmetic.toByteArray(bigInt32Array)
//         assertTrue("Failed on TO byte array: \n val number = uintArrayOf(${bigInt32Array.joinToString(separator = ", ") { "${it}U" }})\n") {
//             javaBigIntByteArray.dropLeadingZeros().contentEquals(intArray.dropLeadingZeros()) ||
//                 (byteArrayOf(0xFF.toByte()) + javaBigIntByteArray.dropLeadingZeros()).contentEquals(intArray.dropLeadingZeros())
//         }
//     }
//
//     @Test
//     fun testFromUByteArray() {
//         assertTrue {
//             val uByteArray = "FF01ABCD01FF".chunked(2).map { it.toUByte(16) }.toUByteArray()
//             val bigInt = BigInteger32Arithmetic.fromUByteArray(uByteArray)
//             bigInt.first.toJavaBigInteger() == 0xFF01ABCD01FF.toBigInteger()
//         }
//     }
//
//     @Test
//     fun testToUByteArray() {
//         assertTrue {
//             val uByteArray = "FF01ABCD01FF".chunked(2).map { it.toUByte(16) }.toUByteArray()
//             val bigInt = BigInteger32Arithmetic.fromUByteArray(uByteArray)
//             val reconstructed = BigInteger32Arithmetic.toUByteArray(bigInt.first)
//             uByteArray.contentEquals(reconstructed)
//         }
//
//         assertTrue {
//             val uByteArray = "19191919191919191919191919191919".chunked(2).map { it.toUByte(16) }.toUByteArray()
//             val bigInt = BigInteger32Arithmetic.fromUByteArray(uByteArray)
//             val reconstructed = BigInteger32Arithmetic.toUByteArray(bigInt.first)
//             uByteArray.contentEquals(reconstructed)
//         }
//     }
//
//     private fun Array<Byte>.dropLeadingZeros(): Array<Byte> {
//         return this.dropWhile { it == 0.toByte() }.toTypedArray()
//     }
//
//     private fun ByteArray.dropLeadingZeros(): ByteArray {
//         return this.dropWhile { it == 0.toByte() }.toByteArray()
//     }
// }
