plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.app.common"
}

dependencies {
  api(libs.timber)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.ktx)
  implementation(libs.kotlinx.serialization.json)
}