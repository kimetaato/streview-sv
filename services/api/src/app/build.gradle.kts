plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("application")
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // Ktor
    implementation(libs.bundles.ktor.core)
    implementation(libs.bundles.ktor.utils)
    implementation(libs.bundles.ktor.auth)

    // swagger
    implementation(libs.ktor.server.swagger)

    // ORM
    implementation(libs.bundles.exposed.core)
    implementation(libs.bundles.exposed.r2dbc)
    implementation(libs.jdbc.postgresql) // 初期化時に内部で参照されるため

    // DIモジュール
    implementation(libs.koin.ktor)

    // kotest
    testImplementation(libs.bundles.kotest.core)
    testImplementation(libs.bundles.test.ktor)
    testImplementation(libs.bundles.test.koin)

    // モジュールの関連付け
    implementation(project(":modules:presentation"))
    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))
    implementation(project(":modules:infrastructure"))
}

