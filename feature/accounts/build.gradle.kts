plugins {
  id("expensestracker.android.feature")
  id("expensestracker.android.library.compose")
}

android {
  namespace = "com.emendo.expensestracker.feature.accounts"
}

dependencies {
  implementation(libs.androidx.activity.compose)
}