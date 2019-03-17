# Kotlin MP BigNum library

Kotlin Multiplatform BigNum library is a pure kotlin implementation of arbitrary precision
arithmetic operations. it follows the same approach as Kotlin does on JVM to keep the interface
familiar.

## Notes

This is the first version of the library, and has the base implementation of all operations. Improvements such as Karatsuba multiplication, Toom-Cook, division using multiplication by reciprocal and other are planned for future releases.

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


