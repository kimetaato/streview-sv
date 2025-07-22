plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

}

dependencies {
    // utils
    implementation(libs.kotlinx.serialization.json)

    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)

    // test
    testImplementation(libs.bundles.kotest.core)

    // モジュールの関連付け
    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))
}

