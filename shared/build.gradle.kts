import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("io.realm.kotlin")
    alias(libs.plugins.moko.resource.generator)
}

kotlin {
    androidTarget()

    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.realm.librarybase)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.okio)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                implementation(libs.image.loader)
                implementation(libs.image.loader.extension.blur)
                implementation(libs.slf4j.simple)

                implementation(libs.multiplatform.settings.no.arg)

                implementation(libs.mvvm.compose)
                implementation(libs.mvvm.flow.compose)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                api(libs.moko.resources)
                api(libs.moko.resources.compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.turbine)
                implementation(libs.moko.resources.test)
            }
        }
        val jvmMain = create("jvmMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.jsoup)
                implementation(libs.ktor.client.okhttp)
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)
                implementation(libs.koin.android)
                implementation(libs.accompanist.drawablepainter)
            }
        }
        val desktopMain by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation(compose.desktop.common)

                implementation(libs.mpfilepicker)
            }
        }
    }

    jvmToolchain(17)
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "org.skynetsoftware.avnlauncher.common"

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
    flavorDimensions += "mode"
    productFlavors {
        create("remoteClient") {
            dimension = "mode"
        }
        create("full") {
            dimension = "mode"
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "org.skynetsoftware.avnlauncher"
}


