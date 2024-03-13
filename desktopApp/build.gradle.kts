buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.4.1")
    }
}

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "org.skynetsoftware.avnlauncher"

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.cli)
                implementation(project(":sharedApp"))
                implementation(project(":config"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs("--add-opens", "java.desktop/sun.awt.X11=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/sun.awt.wl=ALL-UNNAMED")
        jvmArgs("-Dapple.awt.application.appearance=system")

        nativeDistributions {
            modules("java.sql", "java.management")
            packageName = "AVN Game Launcher"
            licenseFile.set(project.rootProject.file("LICENSE.txt"))
            val resourcesDir = rootProject.project("sharedApp").file("src/commonMain/resources")
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
