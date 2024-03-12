rootProject.name = "AVN Launcher V3"

include(":androidApp")
include(":sharedApp")
include(":desktopApp")
include(":domain")
include(":data")
include(":logger")
include(":config")
include(":syncApp")

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
    }
}
