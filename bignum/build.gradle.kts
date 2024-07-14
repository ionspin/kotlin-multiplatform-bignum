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
    if (hostOsName == primaryDevelopmentOs) {
        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
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
