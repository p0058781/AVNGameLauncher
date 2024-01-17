plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("desktop")
    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
            }
        }
    }

    jvmToolchain(17)
}
