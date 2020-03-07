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

import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.node) version Versions.nodePlugin
    id(PluginsDeps.dokka) version Versions.dokkaPlugin
    id(PluginsDeps.spotless) version PluginsDeps.Versions.spotlessVersion
}

val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots/"

val sonatypePassword: String? by project

val sonatypeUsername: String? by project

val sonatypePasswordEnv: String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv: String? = System.getenv()["SONATYPE_USERNAME"]

repositories {
    mavenCentral()
    jcenter()
}
group = "com.ionspin.kotlin"
version = "0.1.6-SNAPSHOT"

val ideaActive = System.getProperty("idea.active") == "true"

fun getHostOsName(): String {
    val target = System.getProperty("os.name")
    if (target == "Linux") return "linux"
    if (target.startsWith("Windows")) return "windows"
    if (target.startsWith("Mac")) return "macos"
    return "unknown"
}

kotlin {

    val hostOsName = getHostOsName()
    println("Host os name $hostOsName")

    if (ideaActive) {
        when (hostOsName) {
            "linux" -> linuxX64("native")
            "macos" -> macosX64("native")
            "windows" -> mingwX64("native")
        }
    }

    if (hostOsName == "linux") {
        jvm()
        js {
            compilations {
                this.forEach {
                    it.compileKotlinTask.kotlinOptions.sourceMap = true
                    it.compileKotlinTask.kotlinOptions.moduleKind = "commonjs"
                    it.compileKotlinTask.kotlinOptions.metaInfo = true

                    if (it.name == "main") {
                        it.compileKotlinTask.kotlinOptions.main = "call"
                    }
                    println("Compilation name ${it.name} set")
                    println("Destination dir ${it.compileKotlinTask.destinationDir}")
                }
                nodejs()
                browser() {
                    testTask {
                        useKarma {
                            usePhantomJS()
                        }
                    }
                }
            }
        }

        linuxX64("linux") {
            binaries {
                staticLib {
                }
            }
        }

        linuxArm32Hfp() {
            binaries {
                staticLib {
                }
            }
        }

        linuxArm64() {
            binaries {
                staticLib {
                }
            }
        }
    }

    iosX64("ios") {
        binaries {
            framework {
            }
        }
    }
    iosArm64("ios64Arm") {
        binaries {
            framework {
            }
        }
    }

    iosArm32("ios32Arm") {
        binaries {
            framework {
            }
        }
    }
    macosX64() {
        binaries {
            framework {
            }
        }
    }

    mingwX64() {
        binaries {
            staticLib {
            }
        }
    }

    mingwX86() {
        binaries {
            staticLib {
            }
        }
    }

    println(targets.names)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(Deps.Common.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
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
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeTest
        } else {
            val nativeTest by creating {
                dependsOn(commonTest)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeTest
        }

        if (hostOsName == "linux") {
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.stdLib))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(Deps.Jvm.coroutinesTest)
                    implementation(kotlin(Deps.Jvm.reflection))
                    implementation(Deps.Jvm.coroutinesCore)
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.stdLib))
                    implementation(Deps.Js.coroutines)
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                    implementation(Deps.Js.coroutines)
                }
            }

            val linuxMain by getting {
                dependsOn(nativeMain)
            }
            val linuxTest by getting {
                dependsOn(nativeTest)
            }

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
        }

        val iosMain by getting {
            dependsOn(nativeMain)
        }
        val iosTest by getting {
            dependsOn(nativeTest)
        }

        val ios64ArmMain by getting {
            dependsOn(nativeMain)
        }
        val ios64ArmTest by getting {
            dependsOn(nativeTest)
        }

        val ios32ArmMain by getting {
            dependsOn(nativeMain)
        }
        val ios32ArmTest by getting {
            dependsOn(nativeTest)
        }

        val macosX64Main by getting {
            dependsOn(nativeMain)
        }
        val macosX64Test by getting {
            dependsOn(nativeTest)
        }

        val mingwX86Main by getting {
            dependsOn(nativeMain)
        }

        val mingwX86Test by getting {
            dependsOn(nativeTest)
        }

        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }

        val mingwX64Test by getting {
            dependsOn(nativeTest)
        }
    }
}

tasks {
    val build by named("build")
    build.dependsOn("spotlessCheck")
    build.dependsOn("spotlessKotlinCheck")

    val hostOsName = getHostOsName()

    create<Jar>("javadocJar") {
        dependsOn(dokka)
        archiveClassifier.set("javadoc")
        from(dokka.get().outputDirectory)
    }

    dokka {
        println("Dokka !")
        multiplatform {
        }
    }
    if (hostOsName == "linux") {
        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

        val linuxTest by getting(KotlinNativeTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                // showStandardStreams = true
            }
        }
    }
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
            url.set("https://github.com/ionspin/kotlin-multiplatform-bignum")
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
                url.set("https://github.com/ionspin/kotlin-multiplatform-bignum")
                connection.set("scm:git:git://git@github.com:ionspin/kotlin-multiplatform-bignum.git")
                developerConnection.set("scm:git:ssh://git@github.com:ionspin/kotlin-multiplatform-bignum.git")
            }
        }
    }
    repositories {
        maven {

            url = uri(sonatypeStaging)
            credentials {
                username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                password = sonatypePassword ?: sonatypePasswordEnv ?: ""
            }
        }

        maven {
            name = "snapshot"
            url = uri(sonatypeSnapshots)
            credentials {
                username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                password = sonatypePassword ?: sonatypePasswordEnv ?: ""
            }
        }
    }
}
