pluginManagement {
    includeBuild("build-logic")
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
        maven("https://jitpack.io")
    }
}

rootProject.name = "expensestracker"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":app-resources")
include(":app-base-ui", "app-base-ui:api")
include(":core:data", ":core:data:api")
include(":core:common")
include(":core:domain")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:model", ":core:model-ui")
include(":core:ui")
include(":android-api")

include(":feature:accounts", ":feature:accounts:api")
include(":feature:categories", ":feature:categories:api")
include(":feature:transactions")
include(":feature:user-settings")
include(":feature:create-transaction", ":feature:create-transaction:api")
include(":sync:work")
include(":sync:sync-test")
include(":baselineprofile")
