plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.data.api"
}

dependencies {
  implementation(projects.core.model)
  implementation(projects.appResources)
  implementation(projects.core.datastore)

  implementation(libs.kotlinx.datetime)
  implementation(libs.paging)
}