plugins {
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)


    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)

    alias(libs.plugins.jetbrains.compose).apply(false)

    alias(libs.plugins.realm.kotlin).apply(false)
    alias(libs.plugins.ktor.plugin).apply(false)
    alias(libs.plugins.ktlint.plugin).apply(false)
    alias(libs.plugins.moko.resource.generator).apply(false)
    alias(libs.plugins.kover).apply(false)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.1.0")
        debug.set(true)
        filter {
            exclude("**/build.gradle.kts")
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all { kotlinOptions.allWarningsAsErrors = true }
}
