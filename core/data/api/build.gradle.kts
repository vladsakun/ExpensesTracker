plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
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