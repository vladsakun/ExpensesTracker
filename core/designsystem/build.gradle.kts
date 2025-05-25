plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  namespace = "com.emendo.expensestracker.core.designsystem"

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  lint {
    checkDependencies = true
  }
  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
  api(projects.appResources)

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