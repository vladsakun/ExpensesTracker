plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.domain"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  implementation(projects.core.data.api)
  implementation(projects.core.common)
  implementation(projects.core.model)
  implementation(projects.core.modelUi)
  implementation(projects.appResources)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
}