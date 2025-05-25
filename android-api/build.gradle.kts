plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.android.api"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}