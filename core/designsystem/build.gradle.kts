plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.library.compose")
}

android {
  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  lint {
    checkDependencies = true
  }
  namespace = "com.emendo.expensestracker.core.designsystem"
}

dependencies {
  api(project(":core:app-resources"))

  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material.iconsExtended)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.ui.tooling.preview)
  api(libs.androidx.compose.ui.util)
  api(libs.kotlinx.immutable.collections)

  debugApi(libs.androidx.compose.ui.tooling)

  implementation(libs.androidx.core.ktx)
}