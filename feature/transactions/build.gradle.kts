plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  api(projects.feature.transactions.api)
  implementation(projects.feature.createTransaction.api)

  implementation(libs.androidx.activity.compose)
  implementation(libs.paging.compose)
}