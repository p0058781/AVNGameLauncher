package org.skynetsoftware.avnlauncher.packager

import java.net.URL


private val URL_JDK_LINUX_X64 =
    URL("https://corretto.aws/downloads/resources/17.0.9.8.1/amazon-corretto-17.0.9.8.1-linux-x64.tar.gz")
private val URL_JDK_LINUX_AARCH64 =
    URL("https://corretto.aws/downloads/resources/17.0.9.8.1/amazon-corretto-17.0.9.8.1-linux-aarch64.tar.gz")
private val URL_JDK_WINDOWS_x64 =
    URL("https://corretto.aws/downloads/resources/17.0.9.8.1/amazon-corretto-17.0.9.8.1-windows-x64-jdk.zip")
private val URL_JDK_MACOS_x64 =
    URL("https://corretto.aws/downloads/resources/17.0.9.8.1/amazon-corretto-17.0.9.8.1-macosx-x64.tar.gz")
private val URL_JDK_MACOS_AARCH64 =
    URL("https://corretto.aws/downloads/resources/17.0.9.8.1/amazon-corretto-17.0.9.8.1-macosx-aarch64.tar.gz")

enum class Platform {
    LinuxX64, LinuxArm64, WindowsX64, MacOSX64, MacOSArm64;

    override fun toString(): String {
        return stringValue()
    }
}

fun Platform.stringValue(): String {
    return when(this) {
        Platform.LinuxX64 -> "linux-x64"
        Platform.LinuxArm64 -> "linux-arm64"
        Platform.WindowsX64 -> "windows-x64"
        Platform.MacOSX64 -> "macos-x64"
        Platform.MacOSArm64 -> "macos-arm64"
    }
}

fun Platform.jPackageString(): String {
    return when(this) {
        Platform.LinuxX64,
        Platform.LinuxArm64 -> "linux"
        Platform.WindowsX64 -> "windows"
        Platform.MacOSX64,
        Platform.MacOSArm64 -> "macOS"
    }
}

fun Platform.jdkDownloadUrl(): URL {
    return when (this) {
        Platform.LinuxX64 -> URL_JDK_LINUX_X64
        Platform.LinuxArm64 -> URL_JDK_LINUX_AARCH64
        Platform.WindowsX64 -> URL_JDK_WINDOWS_x64
        Platform.MacOSX64 -> URL_JDK_MACOS_x64
        Platform.MacOSArm64 -> URL_JDK_MACOS_AARCH64
    }
}

fun Platform.jdkVersion(): String {
    return "17.0.9"
}