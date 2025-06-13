pluginManagement {
    includeBuild("build-logic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        // 他の必要なリポジトリをここに追加
    }
}

rootProject.name = "streview"

include(":infrastructure")
include(":presentation")
include(":usecase")
include(":domain")
include(":common")
