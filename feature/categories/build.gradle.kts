plugins {
  id("expensestracker.android.feature")
  id("expensestracker.android.library.compose")
}

dependencies {
  implementation(project(":sync:work"))

  implementation(libs.androidx.activity.compose)
}