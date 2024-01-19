plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":sharedApp"))
                implementation(libs.koin.android)
            }
        }
    }
    jvmToolchain(17)
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "org.skynetsoftware.avnlauncher"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "org.skynetsoftware.avnlauncher"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
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

