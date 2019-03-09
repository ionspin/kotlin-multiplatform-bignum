pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
enableFeaturePreview("GRADLE_METADATA")
rootProject.name = "KotlinBigInteger"
include("core")

