plugins {
  alias(libs.plugins.expensestracker.android.library)
}

android {
  namespace = "com.emendo.expensestracker.core.app.base.ui.api"
}

dependencies {
  implementation(projects.core.data.api)
  implementation(projects.core.model)
  implementation(projects.appResources)

  implementation(libs.kotlinx.coroutines.android)
}