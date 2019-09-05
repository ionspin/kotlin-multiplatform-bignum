[![Build Status](https://travis-ci.com/ionspin/kotlin-multiplatform-bignum.svg?token=HyeUGwxzSsjXNj8mianH&branch=master)](https://travis-ci.com/ionspin/kotlin-multiplatform-bignum)
[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/kotlin-multiplatform-bignum/community#)
[![Maven Central](https://img.shields.io/maven-central/v/com.ionspin.kotlin/bignum.svg)](https://repo1.maven.org/maven2/com/ionspin/kotlin/bignum/)
# Kotlin MP BigNum library 

Kotlin Multiplatform BigNum library is a pure kotlin implementation of arbitrary precision
arithmetic operations. It follows the same approach as Kotlin does on JVM to keep the interface
familiar.

## Notes & Roadmap

This is very early version of the library, and has the base implementation of **integer** and **floating point** operations. 
Modular arithmetic is planned for future releases, as well as improvements such as Karatsuba multiplication, 
Toom-Cook, division using multiplication by reciprocal, and other.

**The API will move fast and break often until v1.0**

Also, there is a plan to implement platform native versions.

Testing to verify that the library works properly is mostly done against Java BigInteger and BigDecimal implementations.

## Should I use this in production?

The library is still under heavy development, and relies on experimental kotlin features, like unsigned integer. 

## Integration

#### Gradle
```kotlin
implementation("com.ionspin.kotlin:bignum:0.1.0")
```

#### Gradle Metadata
BigNum library up to 0.1.0 was published with Gradle Metadata 0.4
To use it you need to add 
```
enableFeaturePreview("GRADLE_METADATA")
```
to your `settings.gradle` file

From version 0.1.1 BigNum library will be publishing Gradle Metadata 1.0.0 which will only be usable by Gradle >= 5.3.
If you are using version 0.1.1 you don't need to modify your settings.gradle

#### Snapshot builds
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ionspin.kotlin:bignum:0.1.1-SNAPSHOT")

```

## Usage

### Integers

#### Creating Big Integers

To create a big integer you can parse a string:
```kotlin
BigInteger.parse("-1122334455667788990011223344556677889900", 10)
```

Or use the extensions or companion function for `Long`, `Int`, `Byte` or `Short`
```kotlin
val bigIntegerExtension = 234L.toBigInteger()
val bigIntegerCompanion = BigInteger.fromLong(234L)

```

Or use extensions functions for `String`
```kotlin
"12345678".toBigInteger()
```
### Basic Arithmetic Operations

#### Addition
```kotlin
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromInt(Integer.MAX_VALUE)

val sum = a + b
println("Sum: $sum")
----- Output -----
Sum: Sum: 9223372039002259454
```

#### Subtraction
```kotlin
val a = BigInteger.fromLong(Long.MIN_VALUE)
val b = BigInteger.fromLong(Long.MAX_VALUE)

val difference = a - b
println("Difference: $difference")
----- Output -----
Difference: -18446744073709551615
```

#### Multiplication
```kotlin
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromLong(Long.MIN_VALUE)

val product = a * b

println("Product: $product")
----- Output -----
Product: -85070591730234615856620279821087277056
```

#### Division - Quotient
```kotlin
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromInt(Int.MAX_VALUE)

val dividend = a + b
val divisor = BigInteger.fromLong(Long.MAX_VALUE)

val quotient = dividend / divisor
        println("Quotient: $quotient")
----- Output -----
Quotient: 1
```

#### Division - Remainder
```kotlin
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromInt(Int.MAX_VALUE)

val dividend = a + b
val divisor = BigInteger.fromLong(Long.MAX_VALUE)

val remainder = dividend % divisor
println("Remainder: $remainder")
----- Output -----
Remainder: 2147483647
```

#### Division - Quotient and Remainder
```kotlin
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromInt(Int.MAX_VALUE)

val dividend = a + b
val divisor = BigInteger.fromLong(Long.MAX_VALUE)

val quotientAndRemainder = dividend divrem divisor

println("Quotient: ${quotientAndRemainder.quotient} \nRemainder: ${quotientAndRemainder.remainder}")
----- Output -----
Quotient: 1 
Remainder: 2147483647
```

### Bitwise Operations

#### Shift Left
```kotlin
val a = BigInteger.fromByte(1)

val shifted = a shl 215
println("Shifted: $shifted")
----- Output -----
Shifted: 52656145834278593348959013841835216159447547700274555627155488768
```

#### Shift Right
```kotlin
val a = BigInteger.parseString("100000000000000000000000000000000", 10)

val shifted = a shr 90
----- Output -----
Shifted: 80779

```

#### Xor
```kotlin
val operand = BigInteger.parseString("11110000", 2)
val mask = BigInteger.parseString("00111100", 2)

val xorResult = operand xor mask

println("Xor result: ${xorResult.toString(2)}")
----- Output -----
Xor result: 11001100
```


#### And
```kotlin
val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
val andResult = operand and mask
println("And result: ${andResult.toString(16)}")
----- Output -----
And result: ff000000000000
```

#### Or
```kotlin
val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
val orResult = operand or mask
println("Or result: ${orResult.toString(16)}")
----- Output -----
Or result: ffffffffffff0000000000
```

#### Binary Not

Unlike Java BigInteger which does two's complement inversion, this method does bitwise inversion, 

i.e.:

    If the number was "1100" binary, not() returns "0011" => "11" => 4 in base 10
    In the same case Java BigInteger would return "1011" => -13 two's complement base 10
    
```kotlin
val operand = BigInteger.parseString("11110000", 2)
val result = operand.not()
println("Not operation result: ${result.toString(2)}")
----- Output -----
Inv result: 1111
```

#### Modular integers

A `modInverse` function that is equivalent to java BigInteger `modInverse` is available. Note that this method will
produce a **BigInteger** not a **ModularBigInteger**

Big integers can be converted to modularIntegers with same modulo, and then `inverse()` method is available. This method 
**will** return ModularBigInteger

```kotlin
val a = 100_002.toBigInteger()
val modularA = a.toModularBigInteger(500.toBigInteger())
println("ModularBigInteger: ${modularA.toStringWithModulo()}")
----- Output -----
ModularBigInteger: 2 mod 500
```

If you want to create more ModularBigIntegers with the same module, you can retrieve creator by calling `getCreator`

More inforamtion about the ModularBigIntegers can be found in the third section

## Floating Point

### Creating

#### Parsing
To create a BigDecimal you can parse a string in _expanded_ or scientific notation

**Scientific** 

```kotlin
val bigDecimal = BigDecimal.parseString("1.23E-6)")
println("BigDecimal: $bigDecimal")
----- Output -----
BigDecimal: 1.23E-6
```

**Expanded**

```kotlin
val bigDecimal = BigDecimal.parseString("0.00000123")
println("BigDecimal: $bigDecimal")
----- Output -----
BigDecimal: 1.23E-6
```

#### From Long, Int, Short, Byte

You can convert standard types to BigDecimal, i.e. Long
```kotlin
val bigDecimal = BigDecimal.fromLong(7111)
println("BigDecimal: $bigDecimal")
----- Output -----
BigDecimal: 7.111E+3
``` 

Or you can specify an exponent. when you do specify an exponent, input value (long, int, short, byte) is considered to 
be in **scientific notation**.
```kotlin
val bigDecimal = BigDecimal.fromLongWithExponent(1, (-5).toBigInteger())
println("BigDecimal: $bigDecimal")
println("BigDecimalExpanded: ${bigDecimal.toStringExpanded()}")
----- Output -----
BigDecimal: 1.0E-5
BigDecimalExpanded: 0.00001

```

### Extension functions

For `String`
```kotlin

val bigDecimal = "12345678.123".toBigInteger
```

Or for `Double` of `Float`

```kotlin
val bigDecimalFromFloat = 123.456f.toBigDecimal() 
val bigDecimalFromDouble = 123.456.toBigDecimal()

```

## toString

By default toString() is returned in scientific output, but expanded output is also available
```kotlin
val bigDecimal = BigDecimal.parseString("123.456")
println("BigDecimal: ${bigDecimal.toStringExpanded()}")
bigDecimal.toStringExpanded() == "123.456"
----- Output -----
BigDecimal: 123.456
```

## toByteArray and fromByteArray

Converts the BigInteger to and from two's complement big endian array of bytes
```kotlin
val bigIntOriginal = BigInteger.fromULong(ULong.MAX_VALUE)
val byteArray = bigIntOriginal.toByteArray()
val reconstructed = BigInteger.fromByteArray(byteArray)
println("${bigIntOriginal == reconstructed}")
----- Output -----
true
```

### Arithmetic operations

Standard arithmetic operations that are present:
* Addition
* Subtraction
* Multiplication
* Division
* Exponentiation
* Increase by one
* Decrease by one
* Absolute value
* Negate
* Signum


(Suspiciously missing is square root, both from BigInteger and BigDecimal, should be added soon™)

Operations are executed with existing significands and then rounded down afterwards. Decimal mode parameter controls the precision and rounding mode

### DecimalMode
This is a counterpart to the Java BigDecimal MathContext. 

```kotlin
data class DecimalMode(val decimalPrecision : Long = 0, val roundingMode : RoundingMode = RoundingMode.NONE)
``` 

`decimalPrecision` defines how many digits should significand have

`roundingMode` defines rounding mode. 

##### Decimal mode resolution

* `DecimalMode` supplied to the operation always overrides all other `DecimalModes` set in `BigDecimal`s

* If a `DecimalMode` is set when creating a `BigDecimal` that mode will be used for all operations.

* If two `BigDecimal`s have different `DecimalModes` with different RoundingModes an `ArithmeticException` will be thrown. 
If the modes are same, but the precision is different, larger precision will be used.

##### Infinite precision  

Precision 0 and roundingMode none attempt to provide infinite precisions. Exception is division, where default precision is
is the sum of precisions of operands (or 6, if the sum is below 6). If result of the operation cannot fit inside precision and RoundingMode is NONE, `ArithmeticException` 
will be thrown.

Example from the tests:
```kotlin
   fun readmeDivisionTest() {
        assertFailsWith(ArithmeticException::class) {
            val a = 1.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a/b
        }

        assertTrue {
            val a = 1.toBigDecimal()
            val b = 3.toBigDecimal()
            val result = a.div(b, DecimalMode(20, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO))
            result.toString() == "3.3333333333333333333E-1"
        }
    }
```

#### Rounding modes
Name | Description
-----|----------------------------
FLOOR | Towards negative infinity
CEILING|Towards positive infinity
AWAY_FROM_ZERO|Away from zero
TOWARDS_ZERO| Towards zero
NONE|Infinite decimalPrecision, and beyond
ROUND_HALF_AWAY_FROM_ZERO|Round towards nearest integer, using towards zero as tie breaker when significant digit being rounded is 5
ROUND_HALF_TOWARDS_ZERO|Round towards nearest integer, using away from zero as tie breaker when significant digit being rounded is 5
ROUND_HALF_CEILING|Round towards nearest integer, using towards infinity as tie breaker when significant digit being rounded is 5
ROUND_HALF_FLOOR|Round towards nearest integer, using towards negative infinity as tie breaker when significant digit being rounded is 5

### Modular Integers

Modular arithmetic operations are supported only between integers with the same modulo. 

## Creating Modular Integers

First define the modulo you are going to use by getting an instance of the creator, and than 
use that creator to create instances of modular integers

```kotlin
val creator = ModularBigInteger.creatorForModulo(100)
val modularBigInteger = creator.fromLong(150)
println("ModularBigInteger: ${modularBigInteger.toStringWithModulo()}")
----- Output -----
ModularBigInteger: 50 mod 100

```

Otherwise behavior is similar to normal integers


### Sources

For examples of rounding modes consult [Comparison of approaches for rounding to an integer](https://en.wikipedia.org/wiki/Rounding) 
on Wikipedia

This library draws inspiration from libraries like Java BigInteger, GNU MP Arithmetic Library, Javolution JScience,
as well as following literature

```
Modern Computer Arithmetic
Richard P. Brent and Paul Zimmermann
Version 0.5.9 of 7 October 2010
```
```
Hacker`s Delight
Henry S. Warren, Jr.
Second Edition
```
```
Art of Computer Programming, Volume 2: Seminumerical Algorithms
Donald E. Knuth
3rd Edition
```

```
Refinement of a newton reciprocal algorithm for arbitrary precision numbers
Yiping Cheng, Ze Liu
```
And many other blogs and posts scattered over the internet.

If you want to try building BigNum library yourself, those are the sources I would recommend to start with.
