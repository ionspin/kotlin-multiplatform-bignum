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
    jvm()
    js() {
        nodejs()
        browser()
    }
    linuxX64()
    linuxArm32Hfp()
    linuxArm64()
    iosX64()
    iosArm64()
    iosArm32()
    macosX64()
    tvos()
    watchos()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(Deps.Common.bignum)
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
    }
}

tasks {
    val build by named("build")
//    build.dependsOn("spotlessCheck")
//    build.dependsOn("spotlessKotlinCheck")

    val hostOsName = getHostOsName()

    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)
    }

    dokkaJavadoc {
        println("Dokka !")
        dokkaSourceSets {
            create("commonMain") {
                displayName = "common"
                platform = "common"
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
