plugins {
  id("expensestracker.android.library") // Todo refactor to use alias
}

android {
  namespace = "com.emendo.expensestracker.feature.categories.api"
}
dependencies {
  implementation(project(":core:data:api"))
  implementation(project(":core:model"))
}
