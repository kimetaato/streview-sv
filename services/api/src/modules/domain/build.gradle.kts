plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.uuid)
    implementation(libs.kotlin.result)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.datetime)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // kotest
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

// テスト実行時にJUnit 5を使用する
tasks.withType<Test> {
    useJUnitPlatform()
}

