plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.app.common"
}

dependencies {
  api(libs.timber)
  implementation(project(":core:model"))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.lifecycle.ktx)
}