plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
  id("kotlinx-serialization")
}

dependencies {
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
}