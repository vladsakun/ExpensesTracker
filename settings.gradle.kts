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
  }
}

rootProject.name = "expensestracker"
include(":app")
include(":core:app-resources")
include(":core:data")
include(":core:common")
include(":core:designsystem")
include(":core:database")
include(":core:model")
include(":core:ui")

include(":feature:accounts")
include(":feature:categories")
include(":feature:transactions")
