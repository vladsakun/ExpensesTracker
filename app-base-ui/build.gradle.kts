import com.emendo.expensestracker.composeDestinations

plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.library.compose)
  alias(libs.plugins.expensestracker.android.hilt)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.emendo.expensestracker.core.app.base.ui"

  composeDestinations("app.base.ui")

  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(projects.appBaseUi.api)

  implementation(projects.core.data.api)
  implementation(projects.core.domain)
  implementation(projects.core.common)
  implementation(projects.core.ui)
  implementation(projects.core.model)
  implementation(projects.core.designsystem)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.coil.kt)
  implementation(libs.coil.kt.compose)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.immutable.collections)
  implementation(libs.compose.destinations)

  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.compose.destinations.ksp)
}