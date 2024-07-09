import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    alias(libs.plugins.buildkonfig)
}

group = "org.skynetsoftware.avnlauncher"

kotlin {

    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(project(":server"))
                implementation(project(":data"))
                implementation(project(":domain"))
                implementation(project(":logger"))
                implementation(project(":config"))
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

                implementation(libs.dokar3.chiptextfield)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.cli)
                implementation(libs.mpfilepicker)
                implementation(libs.systemtray)
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
    }

    jvmToolchain(17)
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
