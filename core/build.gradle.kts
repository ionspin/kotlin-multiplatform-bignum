import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin(PluginsDeps.multiplatform)    
    id (PluginsDeps.mavenPublish)
    id (PluginsDeps.node) version Versions.nodePlugin
}




repositories {
    mavenCentral()
}
group = "com.ionspin.kotlin.biginteger.core"
version = "0.0.2"

kotlin {
    jvm()
    js() {
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
        }
    }
    linuxX64("linux")
    iosX64("ios") {
        compilations["main"].outputKinds("framework")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(kotlin(Deps.Common.test))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin(Deps.Jvm.stdLib))
                implementation(kotlin(Deps.Jvm.test))
                implementation(kotlin(Deps.Jvm.testJUnit))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin(Deps.Jvm.test))
                implementation(kotlin(Deps.Jvm.testJUnit))
                implementation(Deps.Jvm.oshi)
                implementation(Deps.Jvm.coroutinesTest)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin(Deps.Js.stdLib))
                implementation(kotlin(Deps.Js.test))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val iosMain by getting {
            dependencies {
            }
        }
        val iosTest by getting {
        }

        val nativeMain by creating {
            dependencies {
            }
        }
        val nativeTest by creating {
            
        }

        val linuxMain by getting {
            dependsOn(nativeMain)
        }
        val linuxTest by getting {
            dependsOn(nativeTest)
        }
    }


}




task<Copy>("copyPackageJson") {
    dependsOn("compileKotlinJs")
    println("Copying package.json from $projectDir/core/src/jsMain/npm")
    from ("$projectDir/src/jsMain/npm")
    println("Node modules dir ${node.nodeModulesDir}")
    into ("${node.nodeModulesDir}")
}

tasks {
    val npmInstall by getting
    val compileKotlinJs by getting(AbstractCompile::class)
    val compileTestKotlinJs by getting(Kotlin2JsCompile::class)
    val jsTest by getting

    val populateNodeModulesForTests by creating {
        dependsOn(npmInstall, compileKotlinJs, compileTestKotlinJs)
        doLast {
            copy {
                from(compileKotlinJs.destinationDir)
                configurations["jsRuntimeClasspath"].forEach {
                    from(zipTree(it.absolutePath).matching { include("*.js") })
                }
                configurations["jsTestRuntimeClasspath"].forEach {
                    from(zipTree(it.absolutePath).matching { include("*.js") })
                }

                into("$projectDir/node_modules")
            }
        }
    }


    val runTestsWithMocha by creating(NodeTask::class) {
        dependsOn(populateNodeModulesForTests)
        setScript(file("$projectDir/node_modules/mocha/bin/mocha"))
        setArgs(listOf(
            compileTestKotlinJs.outputFile,
            "--reporter-options",
            "topLevelSuite=${project.name}-tests"
        ))
    }

    jsTest.dependsOn("copyPackageJson")
    jsTest.dependsOn(runTestsWithMocha)

}


