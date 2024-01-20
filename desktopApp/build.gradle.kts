import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.skynetsoftware.avnlauncher.packager.Platform
import org.skynetsoftware.avnlauncher.packager.PlatformNativePackageTask
import proguard.gradle.ProGuardTask

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
version = "0.0.1"//TODO make version global (desktop + android)

kotlin {
    jvm {
        withJava()
        compilations {
            val main = getByName("main")
            tasks {
                fun Jar.fatJarCommon() {
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
                    exclude("icudtl.dat")
                }

                fun Jar.excludeUnsupportedPlatforms() {
                    exclude("org/sqlite/native/Linux/arm*/*")
                    exclude("org/sqlite/native/Linux/ppc64/*")
                    exclude("org/sqlite/native/Linux/x86/*")
                    exclude("org/sqlite/native/Linux-Musl/x86/*")
                    exclude("org/sqlite/native/FreeBSD/**")
                    exclude("org/sqlite/native/Linux-Android/**")
                }

                fun Jar.excludeWindowsLibs() {
                    exclude("jni/windows/*")
                    exclude("skiko-windows*.dll")
                    exclude("org/sqlite/native/Windows/**")
                }

                fun Jar.excludeLinuxLibs() {
                    exclude("jni/linux/*")
                    exclude("libskiko-linux*.so")
                    exclude("org/sqlite/native/Linux/**")
                    exclude("org/sqlite/native/Linux-Musl/**")
                }

                fun Jar.excludeMacOSLibs() {
                    exclude("jni/macos/*")
                    exclude("libskiko-macos*.dylib")
                    exclude("org/sqlite/native/Mac/**")
                }

                fun ProGuardTask.config(taskSuffix: String, fileNameSuffix: String) {
                    outputs.upToDateWhen { false }
                    dependsOn("fatJar$taskSuffix")
                    libraryjars(
                        mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
                        File("${System.getProperty("java.home")}/jmods/").listFiles().filter { it.isFile }.filter { it.extension == "jmod" }
                    )
                    injars("build/libs/desktopApp-fat$fileNameSuffix-$version.jar")
                    outjars("build/libs/desktopApp-release-fat$fileNameSuffix-$version.jar")
                    configuration("proguard-rules.pro")
                    printmapping("build/proguard/mapping.txt")
                }
                register<Jar>("fatJar") {
                    fatJarCommon()
                    archiveBaseName.set("${project.name}-fat")
                }
                register<Jar>("fatJarWinX64") {
                    fatJarCommon()
                    excludeUnsupportedPlatforms()
                    excludeLinuxLibs()
                    excludeMacOSLibs()
                    archiveBaseName.set("${project.name}-fat-windows-x64")
                }
                register<Jar>("fatJarLinuxX64") {
                    fatJarCommon()
                    excludeUnsupportedPlatforms()
                    excludeWindowsLibs()
                    excludeMacOSLibs()
                    exclude("libskiko-linux-arm64.so")
                    exclude("org/sqlite/native/Linux/aarch64/*")
                    exclude("org/sqlite/native/Linux-Musl/aarch64/*")
                    archiveBaseName.set("${project.name}-fat-linux-x64")
                }
                register<Jar>("fatJarLinuxArm64") {
                    fatJarCommon()
                    excludeUnsupportedPlatforms()
                    excludeWindowsLibs()
                    excludeMacOSLibs()
                    exclude("libskiko-linux-x64.so")
                    exclude("org/sqlite/native/Linux/x86_64/*")
                    exclude("org/sqlite/native/Linux-Musl/x86_64/*")
                    archiveBaseName.set("${project.name}-fat-linux-arm64")
                }
                register<Jar>("fatJarMacOSX64") {
                    fatJarCommon()
                    excludeUnsupportedPlatforms()
                    excludeWindowsLibs()
                    excludeLinuxLibs()
                    exclude("libskiko-macos-arm64.dylib")
                    exclude("org/sqlite/native/Mac/aarch64/*")
                    archiveBaseName.set("${project.name}-fat-macos-x64")
                }
                register<Jar>("fatJarMacOSArm64") {
                    fatJarCommon()
                    excludeUnsupportedPlatforms()
                    excludeWindowsLibs()
                    excludeLinuxLibs()
                    exclude("libskiko-macos-x64.dylib")
                    exclude("org/sqlite/native/Mac/x86_64/*")
                    archiveBaseName.set("${project.name}-fat-macos-arm64")
                }

                register<ProGuardTask>("releaseJar") {
                    config("", "")
                }

                register<ProGuardTask>("releaseJarWinX64") {
                    config("WinX64", "-windows-x64")
                }

                register<ProGuardTask>("releaseJarLinuxX64") {
                    config("LinuxX64", "-linux-x64")
                }

                register<ProGuardTask>("releaseJarLinuxArm64") {
                    config("LinuxArm64", "-linux-arm64")
                }

                register<ProGuardTask>("releaseJarMacOSX64") {
                    config("MacOSX64", "-macos-x64")
                }

                register<ProGuardTask>("releaseJarMacOSArm64") {
                    config("MacOSArm64", "-macos-arm64")
                }

                register<PlatformNativePackageTask>("packageWinX64") {
                    dependsOn("releaseJarWinX64")
                    platform = Platform.WindowsX64
                }

                register<PlatformNativePackageTask>("packageLinuxX64") {
                    dependsOn("releaseJarLinuxX64")
                    platform = Platform.LinuxX64
                }

                register<PlatformNativePackageTask>("packageLinuxArm64") {
                    dependsOn("releaseJarLinuxArm64")
                    platform = Platform.LinuxArm64
                }

                register<PlatformNativePackageTask>("packageMacOSX64") {
                    dependsOn("releaseJarMacOSX64")
                    platform = Platform.MacOSX64
                }

                register<PlatformNativePackageTask>("packageMacOSArm64") {
                    dependsOn("releaseJarMacOSArm64")
                    platform = Platform.MacOSArm64
                }

            }
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.macos_x64)
                implementation(compose.desktop.macos_arm64)
                implementation(compose.desktop.windows_x64)
                implementation(compose.desktop.linux_arm64)
                implementation(compose.desktop.linux_x64)
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
    }
}
