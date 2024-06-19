plugins {
    kotlin("multiplatform")
}

kotlin {

    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
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
