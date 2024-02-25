plugins{
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
  id("expensestracker.android.room")
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