pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HypeList"
include(":hype-app")
include(":hype-domain")
include(":hype-data")
include(":hype-entities")
include(":hype-presentation")
include(":hype-resources")
include(":hype-architecture")
