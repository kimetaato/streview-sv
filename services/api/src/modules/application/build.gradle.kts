plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    // utils
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.io.core)
    implementation(libs.kotlinx.serialization.json)

    // トランザクション TODO: 依存関係から取り除く
    implementation(libs.exposed.r2dbc)

    // test
    testImplementation(libs.bundles.kotest.core)
    testImplementation(libs.mockk)

    // モジュールの関連付け
    implementation(project(":modules:domain"))
}

