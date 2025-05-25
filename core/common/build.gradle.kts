plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.app.common"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(libs.timber)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.ktx)
  implementation(libs.kotlinx.serialization.json)
}