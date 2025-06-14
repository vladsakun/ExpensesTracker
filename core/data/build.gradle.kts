plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
  id("kotlinx-serialization")
}

android {
  namespace = "com.emendo.expensestracker.core.data"

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(projects.core.data.api)

  implementation(projects.appResources)
  implementation(projects.core.common)
  implementation(projects.core.database)
  implementation(projects.core.datastore)
  implementation(projects.core.model)
  implementation(projects.core.modelUi)
  implementation(projects.core.network)
  implementation(projects.androidApi)

  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.paging)
}