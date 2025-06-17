plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.r2dbc.pool)

    implementation(project(":modules:domain"))
    implementation(project(":modules:usecase"))
    implementation(project(":modules:presentation"))
}