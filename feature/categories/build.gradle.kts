plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(projects.feature.categories.api)
  implementation(projects.sync.work)
  implementation(projects.feature.createTransaction.api)
  implementation(projects.feature.settings.api)

  implementation(libs.androidx.activity.compose)
  implementation(libs.kotlinx.datetime)
}
