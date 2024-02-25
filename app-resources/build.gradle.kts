plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.library.compose")
}

android {
  namespace = "com.emendo.expensestracker.core.app.resources"
}

dependencies {
  api(libs.timber)
  implementation(libs.androidx.compose.material.iconsExtended)

  // Todo consider moving to core:uimodels
  implementation(projects.core.model)
  implementation(libs.androidx.compose.foundation)
}