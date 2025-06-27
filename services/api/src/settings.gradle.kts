pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
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

include("app")
include("modules:application")
include("modules:domain")
include("modules:presentation")
include("modules:infrastructure")
