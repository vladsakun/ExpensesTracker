plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.domain"
}

dependencies {
  implementation(projects.core.data.api)
  implementation(project(":core:common"))
  implementation(project(":core:model"))

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
}