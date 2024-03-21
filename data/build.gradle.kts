plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()

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
                implementation(libs.kotlinx.datetime)
                implementation(libs.okio)

                implementation(libs.koin.core)

                implementation(libs.multiplatform.settings.no.arg)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.jsoup)
                implementation(libs.ktor.client.okhttp)
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
        val androidMain by getting {
            dependencies {
                implementation(libs.sqdelight.androiddriver)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.sqdelight.sqlitedriver)
            }
        }
    }

    jvmToolchain(17)
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "org.skynetsoftware.avnlauncher.data"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        buildConfig = true
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("org.skynetsoftware.avnlauncher.data")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}