plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
  id("kotlinx-serialization")
}

android {
  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  implementation(projects.feature.transactions.api)

  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
}