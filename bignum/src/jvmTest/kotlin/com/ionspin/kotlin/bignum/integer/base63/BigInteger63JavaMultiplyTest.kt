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

import com.ionspin.kotlin.bignum.integer.base63.array.BigInteger63Arithmetic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Mar-2019
 */
@ExperimentalUnsignedTypes
class BigInteger63JavaMultiplyTest {
    @Test
    fun `Test for sentimental value`() {
        assertTrue {
            val a = ulongArrayOf(10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult
        }

        assertTrue {
            val a = ulongArrayOf(10U, 10U)
            val b = ulongArrayOf(20U, 20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val aBigInt = a.toJavaBigInteger()
            val bBigInt = b.toJavaBigInteger()
            val bigIntResult = aBigInt * bBigInt

            resultBigInt == bigIntResult
        }

        assertTrue {
            val a = ulongArrayOf((0UL - 1UL), 10U)
            val b = ulongArrayOf(20U)
            val c = BigInteger63Arithmetic.multiply(a, b)

            val resultBigInt = c.toJavaBigInteger()

            val bigIntResult = a.toJavaBigInteger() * b.toJavaBigInteger()

            resultBigInt == bigIntResult
        }
    }

    @Test
    fun `Test multiplying three words`() {
        val seed = 1
        val random = Random(seed)
        for (i in 1..Int.MAX_VALUE step 5001) {
            multiplySingleTest(random.nextULong() shr 1, random.nextULong() shr 1, random.nextULong() shr 1)
        }
    }

    @Test
    fun `Multiply two large words`() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 30000
        println("Number of elements $numberOfElements")

        val first = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }

        val second = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }
        val startTime = System.currentTimeMillis()
        multiplySingleTest(first, second)
        val endTime = System.currentTimeMillis()
        println("Time to multiply 2 large words ${endTime - startTime}")
    }

    @Test
    fun `Test multiplying a lot of words`() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 15000
        println("Number of elements $numberOfElements")

        val first = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }

        multiplySingleTest(*first)
    }

    @Test
    fun preciseMultiplyTest() {
        multiplySingleTest(
            ulongArrayOf(3751237528UL, 9223372035661198284UL, 7440555637UL, 0UL, 2UL, 0UL, 2UL),
            ulongArrayOf(1UL, 1UL)
        )
//        multiplySingleTest(1193170172382743678UL, 17005332033106823254UL, 15532449225048523230UL) Invalid sample, 64 bit number!
    }

    fun multiplySingleTest(first: ULongArray, second: ULongArray) {
        assertTrue(
            "Failed on ulongArrayOf(${first.joinToString(separator = ", ") { it.toString() + "UL" }})," +
                " ulongArrayOf(${second.joinToString(separator = ", ") { it.toString() + "UL" }})"
        ) {

            val result = BigInteger63Arithmetic.multiply(first, second)

            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
            bigIntResult == convertedResult
        }
    }

    fun multiplySingleTest(vararg elements: ULong) {
        val elementsArray = elements
        assertTrue("Failed on (${elementsArray.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            val time = elements.size > 100
            lateinit var lastTime: LocalDateTime
            lateinit var startTime: LocalDateTime

            if (time) {
                lastTime = LocalDateTime.now()
                startTime = lastTime
            }

            val result = elements.fold(ULongArray(1) { 1U }) { acc, uLong ->
                BigInteger63Arithmetic.baseMultiply(acc, uLong)
            }
            if (time) {
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
                startTime = lastTime
            }
            val convertedResult = result.toJavaBigInteger()
            val bigIntResult = elements.fold(BigInteger.ONE) { acc, uInt ->
                acc * BigInteger(uInt.toString(), 10)
            }
            if (time) {
                println("Result $convertedResult")
                lastTime = LocalDateTime.now()
                println("Total time ${Duration.between(startTime, lastTime)}")
            }

            bigIntResult == convertedResult
        }
    }

    @Test
    fun mutiplyTwoWordsTest() {
        val seed = 1
        val random = Random(seed)
        val jobList: MutableList<Job> = mutableListOf()
        val ulongLimit = ULong.MAX_VALUE shr 1
        for (i in 1..Int.MAX_VALUE step 3001) {
            val first = random.nextULong(ulongLimit)
            val second = random.nextULong(ulongLimit)
            jobList.add(
                GlobalScope.launch {
                    twoWordsSingleMultiplication(first, second)
                }
            )
        }
        runBlocking {
            jobList.forEach { it.join() }
        }
    }

    @Test
    fun mutiplyTwoWordsSignificantValuesTest() {
        val ulongLimit = ULong.MAX_VALUE shr 1
        val significantValue = arrayOf(
            0U, 1U, 2U, 3U, ulongLimit, ulongLimit - 1U, ulongLimit - 2U, ulongLimit
        )
        significantValue.forEach { first ->
            significantValue.forEach { second ->
                twoWordsSingleMultiplication(first, second)
            }
        }
    }

    fun twoWordsSingleMultiplication(first: ULong, second: ULong) {
        val bigIntResult = BigInteger63Arithmetic.multiply(first, second)
        val javaBigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
        assertTrue {
            bigIntResult.toJavaBigInteger() == javaBigIntResult
        }
    }

    @Test
    fun testKaratsuba() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 1500
        println("Number of elements $numberOfElements")

        val first = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }

        val second = ULongArray(numberOfElements) {
            random.nextULong() shr 1
        }
        karatsubaSingleTest(first, second)
    }

    fun karatsubaSingleTest(first: ULongArray, second: ULongArray) {
        val bigIntResult = BigInteger63Arithmetic.karatsubaMultiply(first, second)
        val javaBigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
        println("------------- ${first.toJavaBigInteger()} * ${second.toJavaBigInteger()}")
        println("JavaBigInt: $javaBigIntResult")
        println("BigInt: ${bigIntResult.toJavaBigInteger()}")
        assertTrue {
            bigIntResult.toJavaBigInteger() == javaBigIntResult
        }
    }

    @Test
    fun testToomCook() {
        val seed = 1
        val random = Random(seed)
        val numberOfElements = 1500

        println("Number of elements $numberOfElements")
        for (i in 1..100) {
            val first = ULongArray(numberOfElements) {
                random.nextULong() shr 1
            }

            val second = ULongArray(numberOfElements) {
                random.nextULong() shr 1
            }
            toomCookSingleTest(first, second)
        }
    }

    @Test
    fun specificToomCook3() {
        val first = ulongArrayOf(7770083104260137692UL, 4422168582474645862UL, 1545459332780423999UL, 2537079926963084929UL, 3337377603670050785UL, 124476092686987278UL, 4132951149092312929UL, 3737427000635587831UL, 4521865081492284133UL, 9217094969610787702UL, 8257311010241047914UL, 140454755781392451UL, 2540851284337141613UL, 1646734824439601557UL, 2547636735459953155UL, 3803526616133592913UL, 543196432850442811UL, 8879701138394289375UL, 7934480367287254243UL, 3791859870088187513UL, 8397203150879013588UL, 2727160675394249516UL, 1256248622432354794UL, 2006580250377253218UL, 9077734488587435735UL, 3153574536592337858UL, 4522356285312239253UL, 5514591275912493798UL, 16653826019285983UL, 472581733525857003UL, 1721845695256102682UL, 2103980876505944850UL, 9149145367778293042UL, 482920949323892968UL, 4922119350629037439UL, 5993542771109352744UL, 8907878326414849200UL, 6504633507882968240UL, 5475876082739569241UL, 8853072596476966975UL, 6841596888290655272UL, 603141635430739969UL, 94036096838532969UL, 6797502412192567500UL, 1968376997633445501UL, 8568215537645814331UL, 1082050971958571547UL, 6306427998183898333UL, 4290695619965781819UL, 9086234702033162572UL, 7449067190430885027UL, 1671228430222989925UL, 7703232582668737221UL, 6659988657415043517UL, 6133332347297789401UL, 6451810366571018695UL, 8870662643713738328UL, 1615497466030459273UL, 2485629786032024503UL, 5523959609558301495UL)
        val second = ulongArrayOf(889271152217344261UL, 8821527082127691491UL, 6007853337661972039UL, 7409901138264402329UL, 8530542844091152364UL, 871979236399606045UL, 7021852196739884331UL, 8926438136151491621UL, 6930836656186932939UL, 1333474863771730621UL, 8783454176798282301UL, 2469673116413578950UL, 2186095544073922936UL, 4028224461826934054UL, 5041762992213269125UL, 2129272323302599291UL, 8732088405932051838UL, 8524936744595626161UL, 4752051884945274938UL, 4778979145791923624UL, 1509761424847564011UL, 6640944828973683528UL, 8665135557987220173UL, 1824126433994244581UL, 9190968623202744931UL, 2271616520993487516UL, 8549254610843690066UL, 3541101407344049574UL, 8664498745467894629UL, 1893255742949687641UL, 2631652446705945343UL, 5941459808938366804UL, 9041838702007529988UL, 7979707548042188757UL, 9198991124504266672UL, 9062467525229140896UL, 9144005348756105583UL, 5260425339767520125UL, 4745417688569481228UL, 3059849972743917787UL, 4746004582620659134UL, 2744977502766718117UL, 4860102926799843155UL, 4590516033442351024UL, 2434067218486693356UL, 6302954027831894638UL, 7269375291851389760UL, 9217999903593215202UL, 8759111025756038781UL, 6359871175675862902UL, 6442717618441285131UL, 3602053356931416220UL, 1210098943561041104UL, 2077426658757904423UL, 6273967592767188485UL, 6971961115330640062UL, 5435050832410872410UL, 1709150746648165450UL, 8324996308728296048UL, 4862602820348794360UL)

        // val first = ulongArrayOf(4150333453060543876UL, 5304700251365718186UL, 7868141709517828722UL, 4106917065147536229UL, 3261427872996154650UL, 4920965608194098138UL, 2673258686032994293UL, 642455498092439229UL, 7381958881451011923UL, 1103075084739190722UL, 8561019039184506218UL, 9178276683904696942UL, 3101570229304501639UL, 5749684408623204920UL, 5465377347754663299UL, 7965880493431814197UL, 3383490865251093453UL, 6333225077737959700UL, 3154882743948610934UL, 5712494969049283746UL, 6264704537981005704UL, 4832155398957875357UL, 4117758894607441076UL, 2931291359124807031UL, 338703434830286764UL, 5267702237004328682UL, 2091456047896028751UL, 2773744009070906252UL, 7386732541798837634UL, 8420380302726647469UL, 2883768759523158340UL, 5901815996129653253UL, 7720396364604894904UL, 1150773591333941320UL, 2793076526128316894UL, 3009647242098783960UL, 4980127567289598032UL, 7411815341141776403UL, 5697801486788159012UL, 8956111842920343469UL)
        // val second = ulongArrayOf(3074457345618258603UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL)

        // val first = ulongArrayOf(6071688647104718211UL, 5759200060660551458UL, 7368419603789893609UL, 3675507983539229782UL, 7826941493652438620UL, 359244230478755712UL, 8425002711035081291UL, 8043319378088123639UL, 8244327554133168878UL, 5583599066756160094UL, 4696745054287602658UL, 3643314252965935922UL, 1010114181408472889UL, 2975940399552298668UL)
        // val second = ulongArrayOf(3074457345618258603UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL, 3074457345618258602UL, 6148914691236517205UL)
        toomCookSingleTest(first, second)
    }

    fun toomCookSingleTest(first: ULongArray, second: ULongArray) {
        val bigIntResult = BigInteger63Arithmetic.toomCook3Multiply(first, second)
        val javaBigIntResult = first.toJavaBigInteger() * second.toJavaBigInteger()
        // println("------------- ${first.toJavaBigInteger()} * ${second.toJavaBigInteger()}")
        // println("JavaBigInt:          $javaBigIntResult")
        // println("ToomCook3:           ${bigIntResult.toJavaBigInteger()}")
        assertTrue("Failed on ulongArrayOf(${first.joinToString(separator = ", ") { it.toString() + "UL" }}) \n" +
        " ulongArrayOf(${second.joinToString(separator = ", ") { it.toString() + "UL" }})") {
            bigIntResult.toJavaBigInteger() == javaBigIntResult
        }
    }
}