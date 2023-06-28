plugins {
  id("expensestracker.android.feature")
  id("expensestracker.android.library.compose")
}

ksp {
  arg("compose-destinations.mode", "navgraphs")
  arg("compose-destinations.moduleName", "transactions")
  arg("compose-destinations.useComposableVisibility", "true")
}

android {
  namespace = "com.emendo.expensestracker.feature.transactions"
}

dependencies {
  implementation(libs.androidx.activity.compose)
}