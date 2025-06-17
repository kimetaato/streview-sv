plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(project(":modules:usecase"))
    implementation(project(":modules:domain"))
}