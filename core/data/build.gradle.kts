plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
  id("kotlinx-serialization")
}

android {
  namespace = "com.emendo.expensestracker.core.data"
}

dependencies {
  api(projects.core.data.api)

  // Todo Get rid of compose in data module
  implementation(project(":core:app-resources"))
  implementation(project(":core:common"))
  implementation(project(":core:database"))
  implementation(project(":core:datastore"))
  implementation(project(":core:model"))
  implementation(project(":core:network"))
  implementation(projects.core.androidApi)

  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.paging)
}