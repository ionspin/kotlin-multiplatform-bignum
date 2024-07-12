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

@file:Suppress("UnstableApiUsage")

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.dokka)
    id(PluginsDeps.spotless) version PluginsDeps.PluginVersions.spotlessVersion
}

val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots/"

val sonatypePassword: String? by project

val sonatypeUsername: String? by project

enum class HostOs {
    LINUX, WINDOWS, MAC
}

val bignumPrimaryDevelopmentOs: String? by project

val primaryDevelopmentOs: HostOs = if (bignumPrimaryDevelopmentOs != null) {
    println("Selected dev OS: $bignumPrimaryDevelopmentOs")
    when (bignumPrimaryDevelopmentOs) {
        "linux" -> HostOs.LINUX
        "windows" -> HostOs.WINDOWS
        "mac" -> HostOs.MAC
        else -> throw org.gradle.api.GradleException("Invalid development enviromoment OS selecte: " +
                "$bignumPrimaryDevelopmentOs. Only linux, windows and mac are supported at the moment")
    }
} else {
    HostOs.LINUX
}

val sonatypePasswordEnv: String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv: String? = System.getenv()["SONATYPE_USERNAME"]

repositories {
    mavenCentral()
    google()
}
group = "com.ionspin.kotlin"
version = projectVersion

val ideaActive = System.getProperty("idea.active") == "true"

fun getHostOsName(): HostOs {
    val target = System.getProperty("os.name")
    if (target == "Linux") return HostOs.LINUX
    if (target.startsWith("Windows")) return HostOs.WINDOWS
    if (target.startsWith("Mac")) return HostOs.MAC
    throw GradleException("Unknown OS: $target")
}

kotlin {

    val hostOs = getHostOsName()
    println("Host os name $hostOs")

    if (ideaActive) {
        when (hostOs) {
            HostOs.LINUX -> linuxX64("native")
            HostOs.MAC -> macosX64("native")
            HostOs.WINDOWS -> mingwX64("native")
        }
    }
    if (hostOs == primaryDevelopmentOs) {
        jvm()

        js {
            compilations {
                this.forEach {
                    it.compileKotlinTask.kotlinOptions.sourceMap = true
                    it.compileKotlinTask.kotlinOptions.metaInfo = true

                    if (it.name == "main") {
                        it.compileKotlinTask.kotlinOptions.main = "call"
                    }
                    println("Compilation name ${it.name} set")
                    println("Destination dir ${it.compileKotlinTask.destinationDirectory}")
                }
                nodejs()
                browser() {
                    testTask {
                        useKarma {
                            useChromeHeadless()
                        }
                    }
                }
            }
        }

        wasmJs {
            browser()
        }
    }

    if (hostOs == HostOs.LINUX) {
        linuxX64("linux")
        if (ideaActive.not()) {
            linuxArm32Hfp()
            linuxArm64()
            androidNativeX64()
            androidNativeX86()
            androidNativeArm32()
            androidNativeArm64()
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    tvos()
    tvosSimulatorArm64()
    if (ideaActive.not()) {
        watchos()
        watchosSimulatorArm64()
    }
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(Deps.Common.testCoroutines)
                implementation(kotlin(Deps.Common.testAnnotation))
                implementation(Deps.Common.coroutines)
            }
        }

        val nativeMain = if (ideaActive) {
            val nativeMain by getting {
                dependsOn(commonMain)
            }
            nativeMain
        } else {
            val nativeMain by creating {
                dependsOn(commonMain)
            }
            nativeMain
        }
        val nativeTest = if (ideaActive) {
            val nativeTest by getting {
                dependsOn(commonTest)
            }
            nativeTest
        } else {
            val nativeTest by creating {
                dependsOn(commonTest)
            }
            nativeTest
        }

        if (hostOs == primaryDevelopmentOs) {
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.stdLib))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(kotlin(Deps.Jvm.reflection))
                }
            }

            val jsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.stdLib))
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                }
            }

            val wasmJsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.WasmJs.stdLib))
                }
            }
            val wasmJsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.WasmJs.test))
                }
            }
        }

        if (hostOs == HostOs.LINUX) {

            val linuxMain by getting {
                dependsOn(nativeMain)
            }
            val linuxTest by getting {
                dependsOn(nativeTest)
            }

            if (ideaActive.not()) {

                val linuxArm32HfpMain by getting {
                    dependsOn(nativeMain)
                }

                val linuxArm32HfpTest by getting {
                    dependsOn(nativeTest)
                }

                val linuxArm64Main by getting {
                    dependsOn(nativeMain)
                }

                val linuxArm64Test by getting {
                    dependsOn(nativeTest)
                }

                val androidNativeX64Main by getting {
                    dependsOn(nativeMain)
                }

                val androidNativeX64Test by getting {
                    dependsOn(nativeTest)
                }

                val androidNativeX86Main by getting {
                    dependsOn(nativeMain)
                }

                val androidNativeX86Test by getting {
                    dependsOn(nativeTest)
                }

                val androidNativeArm32Main by getting {
                    dependsOn(nativeMain)
                }

                val androidNativeArm32Test by getting {
                    dependsOn(nativeTest)
                }

                val androidNativeArm64Main by getting {
                    dependsOn(nativeMain)
                }

                val androidNativeArm64Test by getting {
                    dependsOn(nativeTest)
                }
            }
        }

        val iosX64Main by getting {
            dependsOn(nativeMain)
        }
        val iosX64Test by getting {
            dependsOn(nativeTest)
        }

        val iosArm64Main by getting {
            dependsOn(nativeMain)
        }
        val iosArm64Test by getting {
            dependsOn(nativeTest)
        }

        val macosX64Main by getting {
            dependsOn(nativeMain)
        }
        val macosX64Test by getting {
            dependsOn(nativeTest)
        }

        val tvosMain by getting {
            dependsOn(nativeMain)
        }
        val tvosTest by getting {
            dependsOn(nativeTest)
        }

        val iosSimulatorArm64Main by sourceSets.getting
        val iosSimulatorArm64Test by sourceSets.getting

        iosSimulatorArm64Main.dependsOn(nativeMain)
        iosSimulatorArm64Test.dependsOn(nativeTest)

        val macosArm64Main by sourceSets.getting
        val macosArm64Test by sourceSets.getting

        macosArm64Main.dependsOn(nativeMain)
        macosArm64Test.dependsOn(nativeTest)

        val tvosSimulatorArm64Main by sourceSets.getting
        val tvosSimulatorArm64Test by sourceSets.getting

        tvosSimulatorArm64Main.dependsOn(nativeMain)
        tvosSimulatorArm64Test.dependsOn(nativeTest)

        if (ideaActive.not()) {
            val watchosMain by getting {
                dependsOn(nativeMain)
            }

            val watchosTest by getting {
                dependsOn(nativeTest)
            }

            val watchosSimulatorArm64Main by sourceSets.getting
            val watchosSimulatorArm64Test by sourceSets.getting

            watchosSimulatorArm64Main.dependsOn(nativeMain)
            watchosSimulatorArm64Test.dependsOn(nativeTest)
        }

        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }

        val mingwX64Test by getting {
            dependsOn(nativeTest)
        }

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }
    }
}

