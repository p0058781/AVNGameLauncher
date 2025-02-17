plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":logger"))
                implementation(project(":config"))
                implementation(libs.sqdelight.coroutines)
                implementation(libs.sqdelight.primitiveadapters)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.okio)

                implementation(libs.koin.core)

                implementation(libs.multiplatform.settings.no.arg)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.contentnegotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.jsoup)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqdelight.sqlitedriver)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.turbine)
                implementation(libs.mockk)
            }
        }
    }

    jvmToolchain(17)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("org.skynetsoftware.avnlauncher.data")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}