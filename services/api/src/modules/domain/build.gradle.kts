plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    // utils
    implementation(libs.kotlinx.cotoutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.result)
    implementation(libs.uuid)

    // test
    testImplementation(libs.bundles.kotest.core)
}
