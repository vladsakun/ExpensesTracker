plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
  id("kotlinx-serialization")
}

android {
  buildFeatures {
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
  namespace = "com.emendo.expensestracker.core.network"
}

dependencies {
  implementation(project(":core:common"))
  implementation(project(":core:model"))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp.logging)
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.kotlin.serialization)
}