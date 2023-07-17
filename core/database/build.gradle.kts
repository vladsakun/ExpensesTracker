plugins{
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
  id("expensestracker.android.room")
}

android {
  namespace = "com.emendo.expensestracker.core.database"
}

dependencies {
  implementation(project(":core:model"))

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
}