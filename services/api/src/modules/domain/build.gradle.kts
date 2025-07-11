plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    // utils
    implementation(libs.kotlinx.cotoutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.uuid)

    // test
    testImplementation(libs.bundles.kotest.core)
}

// テスト実行時にJUnit 5を使用する
tasks.withType<Test> {
    useJUnitPlatform()
}

