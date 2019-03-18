[![Build Status](https://travis-ci.com/ionspin/kotlin-multiplatform-bignum.svg?token=HyeUGwxzSsjXNj8mianH&branch=master)](https://travis-ci.com/ionspin/kotlin-multiplatform-bignum)
# Kotlin MP BigNum library 

Kotlin Multiplatform BigNum library is a pure kotlin implementation of arbitrary precision
arithmetic operations. it follows the same approach as Kotlin does on JVM to keep the interface
familiar.

## Notes & Roadmap

This is the first version of the library, and has the base implementation of **integer** operations. Floating point arithmetic,
and modular arithmetic are planned for future releases, as well as improvements such as Karatsuba multiplication, 
Toom-Cook, division using multiplication by reciprocal and other.

Also there is a plan to implement native

## Should I use this in production

No. Even though the tests pass, and it seems to be working fine, the library is not mature enough to be used for anything even remotely critical. Also Kotlin Multiplatform is still experimental, so there's that.

## Integration

#### Gradle
```
    implementation("com.ionspin.kotlin.bignum:core:0.0.4")
```


## Usage

### Creating big integers

To create a big integer you can parse a string:
```
BigInteger.parse("-1122334455667788990011223344556677889900", 10)
```

Or use the extensions function for `Long`, `Int`, `Byte` or `Short`
```
BigInteger.fromLong(234L)
```

### Basic arithmetic operations

#### Addition
```
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromInt(Integer.MAX_VALUE)

val sum = a + b
println("Sum: $sum")
----- Output -----
Sum: Sum: 9223372039002259454
```

#### Subtraction
```
val a = BigInteger.fromLong(1L)
val b = BigInteger.fromInt(2L)

val sum = a + b
println("Sum: $sum")
----- Output -----
Sum: 3
```

#### Multiplication
```
val a = BigInteger.fromLong(Long.MAX_VALUE)
val b = BigInteger.fromLong(Long.MIN_VALUE)

val product = a * b

println("Product: $product")
----- Output -----
Product: -85070591730234615856620279821087277056
```

#### Division - Quotient
```
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
```
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
```
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

### Bitwise operations

#### Shift left
```
val a = BigInteger.fromByte(1)

val shifted = a shl 215
println("Shifted: $shifted")
----- Output -----
Shifted: 52656145834278593348959013841835216159447547700274555627155488768
```

#### Shift right
```
val a = BigInteger.parseString("100000000000000000000000000000000", 10)

val shifted = a shr 90
----- Output -----
Shifted: 52656145834278593348959013841835216159447547700274555627155488768

```

#### XOR
```
val operand = BigInteger.parseString("11110000", 2)
val mask = BigInteger.parseString("00111100", 2)

val xorResult = operand xor mask

println("Xor result: ${xorResult.toString(2)}")
----- Output -----
Xor result: 11001100
```


#### And
```
val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
val andResult = operand and mask
println("And result: ${andResult.toString(16)}")
----- Output -----
And result: ff000000000000
```

#### Or
```
val operand = BigInteger.parseString("FFFFFFFFFF000000000000", 16)
val mask =    BigInteger.parseString("00000000FFFF0000000000", 16)
val orResult = operand or mask
println("Or result: ${orResult.toString(16)}")
----- Output -----
Or result: ffffffffffff0000000000
```

#### Inverted "precise"

Unlike Java BigInteger which does two's complement inversion, this method does bitwise inversion, 

I.e.: If the number was "1100" binary, invPrecise returns "0011" => "11" => 4 decimal 
Where Java BigInteger would return "1011" => -13 two's complement decimal
```
val operand = BigInteger.parseString("11110000", 2)
val invResult = operand.invPrecise()
println("Inv result: ${invResult.toString(2)}")
----- Output -----
Inv result: 1111
```
