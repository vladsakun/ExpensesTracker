plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  namespace = "com.emendo.expensestracker.core.data.api"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  implementation(projects.core.model)
  implementation(projects.core.modelUi)
  implementation(projects.appResources)
  implementation(projects.core.datastore)

  implementation(libs.kotlinx.datetime)
  implementation(libs.paging)
}