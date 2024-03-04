package org.skynetsoftware.avnlauncher.packager

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.UnixStat
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.tools.ant.types.ArchiveFileSet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.Path

abstract class PlatformNativePackageTask : DefaultTask() {

    @get:Input
    abstract var platform: Platform

    private val templatesDir = project.file("package/templates")
    private lateinit var outDir: File

    @TaskAction
    fun createPlatformNativePackage() {
        outDir = File(project.file("build/package"), platform.toString())
        outDir.mkdirs()

        val jdkZip = downloadJdkZip()
        val jmodsDir = extractJmods(jdkZip)
        val usedMods = getUsedMods()
        val runtimeDir = createRuntime(jmodsDir, usedMods)
        val zipFile = createZip(runtimeDir)
        println("Package created: $zipFile")
    }

    private fun downloadJdkZip(): File {
        val url = platform.jdkDownloadUrl()
        val jdkZipFile = File(outDir, Paths.get(url.file).fileName.toString())

        if (!jdkZipFile.exists()) {
            url.openStream().use {
                Files.copy(it, Paths.get(jdkZipFile.absolutePath))
            }
        }

        return jdkZipFile
    }

    private fun extractJmods(jdkZip: File): File {
        val output = File(outDir, "jmods")
        if (output.exists() && output.list()?.isNotEmpty()==true) {
            return output
        }
        output.mkdirs()
        BufferedInputStream(jdkZip.inputStream()).use { inputStream ->
            if (platform==Platform.WindowsX64) {
                ZipArchiveInputStream(inputStream)
            } else {
                TarArchiveInputStream(
                    GzipCompressorInputStream(
                        inputStream
                    )
                )
            }.use { tar ->
                var entry: ArchiveEntry?
                while ((tar.nextEntry.also { entry = it })!=null) {
                    entry?.let {
                        val name = Path(it.name).fileName.toString()
                        val extractTo: Path = output.resolve(name).toPath()

                        if (name.endsWith(".jmod")) {
                            Files.copy(tar, extractTo)
                        }
                    }
                }
            }
        }
        return output
    }

    private fun getUsedMods(): List<String> {
        val jarFile = project.file("build/libs/${jarFileName()}")
        val inputStream = Runtime.getRuntime()
            .exec(arrayOf("jdeps", "--print-module-deps", "--ignore-missing-deps", jarFile.absolutePath)).inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val modsString = reader.readText().trim()
        return modsString.split(",").toMutableList().apply { if (!contains("jdk.crypto.ec")) { add("jdk.crypto.ec") } }.toList()
    }

    private fun jarFileName(): String {
        return "desktopApp-release-fat-${platform}-${project.version}.jar"
    }

    private fun createRuntime(jmodsDir: File, usedMods: List<String>): File {
        val output = File(outDir, "runtime")
        output.deleteRecursively()
        val process = Runtime.getRuntime().exec(arrayOf("jlink",
            "--no-header-files",
            "--no-man-pages",
            "--compress=2",
            "--strip-debug",
            "--module-path",
            jmodsDir.absolutePath,
            "--add-modules",
            usedMods.joinToString(",") { it },
            "--output",
            output.absolutePath
        )
        )
        val exitCode = process.waitFor()
        if (exitCode!=0) {
            println(process.inputStream.bufferedReader().readText())
            throw RuntimeException("jlink exit code: $exitCode")
        }
        return output
    }

