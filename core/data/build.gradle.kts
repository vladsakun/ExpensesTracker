plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.library.compose")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.data"
}

dependencies {
  api(project(":core:app-resources"))
  implementation(project(":core:model"))
  implementation(project(":core:database"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.androidx.compose.material3)
}