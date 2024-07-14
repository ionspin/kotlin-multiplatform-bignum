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

object Versions {
    val kotlinCoroutines = "1.9.0-RC"
    val kotlin = "2.0.0"
    val kotlinSerialization = "1.7.1"
    val dokkaPlugin = "1.9.10"
}

val projectVersion = "0.3.10-SNAPSHOT"

object Deps {

    object Common {
        val stdLib = "stdlib-common"
        val test = "test-common"
        val testAnnotation = "test-annotations-common"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val testCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
        val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
        val bignum = "com.ionspin.kotlin:bignum:$projectVersion"
    }

    object Js {
        val stdLib = "stdlib-js"
        val test = "test-js"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerialization}"
    }

    object Jvm {
        val stdLib = "stdlib-jdk8"
        val test = "test"
        val testJUnit = "test-junit"
        val reflection = "reflect"
        val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerialization}"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
    }

    object iOs {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    }

    object Native {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    }

    object WasmJs {
        val stdLib = "stdlib-wasm-js"
        val test = "test-wasm-js"
    }

    object Project {
        val bignum = ":bignum"
    }
}

object PluginsDeps {
    object PluginVersions {
        val spotlessVersion = "5.14.0"
        val kotlinxSerialization = Versions.kotlin
    }


    val multiplatform = "multiplatform"
    val node = "com.github.node-gradle.node"
    val mavenPublish = "maven-publish"
    val signing = "signing"
    val dokka = "org.jetbrains.dokka"
    val spotless = "com.diffplug.spotless"
    val kotlinxSerialization = "plugin.serialization"
}

