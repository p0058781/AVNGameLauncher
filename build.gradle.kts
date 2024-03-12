import com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Style
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)


    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)

    alias(libs.plugins.jetbrains.compose).apply(false)

    alias(libs.plugins.ktor.plugin).apply(false)
    alias(libs.plugins.ktlint.plugin).apply(false)
    alias(libs.plugins.moko.resource.generator).apply(false)
    alias(libs.plugins.kover).apply(false)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.dependencygraphgenerator)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.1.0")
        debug.set(true)
        filter {
            exclude("**/build.gradle.kts")
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
        exclude("**/Database.kt")
        exclude("**/DatabaseImpl.kt")
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

}
