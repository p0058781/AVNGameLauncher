import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.moko.resource.generator)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget()

    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(project(":data"))
                api(project(":domain"))
                api(project(":logger"))
                api(project(":config"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)

                api(libs.koin.core)
                api(libs.koin.compose)

                implementation(libs.image.loader)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)

                api(libs.moko.resources.asProvider().get().let { "${it.module}:${it.versionConstraint.requiredVersion}" }) {
                    exclude(group = "com.ibm.icu", module = "icu4j")
                }
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
}

multiplatformResources {
    multiplatformResourcesPackage = "org.skynetsoftware.avnlauncher"
}

buildkonfig {
    packageName = "org.skynetsoftware.avnlauncher"

    defaultConfigs {
        buildConfigField(STRING, "version", version.toString())
    }
}


