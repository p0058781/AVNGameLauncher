import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension

plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)

    alias(libs.plugins.jetbrains.compose).apply(false)

    alias(libs.plugins.ktor.plugin).apply(false)
    alias(libs.plugins.ktlint.plugin).apply(false)
    alias(libs.plugins.kover).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.dependencygraphgenerator)
    alias(libs.plugins.buildkonfig).apply(false)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    version = "2.0.0"

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.1.0")
        debug.set(true)
        filter {
            exclude("**/build.gradle.kts")
            exclude("**/BuildKonfig.kt")
        }
    }

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$projectDir/.detekt/detekt.yml")
        baseline = file("$projectDir/.detekt/baseline.xml")
    }

    tasks.withType<Detekt>().configureEach {
        exclude("**/MR.kt")
        exclude("**/GameEntityQueries.kt")
        exclude("**/PlaySessionEntityQueries.kt")
        exclude("**/TotalPlayTime.kt")
        exclude("**/Database.kt")
        exclude("**/DatabaseImpl.kt")
        exclude("**/BuildKonfig.kt")
        exclude("org/skynetsoftware/avnlauncher/app/generated/resources/**/*")
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            md.required.set(false)
            sarif.required.set(false)
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions.allWarningsAsErrors = true
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    configure<KoverReportExtension> {
        filters {
            excludes {
                classes(
                    "org.skynetsoftware.avnlauncher.data.data.DatabaseImpl*",
                    "org.skynetsoftware.avnlauncher.data.GameEntityQueries*",
                    "org.skynetsoftware.avnlauncher.data.GameEntity*",
                    "org.skynetsoftware.avnlauncher.data.TotalPlayTime",
                    "org.skynetsoftware.avnlauncher.data.PlaySessionEntityQueries*",
                    "org.skynetsoftware.avnlauncher.data.PlaySessionEntity*",
                )
            }
        }
    }

}
