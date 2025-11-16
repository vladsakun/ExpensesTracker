plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  buildFeatures {
    androidResources = false
  }
}

dependencies {
  implementation(projects.feature.createTransaction.api)

  api(projects.feature.settings.api)
}
