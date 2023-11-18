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
include(":app")
include(":core:app-resources")
include(":core:app-base")
include(":core:app-base-ui")
include(":core:data")
include(":core:common")
include(":core:domain")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:model")
include(":core:ui")

include(":feature:accounts")
include(":feature:categories")
include(":feature:transactions")
include(":feature:settings")
include(":sync:work")
include(":sync:sync-test")
