plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  implementation(projects.sync.work)
  implementation(projects.feature.createTransaction.api)

  implementation(libs.androidx.activity.compose)
}