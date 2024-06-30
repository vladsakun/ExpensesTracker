plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  implementation(libs.kotlinx.datetime)
}