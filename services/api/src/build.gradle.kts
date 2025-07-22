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

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true

        config.setFrom("$rootDir/config/detekt.yml")
        // サブプロジェクトのレポートディレクトリ（個別レポート用）
        reportsDir = layout.buildDirectory.dir("reports/detekt").get().asFile
    }

    tasks.withType<Detekt>().configureEach {
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

    tasks.withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = false
        }

        systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
    }
}

// ルートプロジェクトでのみ統合レポートタスクを作成
if (project == rootProject) {
    val detektAll by tasks.registering(Detekt::class) {
        description = "全プロジェクトのdetekt結果を統合したレポートを生成"
        parallel = true
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true

        config.setFrom("$rootDir/config/detekt.yml")

        // 全サブプロジェクトのソースを対象に含める
        setSource(files(allprojects.map { "${it.projectDir}/src" }))

        // 統合レポートの出力先
        reportsDir = file("$rootDir/build/reports/detekt")

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

    // 既存のdetektタスクに依存を追加（オプション）
    tasks.named("detekt") {
        finalizedBy(detektAll)
    }
}