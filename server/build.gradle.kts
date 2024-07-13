plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "org.skynetsoftware.avnlauncher"
version = "1.0.0"

dependencies {
    implementation(project(":domain"))
    implementation(project(":logger"))
    implementation(project(":config"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}