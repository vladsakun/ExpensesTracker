plugins {
  id("expensestracker.android.feature")
  id("expensestracker.android.library.compose")
}

android {
  namespace = "com.emendo.expensestracker.features.create.transaction"
}

dependencies {
  implementation(libs.kotlinx.datetime)
}