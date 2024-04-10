plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  api(projects.feature.categories.api)
  implementation(projects.sync.work)
  implementation(projects.feature.createTransaction.api)

  implementation(libs.androidx.activity.compose)
}