plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  namespace = "com.emendo.expensestracker.features.create.transaction"
}

dependencies {
  implementation(libs.kotlinx.datetime)
}