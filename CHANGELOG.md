## Descriptive changelog
(All dates are DD.MM.YYYY)
##### 0.1.1-SNAPSHOT - 5.9.2019
- Bumped gradle version to 5.6.1, which means that the published Gradle Metadata will be 1.0, making
metadata resolution available only on Gradle >= 5.3
- Fixed several issues related to big decimal comparison, modular integer sign, etc.
- Added more BigDecimal extension functions
- Added ModularBigInteger extension functions
- Added Karatsuba multiplication


##### 0.1.0 - 31.7.2019
- Added toByteArray and fromByteArray
- Added toFloat and toDouble to BigInteger and ModularBigInteger classes
- Added BigInteger creation from Float and Double by using `tryFromFloat` and `tryFromDouble`, with optional exact 
parameter to preserve precision.
- Added BigInteger comparison with Float and Double
- Added BigDecimal configuration option to switch to expanded representation instead of scientific when calling `toString()`
- Improved ModularBigInteger exponentiation algorithm, based on Bruce Schneier Applied Cryptography pesudocode

##### 0.0.9 - 11.5.2019 Adding modular integer support, changing api
- Added modular integers - ModularBigInteger
- Added modInverse method to BigInteger
- Extracted interfaces that model big numbers better (BigNumber<BigType> interface and accompanying interfaces)
- Implemented integer reciprocal based on newton iteration (Based on paper by Yiping Cheng, Ze Lie : Refinement of a newton reciprocal algorithm for arbitrary precision numbers)
- Implemented division by reciprocal multiplication. Not used by division at the moment as it is unoptimized and slower than basecase division in ealry benchmarks.
- Fixed a bug in Int32 shift right when shift amount was an exact multiple of word size 
- Added constructor overloads
- Added value methods (intValue, longValue...)
- Renamed invPrecise() bigInteger method to not()
- Renamed numberOfDigits() to numberOfDecimalDigits()
- Introduced BigNumber and BitwiseOperations interfaces 
- Added iOS ARM 32bit support 


##### Minor update - 0.0.8 - 02.04.2019
- Released ios (for X64 and arm) and macos X64 artifacts
- No functional changes


##### Minor update - 0.0.7 - 31.3.2019
- Added extension functions for `String` to `BigInteger` and `BigDecimal`
- Fixed a couple of parsing issues
- Added this changelog

#### Adding floating point support/BigDecimal - 0.0.6 - 30.3.2019
- Added BigDecimal
- Fixed several BigInteger bugs

#### Initial release/BigInteger - 0.0.5 - 20.3.2019
- Added Big integer support

