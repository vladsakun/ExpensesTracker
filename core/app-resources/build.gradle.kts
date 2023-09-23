plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.library.compose")
}

android {
  namespace = "com.emendo.expensestracker.core.app.resources"
}

dependencies {
  api(libs.androidx.compose.material.iconsExtended)
  api(libs.timber)

  // Todo consider moving to core:uimodels
  implementation(project(":core:model"))
}