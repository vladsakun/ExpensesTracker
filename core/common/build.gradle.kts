plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.app.common"
}

dependencies {
  api(libs.timber)
  implementation(libs.kotlinx.coroutines.android)
}