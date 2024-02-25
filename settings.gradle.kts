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
include(":core:model")
include(":core:ui")
include(":android-api")

include(":feature:accounts")
include(":feature:categories")
include(":feature:transactions")
include(":feature:settings")
include(":feature:create-transaction")
include(":sync:work")
include(":sync:sync-test")
include(":benchmark")
include(":baselineprofile")
