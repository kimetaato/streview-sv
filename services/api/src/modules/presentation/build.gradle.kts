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
    testImplementation(libs.ktor.server.content.negotiation)
    testImplementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.bundles.test.ktor)
    testImplementation(libs.bundles.test.koin)
    testImplementation(libs.mockk)

    // モジュールの関連付け
    implementation(project(":modules:application"))
    implementation(project(":modules:domain"))
}
