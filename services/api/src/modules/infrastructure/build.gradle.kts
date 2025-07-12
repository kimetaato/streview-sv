plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // utils
    implementation(libs.kotlinx.cotoutines)
    implementation(libs.kotlinx.io.core)

    // ORM
    implementation(libs.bundles.exposed.core)
    implementation(libs.bundles.exposed.r2dbc)

    // 画像
    implementation(libs.bundles.image.converter)

    // test
    testImplementation(libs.bundles.kotest.core)
    testImplementation(libs.kotest.containers)
    testImplementation(libs.jdbc.postgresql)    // テストコンテナをマイグレーションするときに必要

    // モジュールの関連付け
    implementation(project(":modules:domain"))
    implementation(project(":modules:application"))
    implementation(project(":modules:presentation"))
}

// テスト実行時にJUnit 5を使用する
tasks.withType<Test> {
    useJUnitPlatform()
}
