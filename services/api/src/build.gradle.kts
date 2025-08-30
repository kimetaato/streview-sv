import io.gitlab.arturbosch.detekt.Detekt

// test変更
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt.plugin)
}

tasks.register<JavaExec>("addSampleData") {
    description = "Add sample data to database"
    group = "application"

    // 実行するクラスを指定
    mainClass.set("com.streview.SampleDataGeneratorKt")

    // クラスパスを設定（appモジュールのクラスパスを使用）
    classpath = project("app").sourceSets.main.get().runtimeClasspath
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
        failFast = true
        maxParallelForks = Runtime.getRuntime().availableProcessors()

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
