import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin(PluginsDeps.multiplatform)
    kotlin(PluginsDeps.kotlinxSerialization) version PluginsDeps.Versions.kotlinxSerialization
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.dokka)
//    id(PluginsDeps.spotless) version PluginsDeps.Versions.spotlessVersion
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

group = "com.ionspin.kotlin"
version = projectVersion

fun getHostOsName(): HostOs {
    val target = System.getProperty("os.name")
    if (target == "Linux") return HostOs.LINUX
    if (target.startsWith("Windows")) return HostOs.WINDOWS
    if (target.startsWith("Mac")) return HostOs.MAC
    throw GradleException("Unknown OS: $target")
}

//spotless {
//    kotlin {
//        ktlint()
//        target("**/*.kt")
//    }
//    kotlinGradle {
//        ktlint()
//        target("**/*.gradle.kts")
//    }
//}

signing {
    isRequired = false
    sign(publishing.publications)
}

kotlin {

    val hostOs = getHostOsName()
    if (hostOs == primaryDevelopmentOs) {
        jvm()
        js(IR) {
            /* IR only for now as legacy fails with the following exception:
        TypeError: tmp$.serializer is not a function
            at <global>.compiledSerializerImpl(/tmp/_karma_webpack_695475/commons.js:72072)
            at <global>.serializerOrNull(/tmp/_karma_webpack_695475/commons.js:72052)
            at <global>.serializerByKTypeImpl(/tmp/_karma_webpack_695475/commons.js:71975)
            at <global>.serializer(/tmp/_karma_webpack_695475/commons.js:71947)
            at com.ionspin.kotlin.bignum.serialization.kotlinx.HumanReadableSerializationTest.testSerialization(/tmp/_karma_webpack_695475/commons.js:88247)
            at <global>.<unknown>(/tmp/_karma_webpack_695475/commons.js:88269)
            at Context.<anonymous>(/tmp/_karma_webpack_695475/commons.js:57)
         */
            nodejs()
            browser()
        }
    }
    if (hostOs == HostOs.LINUX) {
        linuxX64()
//    linuxArm32Hfp() Not supported by kotlinx serialization
//    linuxArm64() Not supported by kotlinx serialization
    }
    iosX64()
    iosArm64()
    iosArm32()
    macosX64()
    tvos()
    watchos()
//    mingwX86() Not supported by kotlinx serialization
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(project(Deps.Project.bignum))
                implementation(Deps.Common.kotlinxSerialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
                implementation(Deps.Common.coroutinesMT)
            }
        }

        if (hostOs == primaryDevelopmentOs) {
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(kotlin(Deps.Jvm.reflection))
                }
            }

            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                }
            }
        }


    }
}

tasks {
    val build by named("build")
//    build.dependsOn("spotlessCheck")
//    build.dependsOn("spotlessKotlinCheck")

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

    val jvmTest by getting(Test::class) {
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
        }
    }

        val jsNodeTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

        val jsBrowserTest by getting(org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

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

    if (hostOsName == HostOs.LINUX) {
        val linuxX64Test by getting(KotlinNativeTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                // showStandardStreams = true
            }
        }
    }
}

publishing {
    publications.withType(MavenPublication::class) {
        artifact(tasks["javadocJar"])
        pom {
            name.set("Kotlin Multiplatform BigNum kotlinx serialization")
            description.set("Kotlin Multiplatform BigNum serialization modules for kotlinx serialization")
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
