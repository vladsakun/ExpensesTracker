plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.library.compose)
  id("kotlinx-serialization")
}

android {
  namespace = "com.emendo.expensestracker.core.model.ui"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  implementation(projects.core.model)

  implementation(libs.androidx.compose.foundation)
  implementation(libs.kotlinx.serialization.json)
}