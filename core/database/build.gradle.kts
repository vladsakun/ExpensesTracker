plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
  alias(libs.plugins.expensestracker.android.room)
}

android {
  namespace = "com.emendo.expensestracker.core.database"
}

dependencies {
  implementation(projects.core.model)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.paging)
}