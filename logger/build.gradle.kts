import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(project(":config"))
                implementation(project(":domain"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.log4j.slf4j)
                implementation(libs.log4j.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.test)
                implementation(libs.turbine)
            }
        }
    }

    jvmToolchain(17)
}
