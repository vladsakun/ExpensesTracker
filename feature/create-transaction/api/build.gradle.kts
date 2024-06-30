plugins {
  //  alias(libs.plugins.expensestracker.android.library)
  id("expensestracker.android.library") // Todo refactor to use alias
}

android {
  namespace = "com.emendo.expensestracker.feature.create.transaction.api"
}
dependencies {
  implementation(project(":core:data:api"))
  implementation(project(":core:model"))
}
