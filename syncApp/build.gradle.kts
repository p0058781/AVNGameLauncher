plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    alias(libs.plugins.mosaic)
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
                        attributes["Main-Class"] = "org.skynetsoftware.avnlauncher.sync.MainKt"
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
                implementation(project(":domain"))
                implementation(project(":data"))
                implementation(project(":logger"))

                implementation(libs.koin.core)

                implementation(libs.jline)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

            }
        }
    }
}