    private fun createZip(runtimeDir: File): File {
        val zipFile = File(outDir, "release-${platform}-${project.version}.zip")
        BufferedOutputStream(zipFile.outputStream()).use { outputStream ->
            ZipArchiveOutputStream(outputStream).use { zipArchiveOutputStream ->
                val jPackageXmlContent = createJPackageXml()
                val cfgFileContent = createCfg()
                addDirToZip(zipArchiveOutputStream, File(templatesDir, platform.toString()))
                when (platform) {
                    Platform.LinuxX64,
                    Platform.LinuxArm64 -> {
                        addDirToZip(zipArchiveOutputStream, runtimeDir, "lib/runtime")
                        addFileToZip(
                            zipArchiveOutputStream,
                            project.file("build/libs/${jarFileName()}"),
                            "lib/app/${jarFileName()}"
                        )
                        writeStringToZip(zipArchiveOutputStream, jPackageXmlContent, "lib/app/.jpackage.xml")
                        writeStringToZip(zipArchiveOutputStream, cfgFileContent, "lib/app/AVN Game Launcher.cfg")
                    }

                    Platform.WindowsX64 -> {
                        addDirToZip(zipArchiveOutputStream, runtimeDir, "runtime")
                        addFileToZip(
                            zipArchiveOutputStream,
                            project.file("build/libs/${jarFileName()}"),
                            "app/${jarFileName()}"
                        )
                        writeStringToZip(zipArchiveOutputStream, jPackageXmlContent, "app/.jpackage.xml")
                        writeStringToZip(zipArchiveOutputStream, cfgFileContent, "app/AVN Game Launcher.cfg")
                    }

                    Platform.MacOSX64,
                    Platform.MacOSArm64 -> {
                        addDirToZip(
                            zipArchiveOutputStream,
                            runtimeDir,
                            "AVN Game Launcher.app/Contents/runtime/Contents/Home"
                        )
                        addFileToZip(
                            zipArchiveOutputStream,
                            project.file("build/libs/${jarFileName()}"),
                            "AVN Game Launcher.app/Contents/app/${jarFileName()}"
                        )
                        writeStringToZip(
                            zipArchiveOutputStream,
                            jPackageXmlContent,
                            "AVN Game Launcher.app/Contents/app/.jpackage.xml"
                        )
                        writeStringToZip(
                            zipArchiveOutputStream,
                            cfgFileContent,
                            "AVN Game Launcher.app/Contents/app/AVN Game Launcher.cfg"
                        )
                    }
                }
            }
        }
        return zipFile
    }

    private fun createCfg(): String {
        val cfgString = File(templatesDir, "AVN Game Launcher.cfg").readText()
        return cfgString
            .replace("{version}", project.version.toString())
            .replace("{jar}", jarFileName())
    }

    private fun createJPackageXml(): String {
        val jdkTemplateString = File(templatesDir, ".jpackage.xml").readText()
        return jdkTemplateString
            .replace("{version}", project.version.toString())
            .replace("{jdkVersion}", platform.jdkVersion())
            .replace("{platform}", platform.jPackageString())
    }

    private fun addDirToZip(zipArchiveOutputStream: ZipArchiveOutputStream, dir: File, target: String = "") {
        dir.walk().filter { it.isFile }.forEach {
            val relativePath = target + it.absolutePath.replace(dir.absolutePath, "").let {
                if(target.isEmpty()) {
                    it.trimStart(File.separatorChar)
                } else {
                  it
                }
            }
            addFileToZip(zipArchiveOutputStream, it, relativePath)
        }
    }

    private fun writeStringToZip(zipArchiveOutputStream: ZipArchiveOutputStream, content: String, name: String) {
        val tmpFile = File(System.getProperty("java.io.tmpdir"), System.currentTimeMillis().toString())
        tmpFile.writeText(content)
        val entry = ZipArchiveEntry(tmpFile, name)
        zipArchiveOutputStream.putArchiveEntry(entry)
        tmpFile.inputStream().copyTo(zipArchiveOutputStream)
        zipArchiveOutputStream.closeArchiveEntry()
        tmpFile.delete()
    }

    private fun addFileToZip(zipArchiveOutputStream: ZipArchiveOutputStream, file: File, name: String) {
        val entry = ZipArchiveEntry(file, name)
        zipArchiveOutputStream.putArchiveEntry(entry)
        if (platform==Platform.LinuxX64 || platform==Platform.LinuxArm64 || platform==Platform.MacOSX64 || platform == Platform.MacOSArm64) {
            keepPosixPermissions(file, entry)
        }
        file.inputStream().copyTo(zipArchiveOutputStream)
        zipArchiveOutputStream.closeArchiveEntry()
    }

    private fun keepPosixPermissions(file: File, entry: ZipArchiveEntry) {
        val permissionsSet = Files.getPosixFilePermissions(file.toPath())

        var permissions = 0

        permissionsSet.forEach {
            permissions = when (it) {
                PosixFilePermission.OWNER_READ -> permissions or 0b100000000
                PosixFilePermission.OWNER_WRITE -> permissions or 0b010000000
                PosixFilePermission.OWNER_EXECUTE -> permissions or 0b001000000
                PosixFilePermission.GROUP_READ -> permissions or 0b000100000
                PosixFilePermission.GROUP_WRITE -> permissions or 0b000010000
                PosixFilePermission.GROUP_EXECUTE -> permissions or 0b000001000
                PosixFilePermission.OTHERS_READ -> permissions or 0b000000100
                PosixFilePermission.OTHERS_WRITE -> permissions or 0b000000010
                PosixFilePermission.OTHERS_EXECUTE -> permissions or 0b000000001
            }
        }

        permissions = UnixStat.FILE_FLAG or permissions

        entry.unixMode = permissions
    }


}