tasks {
    val build by named("build")
    build.dependsOn("spotlessCheck")
    build.dependsOn("spotlessKotlinCheck")

    val hostOsName = getHostOsName()

    create<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.get().outputDirectory)
    }

    dokkaHtml {
        println("Dokka !")
        dokkaSourceSets {
        }
    }
    if (hostOsName == primaryDevelopmentOs) {
        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }

//        val jsIrNodeTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//            }
//        }
//
//        val jsIrBrowserTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//            }
//        }
//
//        val jsLegacyNodeTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//            }
//        }
//
//        val jsLegacyBrowserTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//            }
//        }
    }
//
//    if (hostOsName == HostOs.LINUX) {
//        val linuxTest by getting(KotlinNativeTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                // showStandardStreams = true
//            }
//        }
//    }
}

spotless {
    kotlin {
        ktlint()
        target("**/*.kt")
    }
    kotlinGradle {
        ktlint()
        target("**/*.gradle.kts")
    }
}

signing {
    isRequired = false
    sign(publishing.publications)
}

publishing {
    publications.withType(MavenPublication::class) {
        artifact(tasks["javadocJar"])
        pom {
            name.set("Kotlin Multiplatform BigNum")
            description.set("Kotlin Multiplatform BigNum library")
            url.set("https://github.com/KryptonReborn/kotlin-multiplatform-bignum")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("ionspin")
                    name.set("Ugljesa Jovanovic")
                    email.set("opensource@ionspin.com")
                }
            }
            scm {
                url.set("https://github.com/KryptonReborn/kotlin-multiplatform-bignum")
                connection.set("scm:git:git://git@github.com:KryptonReborn/kotlin-multiplatform-bignum.git")
                developerConnection.set("scm:git:ssh://git@github.com:KryptonReborn/kotlin-multiplatform-bignum.git")
            }
        }
    }
    repositories {
        maven {
            uri("https://maven.pkg.github.com/KryptonReborn/kotlin-multiplatform-bignum")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("USERNAME_GITHUB")
                password = findProperty("gpr.token") as String? ?: System.getenv("TOKEN_GITHUB")
            }
        }
    }
}
