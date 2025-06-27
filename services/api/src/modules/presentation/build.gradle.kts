plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))
}