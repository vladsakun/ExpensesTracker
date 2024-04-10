plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  namespace = "com.emendo.expensestracker.features.create.transaction"
}

dependencies {
  implementation(projects.feature.createTransaction.api)

  implementation(libs.kotlinx.datetime)
  implementation(projects.feature.accounts.api)
  implementation(projects.feature.categories.api)
}