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

package com.ionspin.kotlin.bignum.integer.arithmetic

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Mar-2019
 */
@ExperimentalUnsignedTypes
class
BigIntegerTest {

    @Test
    fun testPow() {
        val a = BigInteger.fromInt(2)
        val b = a.pow(10)
        val expected = BigInteger.fromInt(1024)
        assertTrue { b == expected }
    }

    @Test
    fun testPow10() {
        val a = BigInteger.fromInt(10)
        val b = a.pow(2)
        val expected = BigInteger.fromInt(100)
        assertTrue { b == expected }
    }

    @Test
    fun testPow10ButAlsoGenerateTable() {
        val generateTable = false
        val a = BigInteger.fromInt(10)
        if (generateTable) { println("    val powersOf10 = arrayOf(") }
        for (i in 0 .. 100) {
            val powered = a.pow(i)
            if (generateTable) { println("        ulongArrayOf(${powered.magnitude.joinToString(separator = "UL, "){ it.toString() }}UL), ") }
            assertTrue {
                powered.toString(10) == "1" + i.toBigInteger() * '0'
            }
        }
        if (generateTable) { println("    )") }
    }

    @Test
    fun testPow10to145() {
        val a = BigInteger.fromInt(10)
        val b = a.pow(145)
        val expectedString = "10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000000000000"

        val expected = BigInteger.parseString(expectedString, 10)

        assertTrue { b == expected }
    }

    @Test
    fun testPowBigger() {
        val a = BigInteger.fromInt(2)
        val b = a.pow(10240)
        val expectedString =
            "3524971412108382657134814839800281546439142134396647106039138260573107027685474936504833029647366386245" +
                    "69681553952983739732590494759431136198883386731161336668147068707652719076562056460186083699855" +
                    "58721267670321739031938633833281889192620158426531806923144239269726876399951961191980348023291" +
                    "70347230576378241039458975893458563111107812043530303268881875144643529137135717175563277536293" +
                    "26947950763134366874696380043276893902467353218558306108568659249137608267637760032658517165573" +
                    "34210642277343475757799780499021559822412434275087084317293455129570406707590002071704673135527" +
                    "53354321735598756810769757794678579641245604836007296561687102486624465008105906818303813451851" +
                    "42229871868373945980198595129936003792361901975768389050807333599890946870089994162477220200619" +
                    "92559931401872357379708488585003666965930609730430774107407494018065365845077094320534700692354" +
                    "40016982413157838915365691675468225242556274289502682208611223618576893194043332407869238646364" +
                    "23780292915823845509040122842652771246674528169856593374975809915925102014797665008774278345666" +
                    "19156314388107585743546289067551052434075678195345373363919571323210113622615511765134329627207" +
                    "95579360537689287593835767287088130567930552129335997542780192199753489147409086811346735778435" +
                    "97833830910857171008072284250312267769851973643594046830415066139436466661994548993636858018487" +
                    "76729685837803228216113833854742443409221480450232563130417709625320794971672737737385983975520" +
                    "04773997816512490691685793196090240739784153665765037875801240915720593951308532428243929010890" +
                    "90690365154306903599631529865877499305168806703261450369876070529616967815564185509662018228218" +
                    "57978020062536824015697620957222738065538832187097409859502669196589025961199448758997373792973" +
                    "19172333554977239487887405085453278592247582283640379398662319317402093143238141843702276041268" +
                    "22763829893548396254532412898071082609051342346791309548675704473545497601746910070785284527450" +
                    "27994943853229480544512368831378761119681616719327637308142315105120528704683515182038320225078" +
                    "66531391173174936425562128443430494543721460940600864052097202950995543556809488881570147041941" +
                    "08891565239711821728144232741409554280705943283816670482867719728577034355258035447078345677740" +
                    "27206614143419982410109261930698311010857874866840743851472857645330929169548403751084494725893" +
                    "72935545047377105998680105834202190273536762790097487236813783899639737989816145482597091073285" +
                    "82027812829739376428479733818386729806933990394293426130015951489680820100160610223162428423676" +
                    "72741265405434553107296623559604413326352140529618171175450657884255099334618722731697920185582" +
                    "43718239139767330116816068251663921470656698146596173137480894913174236475299307832636771411700" +
                    "14042109302515381324422193350726720968651846913030271569624397770537072865839497640551512918164" +
                    "02546462452719134797179099210233577596277925646031824172274874084562113440043397395191065473620" +
                    "71710425068604089658092870084259391917328384453147095220560087448230248852386707453290778126499" +
                    "08653518446848070122080391082875645348545004863915388760636114766656202302948114683518353740720" +
                    "60530215907909311281816131942219776"
        val expected = BigInteger.parseString(expectedString, 10)
        assertTrue { b == expected }

    }

    @Test
    fun getBitTest() {
        val maxLongPlusOne = BigInteger.fromLong(Long.MAX_VALUE) + 1
        val maxLongBit63 = maxLongPlusOne.bitAt(63)
        val maxLongBit62 = maxLongPlusOne.bitAt(62)
        assertTrue { maxLongBit63 }
        assertFalse { maxLongBit62 }

        val one = BigInteger.ONE
        val bitAtZero = one.bitAt(0)
        val bitAtOne = one.bitAt(1)
        val bitAtNinety = one.bitAt(90)

        assertTrue { bitAtZero }
        assertFalse { bitAtOne }
        assertFalse { bitAtNinety }

        assertFailsWith(RuntimeException::class) {
            one.bitAt(Long.MAX_VALUE)
        }
    }




    @Test
    fun testModInverse() {

        assertTrue {
            var a = BigInteger(11)
            var aInverse = a.modInverse(5.toBigInteger())
            aInverse == BigInteger(1)
        }
        assertFailsWith<ArithmeticException> {
            var a = BigInteger(10)
            a.modInverse(5.toBigInteger())
        }
    }

    @Test
    fun testGcd() {
        val a = 10.toBigInteger()
        val b = 3.toBigInteger()
        assertTrue { a.gcd(b) == 1.toBigInteger() }
        var c = 6.toBigInteger()
        assertTrue { a.gcd(c) == 2.toBigInteger() }
    }

    @Test
    fun divisionSign() {
        assertTrue {
            val a = 7.toBigInteger()
            val b = 3.toBigInteger()
            val (q,r) = a divrem b
            a == (q * b + r)
        }
        assertTrue {
            val a = -7.toBigInteger()
            val b = 3.toBigInteger()
            val (q,r) = a divrem b
            a == (q * b + r)
        }
        assertTrue {
            val a = 7.toBigInteger()
            val b = -3.toBigInteger()
            val (q,r) = a divrem b
            a == (q * b + r)
        }
        assertTrue {
            val a = -7.toBigInteger()
            val b = -3.toBigInteger()
            val (q,r) = a divrem b
            a == (q * b + r)
        }
    }

    @Test
    fun testModInfix() {
        assertTrue {
            val a = -152.toBigInteger()
            val b = a.mod(100.toBigInteger())
            b == 48.toBigInteger()
        }

        assertTrue {
            val a = 152.toBigInteger()
            val b = a.mod(100.toBigInteger())
            b == 52.toBigInteger()
        }


    }

    @Test
    fun testNarrowingFunctions() {
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.intValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.shortValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.byteValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.longValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.uintValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.ushortValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger()
            a.ubyteValue(exactRequired = true)
        }
        assertFailsWith(ArithmeticException::class) {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.ulongValue(exactRequired = true)
        }

        assertTrue {
            val a = 2.toBigInteger()
            a.intValue(exactRequired = true) == 2
        }
        assertTrue {
            val a = 2.toBigInteger()
            val short : Short = 2
            a.shortValue(exactRequired = true) == short
        }
        assertTrue {
            val a = 2.toBigInteger()
            val byte : Byte = 2
            a.byteValue(exactRequired = true) == byte
        }
        assertTrue {
            val a = 2.toBigInteger()
            a.longValue(exactRequired = true) == 2L
        }
        assertTrue {
            val a = 2.toBigInteger()
            a.uintValue(exactRequired = true) == 2U
        }
        assertTrue {
            val a = 2.toBigInteger()
            val ushort : UShort = 2U
            a.ushortValue(exactRequired = true) == ushort
        }
        assertTrue {
            val a = 2.toBigInteger()
            val ubyte : UByte = 2U
            a.ubyteValue(exactRequired = true) == ubyte
        }
        assertTrue {
            val a = 2.toBigInteger()
            a.ulongValue(exactRequired = true) == 2UL
        }

        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.intValue(exactRequired = false) == 9
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            val short : Short = 9
            a.shortValue(exactRequired = false) == short
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            val byte : Byte = 9
            a.byteValue(exactRequired = false) == byte
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.longValue(exactRequired = false) == 9L
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.uintValue(exactRequired = false) == 9U
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            val ushort : UShort = 9U
            a.ushortValue(exactRequired = false) == ushort
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            val ubyte : UByte = 9U
            a.ubyteValue(exactRequired = false) == ubyte
        }
        assertTrue {
            val a = Long.MAX_VALUE.toBigInteger() + 10
            a.ulongValue(exactRequired = false) == 9UL
        }



    }

    @Test
    fun testRange() {


        assertTrue {
            val range = 1.toBigInteger() .. 10.toBigInteger()
            range.contains(5.toBigInteger())
        }

        assertTrue {
            val range = 1.toBigInteger() .. (Long.MAX_VALUE.toBigInteger() * Long.MAX_VALUE.toBigInteger())
            range.contains(Long.MAX_VALUE.toBigInteger() * Int.MAX_VALUE)
        }

    }

}