# Support for kotlinx-serialization

Supports BigInteger and BigDecimal serialization using KotlinX Serialization library. 

Note that because KotlinX Serialization doesn't support linux arm targets as well as MinGW x86, serialization support library doesn't either.
Additionally, because of a bug when building serialization support library only JS IR variant is provided.

## Integration

#### Gradle
```kotlin
implementation("com.ionspin.kotlin:bignum-serialization-kotlinx:0.3.2")
```

#### Snapshot builds
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ionspin.kotlin:bignum:0.3.3-SNAPSHOT")

```

## Quick setup and sample

```kotlin

    val json = Json {
        serializersModule = arrayBasedSerializerModule // or humanReadableSerializerModule
    }

    @Serializable
    data class SomeDataHolder(@Contextual val bigInteger: BigInteger, @Contextual val bigDecimal: BigDecimal)

    @Test
    fun serializeAndDeserialize() {
        val bigInt = BigInteger.parseString("12345678901234567890")
        val bigDecimal = BigDecimal.parseString("1.234E-200")
        val someData = SomeDataHolder(bigInt, bigDecimal)
        val serialized = json.encodeToString(someData)
        println(serialized)
        val deserialized = json.decodeFromString<SomeDataHolder>(serialized)
        assertEquals(someData, deserialized)
    }
```

## Usage

There are two different serializers human readable and backing array based. Backing array based serializers are faster
as they don't need to convert to string and back, which are in general slow operations.

## Samples:

### Big Integer:

#### Array:

```kotlin

        @Serializable
        data class BigIntegerArraySerializtionTestData(@Contextual val a : BigInteger, @Contextual val b : BigInteger)
        
        val a = BigInteger.parseString("-1000000000000000000000000000002000000000000000000000000000003")
        val b = BigInteger.parseString("1000000000000000000000000000002000000000000000000000000000003")
        val testObject = BigIntegerHumanReadableSerializtionTestData(a, b)
        val json = Json {
            serializersModule = bigIntegerArraySerializerModule
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)

        result: {"a":{"magnitude":[2083438008362598403,3369964156491764979,4367533269890700295,1274],"sign":"NEGATIVE"},"b":{"magnitude":[2083438008362598403,3369964156491764979,4367533269890700295,1274],"sign":"POSITIVE"}}
```

#### Human readable:
```kotlin
        @Serializable
        data class BigIntegerHumanReadableSerializtionTestData(@Contextual val a : BigInteger, @Contextual val b : BigInteger)

        val a = BigInteger.parseString("-1000000000000000000000000000002000000000000000000000000000003")
        val b = BigInteger.parseString("1000000000000000000000000000002000000000000000000000000000003")
        val testObject = BigIntegerHumanReadableSerializtionTestData(a, b)
        val json = Json {
            serializersModule = bigIntegerhumanReadableSerializerModule
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)
        
        result: {"a":"-1000000000000000000000000000002000000000000000000000000000003","b":"1000000000000000000000000000002000000000000000000000000000003"}
```


### Big Decimal

#### Human readable:
```kotlin

        @Serializable
        data class BigDecimalHumanReadableTestData(@Contextual val a : BigDecimal, @Contextual val b : BigDecimal)

        val a = BigDecimal.parseString("1.000000000020000000000300000000004")
        val b = BigDecimal.parseString("-1.000000000020000000000300000000004")
        
        val testObject = BigDecimalHumanReadableTestData(a, b)
        val json = Json {
            serializersModule = bigDecimalHumanReadableSerializerModule
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)

        result: {"a":"1.000000000020000000000300000000004","b":"-1.000000000020000000000300000000004"}
```

#### Array based: 
```kotlin
        val a = BigDecimal.parseString("1.000000000020000000000300000000004")
        val b = BigDecimal.parseString("-1.000000000020000000000300000000004")
        val testObject = BigDecimalArraySerializtionTestData(a, b)
        val json = Json {
            serializersModule = bigDecimalArraySerializerModule
        }
        val serialized = json.encodeToString(testObject)
        println(serialized)

        result: {"a":{"significand":{"magnitude":[7819074433982969860,108420217250718],"sign":"POSITIVE"},"exponent":0},"b":{"significand":{"magnitude":[7819074433982969860,108420217250718],"sign":"NEGATIVE"},"exponent":0}}
```


