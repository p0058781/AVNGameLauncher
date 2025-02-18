rootProject.name = "avn-game-launcher"

include(":app")
include(":domain")
include(":data")
include(":logger")
include(":config")
include(":file-picker")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

dependencyResolutionManagement {
    defaultLibrariesExtensionName = "libs"
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        mavenLocal()
    }
}
