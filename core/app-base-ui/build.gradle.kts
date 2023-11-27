plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.hilt")
}

android {
  namespace = "com.emendo.expensestracker.core.app.base.ui"
}

dependencies {
  implementation(project(":core:data"))
  implementation(project(":core:domain"))
  implementation(project(":core:common"))
}