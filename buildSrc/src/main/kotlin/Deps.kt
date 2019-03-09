/*
 * Kontroller
 * Copyright (C) 2018-present Ugljesa Jovanovic (ugljesa.jovanovic@ionspin.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

object Versions {
    val klock = "1.1.1"
    val kotlinCoroutines = "1.1.1"
    val timber = "5.0.0-SNAPSHOT"
    val oshi = "3.12.0"
    val kotlin = "1.3.20"
    val ktor = "1.1.1"
    val kotlinSerialization = "0.10.0"
    val nodePlugin = "1.3.0"


}

object Deps {

    object Common {
        val stdLib = "stdlib-common"
        val test = "test-common"
        val testAnnotation = "test-annotations-common"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinSerialization}"
        val ktorClient = "io.ktor:ktor-client:${Versions.ktor}"
        val ktorClientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        val timber = "com.jakewharton.timber:timber-common:${Versions.timber}"
        val klock = "com.soywiz:klock:${Versions.klock}"
    }

    object Js {
        val stdLib = "stdlib-js"
        val test = "test-js"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerialization}"
        val ktorClient = "io.ktor:ktor-client-js:${Versions.ktor}"
        val ktorClientCore = "io.ktor:ktor-client-core-js:${Versions.ktor}"
        val timber = "com.jakewharton.timber:timber-js:${Versions.timber}"
    }

    object Jvm {
        val stdLib = "stdlib-jdk8"
        val test = "test"
        val testJUnit = "test-junit"
        val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val coroutinesjdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerialization}"
        val ktorClientJvm = "io.ktor:ktor-client-jvm:${Versions.ktor}"
        val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        val ktorClientCore = "io.ktor:ktor-client-core-jvm:${Versions.ktor}"
        val timber = "com.jakewharton.timber:timber-jdk:${Versions.timber}"
        val oshi = "com.github.oshi:oshi-core:${Versions.oshi}"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
    }

    object iOs {
        val ktorClient = "io.ktor:ktor-client-ios:${Versions.ktor}"
        val ktorClientCore = "io.ktor:ktor-client-core-ios:${Versions.ktor}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    }

    object Native {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"

    }

}


object PluginsDeps {
    val kotlinSerializationPlugin = "kotlinx-serialization"
    val multiplatform = "multiplatform"
    val node = "com.github.node-gradle.node"
    val mavenPublish = "maven-publish"
}

