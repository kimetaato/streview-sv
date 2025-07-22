import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt.plugin)
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    afterEvaluate {
        dependencies {
            detektPlugins(libs.detekt.formatting)
        }
    }

    tasks.withType<Detekt>().configureEach {
        if (name != "detektAll") {
            enabled = false
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = false
        }
        systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
    }
}

if (project == rootProject) {
    val detektAll by tasks.registering(Detekt::class) {
        description = "全プロジェクトのdetekt結果を統合したレポートを生成"
        parallel = true
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true

        // Configuration時にパスを解決（実行時ではなく）
        val configFile = file("$rootDir/config/detekt.yml")
        val sourceFiles = files(allprojects.map { "${it.projectDir}/src" })
        val reportsDirectory = file("$rootDir/build/reports/detekt")

        config.setFrom(configFile)
        setSource(sourceFiles)
        reportsDir = reportsDirectory

        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
            md.required.set(true)
        }

        exclude("**/generated/**")
        exclude("**/build/**")
    }

    tasks.named("detekt") {
        dependsOn(detektAll)
    }
}