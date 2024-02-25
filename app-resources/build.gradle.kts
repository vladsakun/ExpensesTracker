plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  namespace = "com.emendo.expensestracker.app.resources"
}

dependencies {
  api(libs.timber)
  implementation(libs.androidx.compose.material.iconsExtended)
}