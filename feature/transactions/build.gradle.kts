plugins {
  id("expensestracker.android.feature")
  id("expensestracker.android.library.compose")
}

dependencies {
  implementation(libs.androidx.activity.compose)
  implementation(libs.paging.compose)
  implementation(libs.kotlinx.datetime)
}