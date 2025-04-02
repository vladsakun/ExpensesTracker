plugins {
  alias(libs.plugins.expensestracker.android.library)
  id("kotlinx-serialization")
}

android {
  namespace = "com.emendo.expensestracker.feature.transactions.api"
}

dependencies {
  api(libs.kotlinx.datetime)
  api(libs.kotlinx.serialization.json)

  implementation(projects.core.model)
}