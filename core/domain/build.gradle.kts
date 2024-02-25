plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.domain"
}

dependencies {
  implementation(projects.core.data.api)
  implementation(projects.core.common)
  implementation(projects.core.model)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
}