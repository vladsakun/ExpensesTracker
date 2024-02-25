plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  implementation(project(":sync:work"))

  implementation(libs.androidx.activity.compose)
}