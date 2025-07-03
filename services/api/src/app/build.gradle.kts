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
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.firebase)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)

    // jsonシリアライズ
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.kotlinx.serialization.json)

    // application.confをyaml形式記述するため
    implementation(libs.ktor.server.config.yaml)

    // モジュールの関連付け
    implementation(project(":modules:presentation"))
    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))
    implementation(project(":modules:infrastructure"))

    // kotest
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

// テスト実行時にJUnit 5を使用する
tasks.withType<Test> {
    useJUnitPlatform()
}
