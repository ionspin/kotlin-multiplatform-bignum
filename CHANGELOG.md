## Descriptive changelog
(All dates are DD.MM.YYYY)
##### 0.1.4-SNAPSHOT - BigDecimal improvements, MINGW64 and MINGW86 targets added
- Main library now has dependancies only on the kotlin standard library (for now, coroutines will be coming back at some point in the future).
- Renamed BigDecimal `round` method to `roundSignificand`, as it describes what it does more precisely
- Added `roundAtDigitPosition` and `roundAfterDecimalPoint` convenience methods.
- Use Long instead of BigInteger for BigDecimal exponent.
- Adding MingwX64 and MingwX86 targets.

##### 0.1.3 - 19.11.2019 - Kotlin and Gradle version bump 
- Kotlin version bump to 1.3.60 - especially important as there was a comparison performance improvement for inline classes 
which are heavily used by bignum library (ULong/UInt).
- Actually bumped gradle to 5.6.1, it was mistakenly left at 5.1.1 in library versions 0.1.1 and 0.1.2
- Cleaned up dependancies, coroutines are currently used only in test modules so they are moved there.


##### 0.1.2 - 17.11.2019 - Performance improvements
- Removed removeLeadingZeros and replaced with countLeadingZeros and appropriate algorithm changes.
- Realized plural of zero is zeros not zeroes.
- Improved exponentiation to use square-and-multiply method.



##### 0.1.1 - 19.10.2019 - Multiplication algorithm improvements
- Implemented Toom-Cook-3 multiplication, although still slow because of inefficient division.
- Bumped gradle version to 5.6.1, which means that the published Gradle Metadata will be 1.0, making
metadata resolution available only on Gradle >= 5.3
- Fixed several issues related to big decimal comparison, modular integer sign, etc.
- Added more BigDecimal extension functions.
- Added ModularBigInteger extension functions.
- Added Karatsuba multiplication.
- Added `copy` and `moveDecimalPoint` methods.
- Added `fromUByteArray` and `toUByteArray`.


##### 0.1.0 - 31.7.2019 ByteArray support and other improvements
- Added toByteArray and fromByteArray.
- Added toFloat and toDouble to BigInteger and ModularBigInteger classes.
- Added BigInteger creation from Float and Double by using `tryFromFloat` and `tryFromDouble`, with optional exact 
parameter to preserve precision.
- Added BigInteger comparison with Float and Double.
- Added BigDecimal configuration option to switch to expanded representation instead of scientific when calling `toString()`.
- Improved ModularBigInteger exponentiation algorithm, based on Bruce Schneier Applied Cryptography pesudocode.

##### 0.0.9 - 11.5.2019 Adding modular integer support, changing api
- Added modular integers - ModularBigInteger.
- Added modInverse method to BigInteger.
- Extracted interfaces that model big numbers better (BigNumber<BigType> interface and accompanying interfaces)
- Implemented integer reciprocal based on newton iteration (Based on paper by Yiping Cheng, Ze Lie : Refinement of a newton reciprocal algorithm for arbitrary precision numbers)
- Implemented division by reciprocal multiplication. Not used by division at the moment as it is unoptimized and slower than basecase division in early benchmarks.
- Fixed a bug in Int32 shift right when shift amount was an exact multiple of word size 
- Added constructor overloads
- Added value methods (intValue, longValue...)
- Renamed invPrecise() bigInteger method to not()
- Renamed numberOfDigits() to numberOfDecimalDigits()
- Introduced BigNumber and BitwiseOperations interfaces 
- Added iOS ARM 32bit support 


##### 0.0.8 - 02.04.2019 - Minor update 
- Released ios (for X64 and arm) and macos X64 artifacts
- No functional changes


##### 0.0.7 - 31.3.2019 Minor update
- Added extension functions for `String` to `BigInteger` and `BigDecimal`
- Fixed a couple of parsing issues
- Added this changelog

##### 0.0.6 - 30.3.2019 - Adding floating point support/BigDecimal  
- Added BigDecimal
- Fixed several BigInteger bugs

#### 0.0.5 - 20.3.2019 - Initial release/BigInteger 
- Added Big integer support

