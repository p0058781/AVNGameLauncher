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
                        attributes["Add-Opens"] = "java.desktop/sun.awt.X11"
                    }
                    from(configurations.getByName("runtimeClasspath").map { if (it.isDirectory) it else zipTree(it) }, main.output.classesDirs)
                    archiveBaseName.set("${project.name}-fat")
                }
            }
        }
    }
    sourceSets {
        val jvmMain by getting  {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":shared"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "avn-launcher-v3"
            packageVersion = "1.0.0"
            jvmArgs(
                "-Dapple.awt.application.appearance=system"
            )
        }
    }
}
