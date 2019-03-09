
val kotlin_version: String by extra

buildscript {
    
    repositories {
        mavenCentral()
        google()
        maven  ("https://kotlin.bintray.com/kotlinx")
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath ("gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:5.0.0")
    }
}

allprojects {

    repositories {
        mavenCentral()
        google()
        maven ("https://kotlin.bintray.com/kotlinx")
        jcenter()
    }
}

group = "com.ionspin.kotlin.biginteger"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

repositories {
    mavenCentral()
}
