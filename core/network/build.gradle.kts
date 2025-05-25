plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
  id("kotlinx-serialization")
}

android {
  buildFeatures {
    buildConfig = true
    androidResources = false
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
  namespace = "com.emendo.expensestracker.core.network"
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.model)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp.logging)
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.kotlin.serialization)
}