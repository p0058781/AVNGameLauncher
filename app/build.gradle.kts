import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("org.jetbrains.compose")
    alias(libs.plugins.buildkonfig)
}

group = "org.skynetsoftware.avnlauncher"

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
                implementation(libs.mvvm.compose)
                implementation(libs.mvvm.flow.compose)

                implementation(libs.image.loader)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.turbine)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)
                implementation(libs.androidx.work.runtimektx)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.accompanist.drawablepainter)
                implementation(libs.androidx.navigation.compose)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.cli)
                implementation(libs.mpfilepicker)
            }
        }
    }

    jvmToolchain(17)
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "org.skynetsoftware.avnlauncher"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.skynetsoftware.avnlauncher"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = project.version.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    packagingOptions.resources {
        excludes += "META-INF/AL2.0"
        excludes += "META-INF/LGPL2.1"
    }
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = "@vng@m31@unch3r"
            keyAlias = "avngamelauncher"
            keyPassword = "@vng@m31@unch3r"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

buildkonfig {
    packageName = "org.skynetsoftware.avnlauncher"

    defaultConfigs {
        buildConfigField(STRING, "version", version.toString())
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("--add-opens", "java.desktop/sun.awt.X11=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/sun.awt.wl=ALL-UNNAMED")
        jvmArgs("-Dapple.awt.application.appearance=system")

        nativeDistributions {
            modules("java.sql", "java.management", "jdk.unsupported")
            packageName = "AVN Game Launcher"
            licenseFile.set(project.rootProject.file("LICENSE.txt"))
            val resourcesDir = project.file("src/commonMain/resources")
            macOS {
                iconFile.set(File(resourcesDir, "icon.icns"))
            }
            windows {
                iconFile.set(File(resourcesDir, "icon.ico"))
            }
            linux {
                iconFile.set(File(resourcesDir, "icon.png"))
            }
            buildTypes.release.proguard {
                configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
    }
}

tasks {
    withType<org.gradle.jvm.tasks.Jar> {
        manifest {
            attributes["Main-Class"] = "MainKt"
            attributes["Add-Opens"] = "java.desktop/sun.awt.X11 java.desktop/sun.awt.wl"
        }
    }
}
