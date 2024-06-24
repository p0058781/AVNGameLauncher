import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
    }

    jvmToolchain(17)
}
