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

val hostOs = getHostOsName()

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
    jvm()
    js {
        compilations {
            this.forEach {
                it.compileTaskProvider.configure {
                    kotlinOptions.sourceMap = true
                    kotlinOptions.metaInfo = true
                    if (it.name == "main") {
                        kotlinOptions.main = "call"
                    }
                }
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
    linuxX64()
    linuxArm64()
    androidNativeX64()
    androidNativeX86()
    androidNativeArm32()
    androidNativeArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosX64()
    watchosSimulatorArm64()
    mingwX64()
    wasmJs {
        browser()
    }
    wasmWasi()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin(Deps.Common.stdLib))
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(Deps.Common.testCoroutines)
                implementation(kotlin(Deps.Common.testAnnotation))
                implementation(Deps.Common.coroutines)
            }
        }

        jvmMain.dependencies {
            implementation(kotlin(Deps.Jvm.stdLib))
        }
        jvmTest.dependencies {
            implementation(kotlin(Deps.Jvm.test))
            implementation(kotlin(Deps.Jvm.testJUnit))
            implementation(kotlin(Deps.Jvm.reflection))
        }
        jsMain.dependencies {
            implementation(kotlin(Deps.Js.stdLib))
        }
        jsTest.dependencies {
            implementation(kotlin(Deps.Js.test))
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin(Deps.WasmJs.test))
            }
        }

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
            languageSettings.optIn("expect-actual-classes")
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
        dokkaSourceSets {
        }
    }

    val jvmTest by getting(Test::class) {
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    all {
        if (hostOs == HostOs.LINUX) {
            // Linux task dependecies

            // @formatter:off
            if (this.name.equals("signAndroidNativeArm32Publication")) { this.mustRunAfter("signAndroidNativeArm64Publication") }
            if (this.name.equals("signAndroidNativeArm64Publication")) { this.mustRunAfter("signAndroidNativeX64Publication") }
            if (this.name.equals("signAndroidNativeX64Publication")) { this.mustRunAfter("signAndroidNativeX86Publication") }
            if (this.name.equals("signAndroidNativeX86Publication")) { this.mustRunAfter("signJsPublication") }
            if (this.name.equals("signJsPublication")) { this.mustRunAfter("signJvmPublication") }
            if (this.name.equals("signJvmPublication")) { this.mustRunAfter("signKotlinMultiplatformPublication") }
            if (this.name.equals("signKotlinMultiplatformPublication")) { this.mustRunAfter("signLinuxArm64Publication") }
            if (this.name.equals("signLinuxArm64Publication")) { this.mustRunAfter("signLinuxX64Publication") }
            if (this.name.equals("signLinuxX64Publication")) { this.mustRunAfter("signWasmJsPublication") }
            if (this.name.equals("signWasmJsPublication")) { this.mustRunAfter("signWasmWasiPublication") }
            // @formatter:on

            if (this.name.startsWith("publish")) {
                this.mustRunAfter("signAndroidNativeArm32Publication")
                this.mustRunAfter("signAndroidNativeArm64Publication")
                this.mustRunAfter("signAndroidNativeX64Publication")
                this.mustRunAfter("signAndroidNativeX86Publication")
                this.mustRunAfter("signJsPublication")
                this.mustRunAfter("signJvmPublication")
                this.mustRunAfter("signKotlinMultiplatformPublication")
                this.mustRunAfter("signLinuxArm64Publication")
                this.mustRunAfter("signLinuxX64Publication")
                this.mustRunAfter("signWasmJsPublication")
                this.mustRunAfter("signWasmWasiPublication")
            }

            if (this.name.startsWith("compileTest")) {
                this.mustRunAfter("signAndroidNativeArm32Publication")
                this.mustRunAfter("signAndroidNativeArm64Publication")
                this.mustRunAfter("signAndroidNativeX64Publication")
                this.mustRunAfter("signAndroidNativeX86Publication")
                this.mustRunAfter("signJsPublication")
                this.mustRunAfter("signJvmPublication")
                this.mustRunAfter("signKotlinMultiplatformPublication")
                this.mustRunAfter("signLinuxArm64Publication")
                this.mustRunAfter("signLinuxX64Publication")
                this.mustRunAfter("signWasmJsPublication")
                this.mustRunAfter("signWasmWasiPublication")
            }

            if (this.name.startsWith("linkDebugTest")) {
                this.mustRunAfter("signAndroidNativeArm32Publication")
                this.mustRunAfter("signAndroidNativeArm64Publication")
                this.mustRunAfter("signAndroidNativeX64Publication")
                this.mustRunAfter("signAndroidNativeX86Publication")
                this.mustRunAfter("signJsPublication")
                this.mustRunAfter("signJvmPublication")
                this.mustRunAfter("signKotlinMultiplatformPublication")
                this.mustRunAfter("signLinuxArm64Publication")
                this.mustRunAfter("signLinuxX64Publication")
                this.mustRunAfter("signWasmJsPublication")
                this.mustRunAfter("signWasmWasiPublication")
            }
        }

        if (hostOs == HostOs.MAC) {
            // Macos task dependencies
            // @formatter:off
            if (this.name.equals("signIosArm64Publication")) { this.mustRunAfter("signIosSimulatorArm64Publication") }
            if (this.name.equals("signIosSimulatorArm64Publication")) { this.mustRunAfter("signIosX64Publication") }
            if (this.name.equals("signIosX64Publication")) { this.mustRunAfter("signMacosArm64Publication") }
            if (this.name.equals("signMacosArm64Publication")) { this.mustRunAfter("signMacosX64Publication") }
            if (this.name.equals("signMacosX64Publication")) { this.mustRunAfter("signTvosArm64Publication") }
            if (this.name.equals("signTvosArm64Publication")) { this.mustRunAfter("signTvosSimulatorArm64Publication") }
            if (this.name.equals("signTvosSimulatorArm64Publication")) { this.mustRunAfter("signTvosX64Publication") }
            if (this.name.equals("signTvosX64Publication")) { this.mustRunAfter("signWatchosArm32Publication") }
            if (this.name.equals("signWatchosArm32Publication")) { this.mustRunAfter("signWatchosArm64Publication") }
            if (this.name.equals("signWatchosArm64Publication")) { this.mustRunAfter("signWatchosDeviceArm64Publication") }
            if (this.name.equals("signWatchosDeviceArm64Publication")) { this.mustRunAfter("signWatchosSimulatorArm64Publication") }
            if (this.name.equals("signWatchosSimulatorArm64Publication")) { this.mustRunAfter("signWatchosX64Publication") }
            // @formatter:on

            if (this.name.startsWith("publish")) {
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosSimulatorArm64Publication")
                this.mustRunAfter("signIosX64Publication")
                this.mustRunAfter("signMacosArm64Publication")
                this.mustRunAfter("signMacosX64Publication")
                this.mustRunAfter("signTvosArm64Publication")
                this.mustRunAfter("signTvosSimulatorArm64Publication")
                this.mustRunAfter("signTvosX64Publication")
                this.mustRunAfter("signWatchosArm32Publication")
                this.mustRunAfter("signWatchosArm64Publication")
                this.mustRunAfter("signWatchosDeviceArm64Publication")
                this.mustRunAfter("signWatchosSimulatorArm64Publication")
                this.mustRunAfter("signWatchosX64Publication")
            }

            if (this.name.startsWith("compileTest")) {
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosSimulatorArm64Publication")
                this.mustRunAfter("signIosX64Publication")
                this.mustRunAfter("signMacosArm64Publication")
                this.mustRunAfter("signMacosX64Publication")
                this.mustRunAfter("signTvosArm64Publication")
                this.mustRunAfter("signTvosSimulatorArm64Publication")
                this.mustRunAfter("signTvosX64Publication")
                this.mustRunAfter("signWatchosArm32Publication")
                this.mustRunAfter("signWatchosArm64Publication")
                this.mustRunAfter("signWatchosDeviceArm64Publication")
                this.mustRunAfter("signWatchosSimulatorArm64Publication")
            }
            if (this.name.startsWith("linkDebugTest")) {
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosArm64Publication")
                this.mustRunAfter("signIosSimulatorArm64Publication")
                this.mustRunAfter("signIosX64Publication")
                this.mustRunAfter("signMacosArm64Publication")
                this.mustRunAfter("signMacosX64Publication")
                this.mustRunAfter("signTvosArm64Publication")
                this.mustRunAfter("signTvosSimulatorArm64Publication")
                this.mustRunAfter("signTvosX64Publication")
                this.mustRunAfter("signWatchosArm32Publication")
                this.mustRunAfter("signWatchosArm64Publication")
                this.mustRunAfter("signWatchosDeviceArm64Publication")
                this.mustRunAfter("signWatchosSimulatorArm64Publication")
            }
        }

        if (hostOs == HostOs.WINDOWS) {
            if (this.name.startsWith("publish")) {
                this.mustRunAfter("signMingwX64Publication")
            }
            if (this.name.startsWith("compileTest")) {
                this.mustRunAfter("signMingwX64Publication")
            }
            if (this.name.startsWith("linkDebugTest")) {
                this.mustRunAfter("signMingwX64Publication")
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
