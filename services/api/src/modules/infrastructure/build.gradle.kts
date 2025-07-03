plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.r2dbc)
    implementation(libs.jdbc.postgresql)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.r2dbc.pool)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation(libs.kotlinx.io.core)
    implementation(libs.imgscalr)
    implementation(libs.webp.imageio)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.containers)

    implementation(project(":modules:domain"))
    implementation(project(":modules:application"))
    implementation(project(":modules:presentation"))
}