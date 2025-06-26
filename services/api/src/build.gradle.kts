plugins {
    alias(libs.plugins.kotlin.jvm) apply false // kotlin("jvm") version "2.1.10"
    alias(libs.plugins.kotlin.plugin.serialization) apply false
    alias(libs.plugins.ktor) apply false
    id("idea")
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}