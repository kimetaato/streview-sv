plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.io.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.exposed.r2dbc)

    implementation(libs.kotest.assertions.core)
    implementation(libs.kotest.runner.junit5)

    implementation(project(":modules:domain"))
}
