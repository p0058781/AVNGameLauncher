import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()
        compilations {
            val main = getByName("main")
            tasks {
                register<Jar>("fatJar") {
                    group = "application"
                    dependsOn(build)
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    manifest {
                        attributes["Main-Class"] = "MainKt"
                        attributes["Add-Opens"] = "java.desktop/sun.awt.X11=ALL-UNNAMED"
                    }
                    from(
                        configurations.getByName("runtimeClasspath").map { if (it.isDirectory) it else zipTree(it) },
                        main.output.classesDirs
                    )
                    archiveBaseName.set("${project.name}-fat")
                }
            }
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":sharedApp"))
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

        buildTypes.release.proguard {
            obfuscate.set(true)
            optimize.set(true)
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "avn-launcher-v3"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("../sharedApp/src/commonMain/resources")
            macOS {
                packageName = "AVN Game Launcher"
                iconFile.set(iconsRoot.resolve("icon.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon.ico"))
                upgradeUuid = "822cc90d-718b-4087-b337-bb203005f9ad"
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}
