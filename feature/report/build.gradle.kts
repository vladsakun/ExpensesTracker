plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
  id("kotlinx-serialization")
}

dependencies {
  implementation(projects.feature.transactions.api)

  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
}