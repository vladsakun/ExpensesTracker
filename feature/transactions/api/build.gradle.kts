plugins {
  alias(libs.plugins.expensestracker.android.library)
  id("kotlinx-serialization")
}

android {
  namespace = "com.emendo.expensestracker.feature.transactions.api"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(libs.kotlinx.datetime)
  api(libs.kotlinx.serialization.json)

  implementation(projects.core.model)
}