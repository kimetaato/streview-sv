plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("application")
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // DIモジュール
    implementation(libs.koin.ktor)

    // ORM
    implementation(libs.exposed.core)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.r2dbc)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.r2dbc.pool)
    implementation(libs.jdbc.postgresql)

    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.status.page)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    // jsonシリアライズ
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)

    // application.confをyaml形式記述するため
    implementation(libs.ktor.server.config.yaml)

    implementation(project(":modules:presentation"))
    implementation(project(":modules:usecase"))
    implementation(project(":modules:domain"))
    implementation(project(":modules:infrastructure"))

}